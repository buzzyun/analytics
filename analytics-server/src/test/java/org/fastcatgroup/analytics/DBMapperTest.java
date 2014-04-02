package org.fastcatgroup.analytics;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.fastcatgroup.analytics.db.CommonDBHandler;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.db.vo.SearchKeywordHitVO;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class DBMapperTest {
	Logger logger = LoggerFactory.getLogger(DBMapperTest.class);

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String dbUrl = "jdbc:derby:/tmp/idbtest;create=true";
	private String dbUser = "sa";
	private String dbPass = "";
	private String dbms = "";
	private CommonDBHandler dbModule;

	@Before
	public void init() {
		String LOG_LEVEL = System.getProperty("LOG_LEVEL");

		if (LOG_LEVEL == null || "".equals(LOG_LEVEL)) {
			LOG_LEVEL = "DEBUG";
		}

		((ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME))
				.setLevel(Level.toLevel("DEBUG"));

		((ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(DBMapperTest.class))
				.setLevel(Level.toLevel("DEBUG"));

		if (System.getProperty("JDBC_DRIVER") != null) {
			driver = System.getProperty("JDBC_DRIVER");
		}
		if (System.getProperty("JDBC_URL") != null) {
			dbUrl = System.getProperty("JDBC_URL");
		}
		if (System.getProperty("JDBC_USER") != null) {
			dbUser = System.getProperty("JDBC_USER");
		}
		if (System.getProperty("JDBC_PASS") != null) {
			dbPass = System.getProperty("JDBC_PASS");
		}
		if (System.getProperty("JDBC_DBMS") != null) {
			dbms = System.getProperty("JDBC_DBMS");
		}
	}

	public SqlSession initDb(String driver, String dbUrl, String dbUser,
			String dbPass, Class[] classList) throws Exception {
		
		Map<String,Object> globalParam = new HashMap<String,Object>();
		globalParam.put("DBMS", dbms);
		
		dbModule = new CommonDBHandler(null, classList);
		dbModule.load();
		SqlSession session = dbModule.openSession();
	
		logger.debug("dbms : {}", session.getConfiguration().getVariables().get("DBMS"));
		
		return session;
	}

	public void testModules(String site, String category, String stype) throws Exception {
		Class[] classList = new Class[] {
			SearchHitMapper.class,
			SearchKeywordHitMapper.class,
			SearchTypeHitMapper.class
			};
		
		CommonDBHandler dbModule = null;
		SqlSession session = null;
		
		try {
		
			session = initDb(driver, dbUrl, dbUser, dbPass, classList);
			
			for(Class<?> clazz : classList) {
				
				Object obj = session.getMapper(clazz);
				
				if(obj instanceof SearchHitMapper) {
					SearchHitMapper mapper = (SearchHitMapper)obj;
					SearchHitVO vo;
					List<SearchHitVO> list;
					try {
						mapper.createTable(site);
						mapper.createIndex(site);
						mapper.validateTable(site);
						//입력자료 설정
						mapper.putEntry(site, category, "m201311", 200, 100, 10);
						mapper.putEntry(site, category, "d20131225", 1, 100, 10);
						mapper.putEntry(site, category, "m201312", 100, 100, 10);
						mapper.putEntry(site, category, "d20131226", 2, 100, 10);
						mapper.putEntry(site, category, "d20131227", 3, 100, 10);
						mapper.putEntry(site, category, "d20131228", 4, 100, 10);
						mapper.putEntry(site, category, "d20131229", 5, 100, 10);
						mapper.putEntry(site, category, "d20131230", 6, 100, 10);
						mapper.putEntry(site, category, "d20131231", 7, 100, 10);
						session.commit();
						
						//최古 항목
						vo = mapper.getMinEntry(site, category, "d");
						assertEquals("d20131225", vo.getTimeId());
						
						//최근 항목
						vo = mapper.getMaxEntry(site, category, "d");
						assertEquals("d20131231", vo.getTimeId());
						
						vo = mapper.getMaxEntry(site, category, "m");
						assertEquals("m201312", vo.getTimeId());
						
						//범위내 항목 덤프
						list = null;//mapper.getEntryListBetween(site, category, "D20131226", "D20131227");
						for(SearchHitVO entry : list) {
							logger.debug("entry : {}", entry.getTimeId());
						}
						
						//범위내 항목 덤프 (날자타입)
						list = null;//mapper.getEntryListBetween(site, category, null, null);
						for(SearchHitVO entry : list) {
							logger.debug("entry : {}", entry.getTimeId());
						}
						
						//범위내 항목 갯수
						int count = mapper.getCountBetween(site, category, "d", "d20131226", "d20131230");
						assertEquals(count,5);
						
						//범위내 조회수 합
						int sum = mapper.getSumBetween(site, category, "d20131226", "d20131231");
						assertEquals(sum,2+3+4+5+6+7);
						
					} finally {
						mapper.dropTable(site);
						session.commit();
					}
				} else if(obj instanceof SearchKeywordHitMapper) {
					SearchKeywordHitMapper mapper = (SearchKeywordHitMapper)obj;
					SearchKeywordHitVO vo;
					List<SearchKeywordHitVO> list;
					List<String> keywordList;
					try {
						mapper.createTable(site);
						mapper.createIndex(site);
						mapper.validateTable(site);
						
						mapper.putEntry(site, category, "d20131101", "조아라", 5);
						mapper.putEntry(site, category, "d20131101", "테스트", 5);
						mapper.putEntry(site, category, "d20131201", "테스트", 5);
						mapper.putEntry(site, category, "d20131201", "키워드", 6);
						mapper.putEntry(site, category, "d20131201", "마스터", 7);
						mapper.putEntry(site, category, "d20131202", "드래곤", 8);
						mapper.putEntry(site, category, "d20131203", "키워드", 9);
						mapper.putEntry(site, category, "d20131203", "노트북", 10);
						mapper.putEntry(site, category, "d20131204", "노트북", 10);
						mapper.putEntry(site, category, "m201312", "키워드", 11);
						mapper.putEntry(site, category, "m201313", "키워드", 12);
						session.commit();
						
//						vo = mapper.getMinEntry(site, category, "d", "키워드");
//						assertEquals(vo.getTimeId(), "d20131201");
//						vo = mapper.getMaxEntry(site, category, "d", "키워드");
//						assertEquals(vo.getTimeId(), "d20131203");
//						
//						vo = mapper.getMinEntry(site, category, "d", "");
//						assertEquals(vo.getTimeId(), "d20131101");
//						vo = mapper.getMaxEntry(site, category, "d", "");
//						assertEquals(vo.getTimeId(), "d20131204");
						
//						keywordList = mapper.searchKeyword(site, category, "", "d20131202", "d20131204");
//						for(String keyword : keywordList) {
//							logger.debug("keyword : {}", keyword);
//						}
//						
//						keywordList = mapper.searchKeyword(site, category, "", "", "");
//						for(String keyword : keywordList) {
//							logger.debug("keyword : {}", keyword);
//						}
//						
//						keywordList = mapper.searchKeyword(site, category, "노트", "", "");
//						for(String keyword : keywordList) {
//							logger.debug("keyword : {}", keyword);
//							assertEquals(keyword,"노트북");
//						}
//						
//						list = mapper.getEntryListBetween(site, category, "d", "", "d20131202", "d20131204", false);
//						for(SearchKeywordHitVO entry : list) {
//							logger.debug("entry : {} / {}", entry.getTimeId(), entry.getKeyword());
//						}
//						
//						list = mapper.getEntryListBetween(site, category, "d", "노트북", "", "", false);
//						for(SearchKeywordHitVO entry : list) {
//							logger.debug("entry : {} / {}", entry.getTimeId(), entry.getKeyword());
//						}
//						
//						list = mapper.getEntryListBetween(site, category, "d", "노트북", "", "", true);
//						for(SearchKeywordHitVO entry : list) {
//							logger.debug("entry : {} / {}", entry.getTimeId(), entry.getKeyword());
//						}
						
//						int count = mapper.getCountBetween(site, category, "m", "키워드", "", "");
//						assertEquals(count, 2);
//						
//						count = mapper.getCountBetween(site, category, "d", "", "d20131101", "d20131204");
//						assertEquals(count, 9);
//						
//						int sum = mapper.getSumBetween(site, category, "키워드", "d20131101", "d20131204");
//						assertEquals(sum, 15);
						
					} finally {
						mapper.dropTable(site);
						session.commit();
					}
				} else if(obj instanceof SearchTypeHitMapper) {
					SearchTypeHitMapper mapper = (SearchTypeHitMapper)obj;
					SearchTypeHitVO vo;
					List<SearchTypeHitVO> list;
					List<String> typeList;
					try {
						mapper.createTable(site);
						mapper.createIndex(site);
						mapper.validateTable(site);
						
						mapper.putEntry(site, category, stype, "prod_main", "d20131201", 100);
						mapper.putEntry(site, category, stype, "prod_list", "d20131201", 100);
						mapper.putEntry(site, category, stype, "prod_detail", "d20131201", 500);
						mapper.putEntry(site, category, stype, "prod_main", "d20131202", 80);
						mapper.putEntry(site, category, stype, "prod_list", "d20131202", 80);
						mapper.putEntry(site, category, stype, "prod_detail", "d20131202", 500);
						mapper.putEntry(site, category, stype, "prod_detail", "d20131203", 800);
						mapper.putEntry(site, category, stype, "prod_event", "d20131204", 800);
						
//						vo = mapper.getMinEntry(site, category, stype, "d", "prod_main");
//						assertEquals(vo.getTimeId(), "d20131201");
//						vo = mapper.getMaxEntry(site, category, stype, "d", "prod_main");
//						assertEquals(vo.getTimeId(), "d20131202");
//						vo = mapper.getMaxEntry(site, category, stype, "d", "prod_detail");
//						assertEquals(vo.getTimeId(), "d20131203");
//						
//						typeList = mapper.listTypes(site, category, stype, "", "");
//						
//						for(String type : typeList) {
//							logger.debug("type : {}", type);
//						}
//						
//						list = mapper.getEntryListBetween(site, category, stype, "d", "prod_main", "", "", false);
//						for(SearchTypeHitVO entry: list) {
//							logger.debug("entry : {}/{}", entry.getTimeId(), entry.getDtype());
//						}
//						
//						list = mapper.getEntryListBetween(site, category, stype, "d", "", "d20131201", "d20131204", false);
//						for(SearchTypeHitVO entry: list) {
//							logger.debug("entry : {}/{}", entry.getTimeId(), entry.getDtype());
//						}
//						
//						int count = mapper.getCountBetween(site, category, stype, "d", "prod_event", "d20131201", "d20131204");
//						assertEquals(count,1);
//						
//						int sum = mapper.getSumBetween(site, category, stype, "d", "prod_main", "d20131201", "d20131204");
//						assertEquals(sum,180);
						
					} finally {
						mapper.dropTable(site);
						session.commit();
					}
				}
				
				logger.debug("obj : {}", obj);
			}
		} finally {
			
			if (session != null) {
				session.close();
			}
			
			if (dbModule != null) {
				dbModule.unload();
			}
		}
	}

	@Test
	public void testBySiteAndCategory() throws Exception {

		String site, category, stype;

		site = "www";
		category = "01";
		stype = "product";

		testModules(site, category, stype);
	}
}

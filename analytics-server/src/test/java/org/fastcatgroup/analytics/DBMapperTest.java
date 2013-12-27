package org.fastcatgroup.analytics;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.fastcatgroup.analytics.db.CommonDBModule;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchTypeRatioMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
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
	private CommonDBModule dbModule;

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
			String dbPass, String[] classList) throws Exception {
		
		List<URL> mapperFileList = new ArrayList<URL>();
		for(String path : classList) {
			path = path.replaceAll("[.]", "/")+".xml";
			mapperFileList.add(Resources.getResourceURL(path));
		}
		
		Map<String,Object> globalParam = new HashMap<String,Object>();
		globalParam.put("DBMS", dbms);
		
		dbModule = new CommonDBModule(driver, dbUrl,
				dbUser, dbPass, globalParam, mapperFileList, null, null, null);
		dbModule.load();
		SqlSession session = dbModule.openSession();
	
		logger.debug("dbms : {}", session.getConfiguration().getVariables().get("DBMS"));
		
		return session;
	}

	public void testModules(String site, String category, String stype) throws Exception {
		String[] classList = new String[] {
			SearchHitMapper.class.getName(),
			SearchKeywordHitMapper.class.getName(),
			SearchTypeRatioMapper.class.getName()
			};
		
		CommonDBModule dbModule = null;
		SqlSession session = null;
		
		try {
		
			session = initDb(driver, dbUrl, dbUser, dbPass, classList);
			
			for(String classStr : classList) {
				
				Object obj = session.getMapper(Class.forName(classStr));
				
				if(obj instanceof SearchHitMapper) {
					SearchHitMapper mapper = (SearchHitMapper)obj;
					SearchHitVO vo;
					List<SearchHitVO> list;
					try {
						mapper.createTable(site, category);
						mapper.createIndex(site, category);
						mapper.validateTable(site, category);
						mapper.putEntry(site, category, "d20131225", 1);
						mapper.putEntry(site, category, "m201312", 100);
						mapper.putEntry(site, category, "d20131226", 2);
						mapper.putEntry(site, category, "d20131227", 3);
						mapper.putEntry(site, category, "d20131228", 4);
						mapper.putEntry(site, category, "d20131229", 5);
						mapper.putEntry(site, category, "d20131230", 6);
						mapper.putEntry(site, category, "d20131231", 7);
						vo = mapper.getMinEntry(site, category, "d");
						assertEquals("d20131225", vo.getTimeId());
						
						vo = mapper.getMaxEntry(site, category, "d");
						assertEquals("d20131231", vo.getTimeId());
						
						vo = mapper.getMaxEntry(site, category, "m");
						assertEquals("m201312", vo.getTimeId());
						
						list = mapper.getEntryListBetween(site, category, "d", "d20131226", "d20131227");
						for(SearchHitVO entry : list) {
							logger.debug("entry : {}", entry.getTimeId());
						}
						
						list = mapper.getEntryListBetween(site, category, "d", null, null);
						for(SearchHitVO entry : list) {
							logger.debug("entry : {}", entry.getTimeId());
						}
						
						int count = mapper.getCountBetween(site, category, "d", "d20131226", "d20131230");
						assertEquals(count,5);
						
						int sum = mapper.getSumBetween(site, category, "d20131226", "d20131231");
						assertEquals(sum,2+3+4+5+6+7);
						
					} finally {
						mapper.dropTable(site, category);
					}
				} else if(obj instanceof SearchKeywordHitMapper) {
//					SearchKeywordHitMapper mapper = (SearchKeywordHitMapper)obj;
//					try {
//						mapper.createTable(site, category);
//						mapper.createIndex(site, category);
//						mapper.validateTable(site, category);
//					} finally {
//						mapper.dropTable(site, category);
//					}
//				} else if(obj instanceof SearchTypeRatioMapper) {
//					SearchTypeRatioMapper mapper = (SearchTypeRatioMapper)obj;
//					try {
//						mapper.createTable(site, category, stype);
//						mapper.createIndex(site, category, stype);
//						mapper.validateTable(site, category, stype);
//					} finally {
//						mapper.dropTable(site, category, stype);
//					}
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
		stype = "main";

		testModules(site, category, stype);
	}
}

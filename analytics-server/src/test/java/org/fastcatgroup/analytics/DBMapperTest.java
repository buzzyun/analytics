package org.fastcatgroup.analytics;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.fastcatgroup.analytics.db.CommonDBModule;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchTypeRatioMapper;
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
			driver = System.getProperty("JDBC_URL");
		}
		if (System.getProperty("JDBC_USER") != null) {
			driver = System.getProperty("JDBC_USER");
		}
		if (System.getProperty("JDBC_PASS") != null) {
			driver = System.getProperty("JDBC_PASS");
		}
	}

	public CommonDBModule initDb(String driver, String dbUrl, String dbUser,
			String dbPass, List<URL> mapperFileList) {
		CommonDBModule externalDBModule = new CommonDBModule(driver, dbUrl,
				dbUser, dbPass, mapperFileList, null, null, null);
		externalDBModule.load();
		return externalDBModule;
	}

	@Test
	public void testTemp() throws Exception {
		String mapperFilePath = "org/fastcatgroup/analytics/db/mapper/SearchHitMapper.xml";
		URL mapperFile = Resources.getResourceURL(mapperFilePath);
		List<URL> mapperFileList = new ArrayList<URL>();
		mapperFileList.add(mapperFile);
		// 디비를 열고 닫고 여러번가능한지..
		for (int i = 0; i < 3; i++) {

			String site = "main";
			String category = "";

			CommonDBModule dbModule = initDb(driver, dbUrl, dbUser, dbPass,
					mapperFileList);

			SqlSession session = dbModule.openSession();
			SearchHitMapper mapper = session.getMapper(SearchHitMapper.class);
			try {
				mapper.createTable(site, category);
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
			try {
				mapper.createIndex(site, category);
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
			try {
				mapper.dropTable(site, category);
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
			session.commit();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error("{}", e.getMessage());
			}
			dbModule.unload();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error("{}", e.getMessage());
			}
		}
		assertTrue(true);
	}

	public void testModules(String site, String category, String stype) throws Exception {
		String[] classList = new String[] {
			SearchHitMapper.class.getName(),
			SearchKeywordHitMapper.class.getName(),
			SearchTypeRatioMapper.class.getName()
			};
		
		List<URL> mapperFileList = new ArrayList<URL>();
		for(String path : classList) {
			path = path.replaceAll("[.]", "/")+".xml";
			mapperFileList.add(Resources.getResourceURL(path));
		}
		
		CommonDBModule dbModule = null;
		
		try {
		
			dbModule = initDb(driver, dbUrl, dbUser, dbPass, mapperFileList);
			
			SqlSession session = dbModule.openSession();
			
			for(String classStr : classList) {
				
				Object obj = session.getMapper(Class.forName(classStr));
				
				if(obj instanceof SearchHitMapper) {
					SearchHitMapper mapper = (SearchHitMapper)obj;
					mapper.createTable(site, category);
					mapper.createIndex(site, category);
					mapper.validateTable(site, category);
					mapper.dropTable(site, category);
				} else if(obj instanceof SearchKeywordHitMapper) {
					SearchKeywordHitMapper mapper = (SearchKeywordHitMapper)obj;
					mapper.createTable(site, category);
					mapper.createIndex(site, category);
					mapper.validateTable(site, category);
					mapper.dropTable(site, category);
				} else if(obj instanceof SearchTypeRatioMapper) {
					SearchTypeRatioMapper mapper = (SearchTypeRatioMapper)obj;
					mapper.createTable(site, category, stype);
					mapper.createIndex(site, category, stype);
					mapper.validateTable(site, category, stype);
					mapper.dropTable(site, category, stype);
				}
				
				logger.debug("obj : {}", obj);
			}
		} finally {
			
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

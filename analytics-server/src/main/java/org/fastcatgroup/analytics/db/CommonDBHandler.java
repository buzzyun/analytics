/*
 * Copyright (c) 2013 Websquared, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     swsong - initial API and implementation
 */

package org.fastcatgroup.analytics.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.fastcatgroup.analytics.env.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDBHandler {
	private static Logger logger = LoggerFactory.getLogger(CommonDBHandler.class);

	private Settings settings;
	private Map<String, Object> globalParam;

	private Class<?>[] mapperList;

	private SqlSessionFactory sqlSessionFactory;
	private Properties driverProperties;

	public CommonDBHandler(Settings settings, Class<?>[] mapperList) {
		this(settings, mapperList, null, null);
	}

	public CommonDBHandler(Settings settings, Class<?>[] mapperList, Properties driverProperties, Map<String, Object> globalParam) {
		this.settings = settings;
		this.driverProperties = driverProperties;
		this.globalParam = globalParam;
		this.mapperList = mapperList;
	}

	public boolean load() {
		
		if (driverProperties == null) {
			driverProperties = new Properties();
			// ******* driverProperties *****
			// poolMaximumActiveConnections
			// poolMaximumIdleConnections
			// poolMaximumCheckoutTime
			// poolTimeToWait
			// poolPingQuery
			// poolPingEnabled
			// poolPingConnectionsNotUsedFor
			// ////////////////////////////////
		}

		String dbType = settings.getString("type");
		driverProperties.setProperty("user", settings.getString("user"));
		driverProperties.setProperty("password", settings.getString("password"));
		driverProperties.setProperty("driver.encoding", "UTF-8");

		PooledDataSource dataSource = new PooledDataSource(settings.getString("driver"), settings.getString("url"), driverProperties);
		org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("ID", new JdbcTransactionFactory(), dataSource);
		Configuration configuration = new Configuration(environment);
		if (globalParam != null) {
			configuration.getVariables().putAll(globalParam);
		}
		
		if (mapperList != null) {
			List<URL> mapperFileList = new ArrayList<URL>();
			for (Class<?> mapperDAO : mapperList) {
				try {
					String mapperFilePath = mapperDAO.getName().replace('.', '/') + "_" + dbType + ".xml";
					URL mapperFile = Resources.getResourceURL(mapperFilePath);
					mapperFileList.add(mapperFile);
				} catch (IOException e) {
					logger.error("error load MapperFile", e);
				}
			}
		
			for (URL mapperFile : mapperFileList) {
				addSqlMappings(configuration, mapperFile);
			}
		}

		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		return true;
	}

	public SqlSession openSession() {
		return sqlSessionFactory.openSession();
	}

	public SqlSession openBatchSession() {
		return sqlSessionFactory.openSession(ExecutorType.BATCH);
	}

	public boolean unload() {
		logger.info(getClass().getSimpleName() + "[{}] Unloaded!, sqlSessionFactory = {}", settings.getString("url"), sqlSessionFactory);

		return true;
	}

	private void addSqlMappings(Configuration conf, URL mapperFilePath) {
		InputStream is = null;
		try {
			is = mapperFilePath.openStream();
			XMLMapperBuilder xmlParser = new XMLMapperBuilder(is, conf, mapperFilePath.toString(), conf.getSqlFragments());
			xmlParser.parse();
		} catch (IOException e) {
			logger.error("error loading mybatis mapping config file.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

}

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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.module.AbstractModule;
import org.fastcatgroup.analytics.module.ModuleException;
import org.fastcatgroup.analytics.service.ServiceManager;

public class CommonDBModule extends AbstractModule {

	private String driver;
	private String dbUrl;
	private String dbUser;
	private String dbPass;
	private Map<String,Object> globalParam;

	protected static CommonDBModule instance;

	private List<URL> mapperFileList;
	
	private SqlSessionFactory sqlSessionFactory;

	public static CommonDBModule getInstance() {
		return instance;
	}

	public void asSingleton() {
		instance = this;
	}

	public CommonDBModule(String driver, String dbUrl, String user, String pass, Map<String,Object> globalParam, List<URL> mapperFileList, Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings);
		this.driver = driver;
		this.dbUrl = dbUrl;
		this.dbUser = user;
		this.dbPass = pass;
		this.globalParam = globalParam;
		this.mapperFileList = mapperFileList;
	}

	@Override
	protected boolean doLoad() throws ModuleException {
		
		PooledDataSource dataSource = new PooledDataSource(driver, dbUrl, dbUser, dbPass);
		org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("ID", new JdbcTransactionFactory(), dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.getVariables().putAll(globalParam);
		
		if(mapperFileList != null){
			for(URL mapperFile : mapperFileList){
				addSqlMappings(configuration, mapperFile);
			}
		}
		
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		return true;
	}

	public SqlSession openSession(){
		return sqlSessionFactory.openSession();
	}
	public SqlSession openBatchSession(){
		return sqlSessionFactory.openSession(ExecutorType.BATCH);
	}
	
	@Override
	protected boolean doUnload() throws ModuleException {
		try {
			logger.info(getClass().getSimpleName()+"[{}] Unloaded!, sqlSessionFactory = {}", dbUrl, sqlSessionFactory);
			DriverManager.getConnection(dbUrl, dbUser, dbPass);
		} catch (SQLException e) {
			logger.error("{}", e.getMessage());
			return false;
		}
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
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static class MapperSession<T> {
		private SqlSession session;
		private T mapper;
		
		public SqlSession getSession() {
			return session;
		}
		
		public MapperSession(SqlSession session, T mapper){
			this.session = session;
			this.mapper = mapper;
		}
		
		public T getMapper(){
			return mapper;
		}
		
		public void commit(){
			if(session != null){
				session.commit();
			}
		}
		
		public void rollback(){
			if(session != null){
				session.rollback();
			}
		}
		public void closeSession(){
			if(session != null){
				session.commit();
				session.close();
			}
		}
	}

}

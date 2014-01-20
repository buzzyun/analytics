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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.fastcatgroup.analytics.db.mapper.ManagedMapper;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.module.ModuleException;
import org.fastcatgroup.analytics.service.AbstractService;
import org.fastcatgroup.analytics.service.ServiceManager;

public abstract class AbstractDBService extends AbstractService {

	protected InternalDBModule internalDBModule;
	private Class<?>[] mapperList;

	public AbstractDBService(String dbPath, Class<?>[] mapperList, Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		this.mapperList = mapperList;
		String dbmsName = "mysql"; // TODO 차후 셋팅으로..
		String absoluteDbPath = environment.filePaths().file(dbPath).getAbsolutePath();
		// system관련 mapper설정.
		List<URL> mapperFileList = new ArrayList<URL>();
		for (Class<?> mapperDAO : mapperList) {
			try {
				String mapperFilePath = mapperDAO.getName().replace('.', '/') + "_" + dbmsName + ".xml";
				URL mapperFile = Resources.getResourceURL(mapperFilePath);
				mapperFileList.add(mapperFile);
			} catch (IOException e) {
				logger.error("error load MapperFile", e);
			}
		}
		internalDBModule = new InternalDBModule(absoluteDbPath, mapperFileList, environment, settings, serviceManager);

	}

	public InternalDBModule internalDBModule() {
		return internalDBModule;
	}

	public <T> MapperSession<T> getMapperSession(Class<T> type) {
		SqlSession session = internalDBModule.openSession();
		return new MapperSession<T>(session, session.getMapper(type));

	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		internalDBModule.load();
		for (Class<?> mapperDAO : mapperList) {
			Class<? extends ManagedMapper> clazz = (Class<? extends ManagedMapper>) mapperDAO;
			MapperSession<? extends ManagedMapper> mapperSession = (MapperSession<? extends ManagedMapper>) getMapperSession(clazz);
			ManagedMapper managedMapper = mapperSession.getMapper();
			try {
				logger.debug("valiadte {}", clazz.getSimpleName());
				managedMapper.validateTable();
			} catch (Exception e) {
				try {
					logger.debug("drop {}", clazz.getSimpleName());
					managedMapper.dropTable();
					mapperSession.commit();
				} catch (Exception ignore) {
				}
				try {
					logger.debug("create table {}", clazz.getSimpleName());
					managedMapper.createTable();
					mapperSession.commit();
					logger.debug("create index {}", clazz.getSimpleName());
					managedMapper.createIndex();
					mapperSession.commit();

					initMapper(managedMapper);

				} catch (Exception e2) {
					logger.error("", e2);
				}
			}
			mapperSession.closeSession();

		}

		return true;
	}

	protected abstract void initMapper(ManagedMapper managedMapper) throws Exception;

	@Override
	protected boolean doStop() throws AnalyticsException {
		try {
			internalDBModule.unload();
		} catch (ModuleException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		internalDBModule = null;
		return true;
	}

}

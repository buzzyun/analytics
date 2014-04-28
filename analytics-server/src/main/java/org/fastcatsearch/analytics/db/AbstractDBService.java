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

package org.fastcatsearch.analytics.db;

import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.module.ModuleException;
import org.fastcatsearch.analytics.service.AbstractService;
import org.fastcatsearch.analytics.service.ServiceManager;

public abstract class AbstractDBService extends AbstractService {

	protected CommonDBHandler dbHandler;
	
	private Class<?>[] mapperList;
	
	public AbstractDBService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
	}

	public CommonDBHandler dbHandler() {
		return dbHandler;
	}

	protected void init(Settings settings, Class<?>[] mapperList, Properties driverProperties, Map<String,Object> globalParam) {
		this.settings = settings;
		this.mapperList = mapperList;
		dbHandler = new CommonDBHandler(settings, mapperList, driverProperties, globalParam);
	}
	
	public <T> MapperSession<T> getMapperSession(Class<T> type) {
		SqlSession session = dbHandler.openSession();
		return new MapperSession<T>(session, session.getMapper(type));

	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		
		dbHandler.load();
		
		initMapper(mapperList);
		return true;
	}

	protected abstract void initMapper(Class<?>[] mapperList) throws AnalyticsException;

	@Override
	protected boolean doStop() throws AnalyticsException {
		try {
			dbHandler.unload();
		} catch (ModuleException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		dbHandler = null;
		return true;
	}

}

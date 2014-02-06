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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fastcatgroup.analytics.db.mapper.AnalyticsMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.service.ServiceManager;

public class AnalyticsDBService extends AbstractDBService {

	protected static AnalyticsDBService instance;

	private static Class<?>[] mapperList = new Class<?>[] { SearchHitMapper.class };

	public AnalyticsDBService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		Properties driverProperties = null;
		Map<String, Object> globalParam = null;
		init(settings, mapperList, driverProperties, globalParam);

		if (super.doStart()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean doStop() throws AnalyticsException {
		if (super.doStop()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		return super.doClose();
	}

	@Override
	protected void initMapper(Class<?>[] mapperList) throws AnalyticsException {

		List<String> siteList = new ArrayList<String>();
		List<String> categoryList = new ArrayList<String>();

		for (Class<?> mapperDAO : mapperList) {
			Class<? extends AnalyticsMapper> clazz = (Class<? extends AnalyticsMapper>) mapperDAO;
			MapperSession<? extends AnalyticsMapper> mapperSession = (MapperSession<? extends AnalyticsMapper>) getMapperSession(clazz);
			AnalyticsMapper managedMapper = mapperSession.getMapper();

			for (String site : siteList) {
				for (String category : categoryList) {
					try {
						logger.debug("valiadte {}", clazz.getSimpleName());
						managedMapper.validateTable(site, category);
					} catch (Exception e) {
						try {
							logger.debug("drop {}", clazz.getSimpleName());
							managedMapper.dropTable(site, category);
							mapperSession.commit();
						} catch (Exception ignore) {
						}
						try {
							logger.debug("create table {}", clazz.getSimpleName());
							managedMapper.createTable(site, category);
							mapperSession.commit();
							logger.debug("create index {}", clazz.getSimpleName());
							managedMapper.createIndex(site, category);
							mapperSession.commit();

						} catch (Exception e2) {
							logger.error("", e2);
						}
					}

				}
			}

			mapperSession.closeSession();

		}

	}

}
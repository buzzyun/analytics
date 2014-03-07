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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.db.mapper.AnalyticsMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.mapper.UserAccountMapper;
import org.fastcatgroup.analytics.db.vo.UserAccountVO;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.service.ServiceManager;

public class AnalyticsDBService extends AbstractDBService {

	String[] rankList;
	String[] typeList;

	private static Class<?>[] mapperList = new Class<?>[] { 
		SearchKeywordHitMapper.class
		, SearchHitMapper.class
		, RelateKeywordMapper.class
		, RelateKeywordValueMapper.class
		, SearchTypeHitMapper.class
		, SearchKeywordRankMapper.class 
		, UserAccountMapper.class
	};

	public AnalyticsDBService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		rankList = settings.getStringArray("rankList", ",");
		typeList = settings.getStringArray("typeList", ",");

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

		SiteCategoryListConfig config = ServiceManager.getInstance().getService(StatisticsService.class).getSiteCategoryListConfig();
		List<SiteCategoryConfig> siteCategoryConfig = config.getList();

		for (Class<?> mapperDAO : mapperList) {
			
			Class<? extends AnalyticsMapper> clazz = (Class<? extends AnalyticsMapper>) mapperDAO;
			MapperSession<? extends AnalyticsMapper> mapperSession = (MapperSession<? extends AnalyticsMapper>) getMapperSession(clazz);
			AnalyticsMapper managedMapper = mapperSession.getMapper();
			for (SiteCategoryConfig siteConfig : siteCategoryConfig) {
				String siteId = siteConfig.getSiteId();
				try {
					try{
						//mysql에서 이상하게 최초쿼리는 에러나서..
						logger.debug("valiadte1 {}, {}", siteId, clazz.getSimpleName());
						managedMapper.validateTable(siteId);
					}catch(Exception ignore){
					}
					
					logger.debug("valiadte {}, {}", siteId, clazz.getSimpleName());
					managedMapper.validateTable(siteId);
				} catch (Exception e) {
//					logger.error("valid error", e);
					try {
						logger.debug("drop {}, {}", siteId, clazz.getSimpleName());
						managedMapper.dropTable(siteId);
						mapperSession.commit();
					} catch (Exception ignore) {
					}
					try {
						logger.debug("create table {}, {}", siteId, clazz.getSimpleName());
						managedMapper.createTable(siteId);
						mapperSession.commit();
						logger.debug("create index {}, {}", siteId, clazz.getSimpleName());
						managedMapper.createIndex(siteId);
						mapperSession.commit();
						
						if(managedMapper instanceof UserAccountMapper) {
							UserAccountMapper mapper = (UserAccountMapper)managedMapper;
							mapper.putEntry(new UserAccountVO(UserAccountVO.ADMIN_USER_NAME, UserAccountVO.ADMIN_USER_ID, "1111", "", ""));
						}
					} catch (Exception e2) {
						logger.error("", e2);
					}
				}

			}
			mapperSession.closeSession();
		}

	}

}

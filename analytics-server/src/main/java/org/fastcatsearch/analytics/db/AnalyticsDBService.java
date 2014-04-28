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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.db.mapper.AnalyticsMapper;
import org.fastcatsearch.analytics.db.mapper.ClickHitMapper;
import org.fastcatsearch.analytics.db.mapper.ClickKeywordHitMapper;
import org.fastcatsearch.analytics.db.mapper.ClickKeywordTargetHitMapper;
import org.fastcatsearch.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatsearch.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordEmptyMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatsearch.analytics.db.mapper.SearchPathHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatsearch.analytics.db.mapper.UserAccountMapper;
import org.fastcatsearch.analytics.db.vo.UserAccountVO;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.service.ServiceManager;

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
		, SearchKeywordEmptyMapper.class
		, UserAccountMapper.class
		, ClickHitMapper.class
		, ClickKeywordHitMapper.class
		, ClickKeywordTargetHitMapper.class
		, SearchPathHitMapper.class
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
	
	public void addNewSiteMappers(String siteId) {
		for (Class<?> mapperDAO : mapperList) {
			Class<? extends AnalyticsMapper> clazz = (Class<? extends AnalyticsMapper>) mapperDAO;
			MapperSession<? extends AnalyticsMapper> mapperSession = (MapperSession<? extends AnalyticsMapper>) getMapperSession(clazz);
			try {
				addNewSiteMapper(siteId, mapperSession, clazz);
			} finally {
				mapperSession.closeSession();
			}
		}
	}
	
	public void dropSiteMapper(String siteId) {
		for (Class<?> mapperDAO : mapperList) {
			if(!mapperDAO.equals(UserAccountMapper.class)) {
				Class<? extends AnalyticsMapper> clazz = (Class<? extends AnalyticsMapper>) mapperDAO;
				MapperSession<? extends AnalyticsMapper> mapperSession = (MapperSession<? extends AnalyticsMapper>) getMapperSession(clazz);
				try {
					dropSiteMapper(siteId, mapperSession, clazz);
				} catch (Exception e) {
					logger.error("",e);
				} finally {
					mapperSession.closeSession();
				}
			}
		}
	}
	
	public void dropSiteMapper(String siteId, MapperSession<? extends AnalyticsMapper> mapperSession, Class<? extends AnalyticsMapper> clazz) {
		AnalyticsMapper managedMapper = mapperSession.getMapper();
		try {
			logger.debug("drop {}, {}", siteId, clazz.getSimpleName());
			managedMapper.dropTable(siteId);
			mapperSession.commit();
		} catch (Exception ignore) {
		}
	}
	
	public void addNewSiteMapper(String siteId, MapperSession<? extends AnalyticsMapper> mapperSession, Class<? extends AnalyticsMapper> clazz) {
		AnalyticsMapper managedMapper = mapperSession.getMapper();
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
				managedMapper.createTable(siteId, settings.getString("option", ""));
				mapperSession.commit();
				logger.debug("create index {}, {}", siteId, clazz.getSimpleName());
				managedMapper.createIndex(siteId);
				mapperSession.commit();
			} catch (Exception e2) {
				logger.error("", e2);
			}
		}
	}

	@Override
	protected void initMapper(Class<?>[] mapperList) throws AnalyticsException {

		SiteListSetting config = ServiceManager.getInstance().getService(StatisticsService.class).getSiteListSetting();
		List<SiteSetting> siteCategoryConfig = config.getSiteList();

		for (Class<?> mapperDAO : mapperList) {
			Class<? extends AnalyticsMapper> clazz = (Class<? extends AnalyticsMapper>) mapperDAO;
			if(!clazz.equals(UserAccountMapper.class)) {
				logger.debug("class : {}", clazz.getSimpleName());
				MapperSession<? extends AnalyticsMapper> mapperSession = (MapperSession<? extends AnalyticsMapper>) getMapperSession(clazz);
				try {
					for (SiteSetting siteConfig : siteCategoryConfig) {
						String siteId = siteConfig.getId();
						this.addNewSiteMapper(siteId, mapperSession, clazz);
					}
				} finally {
					if (mapperSession != null) {
						mapperSession.closeSession();
					}
				}
			}
		}
		
		try {
			MapperSession<UserAccountMapper> mapperSession = getMapperSession(UserAccountMapper.class);
			this.addNewSiteMapper("", mapperSession, UserAccountMapper.class);
			
			UserAccountMapper userAccountMapper = mapperSession.getMapper();
			
			if(userAccountMapper.getCount() == 0) {
				try{
					userAccountMapper.putEntry(new UserAccountVO("Administrator", "admin", "1111", "", ""));
				} finally {
					if (mapperSession != null) {
						mapperSession.closeSession();
					}
				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}
}

package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.log.SearchLogResult;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateSearchHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	String timeId;
	
	public UpdateSearchHitHandler(String siteId, String categoryId, String timeId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		SearchLogResult searchLogResult = (SearchLogResult) parameter;
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		
		/*
		 * 1. 검색횟수기록.
		 */
		MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
		try {
			SearchHitMapper mapper = mapperSession.getMapper();
			logger.debug("#### UpdateSearchHit {} >> {} > {} / {}", timeId, categoryId, searchLogResult, mapper);
			
			SearchHitVO vo = mapper.getEntry(siteId, categoryId, timeId);
			if(vo != null){
				mapper.updateEntry(siteId, categoryId, timeId, searchLogResult.getSearchCount(), searchLogResult.getAverageResponseTime(), searchLogResult.getMaxResponseTime());
			} else {
				mapper.putEntry(siteId, categoryId, timeId, searchLogResult.getSearchCount(), searchLogResult.getAverageResponseTime(), searchLogResult.getMaxResponseTime());
			}
			
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		
		return parameter;
	}

}

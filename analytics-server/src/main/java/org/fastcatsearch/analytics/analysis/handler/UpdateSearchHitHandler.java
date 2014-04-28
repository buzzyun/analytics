package org.fastcatsearch.analytics.analysis.handler;

import org.fastcatsearch.analytics.analysis.log.SearchLogResult;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;

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

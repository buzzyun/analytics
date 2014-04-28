package org.fastcatsearch.analytics.analysis.handler;

import java.util.Calendar;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.log.SearchLogResult;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdateHourlySearchHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	Calendar calendar;
	
	public UpdateHourlySearchHitHandler(String siteId, String categoryId, Calendar calendar) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.calendar = (Calendar) calendar.clone();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
	}

	@Override
	public Object process(Object parameter) throws Exception {
		SearchLogResult[] searchLogResultArray = (SearchLogResult[]) parameter;
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		
		logger.debug("hourly result : {}{}", "", searchLogResultArray);
		
		for (int timeInx = 0; timeInx < searchLogResultArray.length; timeInx++) {
			SearchLogResult searchLogResult = searchLogResultArray[timeInx];
			
			if(searchLogResult == null) {
				continue;
			}
			/*
			 * 1. 검색횟수기록.
			 */
			MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
			
			calendar.set(Calendar.HOUR_OF_DAY, timeInx);
			
			String timeId = StatisticsUtils.getTimeId(calendar, Calendar.HOUR_OF_DAY);
			try {
				SearchHitMapper mapper = mapperSession.getMapper();
	
				logger.trace("#### UpdateSearchHit {}/{} >> {} > {} / {}", timeId, timeInx, categoryId, searchLogResult, mapper);
				
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
		}
		return null;
	}
}

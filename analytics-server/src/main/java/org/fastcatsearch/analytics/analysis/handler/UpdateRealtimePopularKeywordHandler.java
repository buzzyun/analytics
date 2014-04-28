package org.fastcatsearch.analytics.analysis.handler;

import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdateRealtimePopularKeywordHandler extends ProcessHandler {

	String siteId;
	String categoryId;
	
	public UpdateRealtimePopularKeywordHandler(String siteId, String categoryId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		logger.debug("UpdateRealtimePopularKeywordHandler > {}", parameter);
		if (parameter != null) {
			List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
			StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
			statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
		}
		return null;
	}

}

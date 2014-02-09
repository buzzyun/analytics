package org.fastcatgroup.analytics.analysis.handler;

import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateDailyPopularKeywordHandler extends ProcessHandler {

	String siteId;
	String categoryId;
	
	public UpdateDailyPopularKeywordHandler(String siteId, String categoryId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		if (parameter != null) {
			List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
			StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
			//TODO db입력.
//			statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
		}
		return null;
	}

}

package org.fastcatgroup.analytics.analysis.handler;

import java.util.List;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;

public class UpdatePopularKeywordHandler extends ProcessHandler {

	@Override
	public Object process(Object parameter) throws Exception {
		if (parameter != null) {
			List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
//			statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
		}
		return null;
	}

}

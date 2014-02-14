package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.PopularKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.vo.PopularKeywordVO;
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
			// TODO db입력.

			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<PopularKeywordMapper> mapperSession = dbService.getMapperSession(PopularKeywordMapper.class);
			try {
				PopularKeywordMapper mapper = mapperSession.getMapper();
				// TODO 기준시각을 받는다.
				Calendar cal = null;

				String time = "";
				for (RankKeyword rankKeyword : keywordList) {
					rankKeyword.getKeyword();
					PopularKeywordVO vo = new PopularKeywordVO(categoryId, time, rankKeyword.getKeyword(), rankKeyword.getCount(), rankKeyword.getRank(),
							rankKeyword.getCountDiff(), rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
					mapper.putEntry(vo);
				}
			} finally {
				if (mapperSession != null) {
					mapperSession.closeSession();
				}
			}
			
			//TODO 일간 인기키워드를 서비스할지 셋팅에서 확인한다. 
			//어제. 그제, 그그제.. 등.. 
			// statisticsService.updateDailyPopularKeywordList(siteId, categoryId, keywordList);
		}
		return null;
	}

}

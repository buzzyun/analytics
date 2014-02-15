package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
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
			// db입력.
			
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<SearchKeywordRankMapper> mapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
			try {
				SearchKeywordRankMapper mapper = mapperSession.getMapper();
				// 기준시각.
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, -1);

				String timeId = SearchStatisticsProperties.getTimeId(cal, Calendar.DAY_OF_MONTH);
				
				
				int count = mapper.getCount(siteId, categoryId, timeId);
				if(count > 0){
					mapper.updateClean(siteId, categoryId, timeId);
				}
				
				for (RankKeyword rankKeyword : keywordList) {
					rankKeyword.getKeyword();
					RankKeywordVO vo = new RankKeywordVO(categoryId, timeId, rankKeyword.getKeyword(), rankKeyword.getCount(), rankKeyword.getRank(),
							rankKeyword.getCountDiff(), rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
					mapper.putEntry(siteId, vo);
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

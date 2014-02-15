package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateSearchHitHandler extends ProcessHandler {

	@Override
	public Object process(Object parameter) throws Exception {
		Integer count = (Integer) parameter;
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
		try {
			SearchHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			String timeId = SearchStatisticsProperties.getTimeId(cal, Calendar.DAY_OF_MONTH);
			
			if(count > 0){
//				mapper.updateClean(siteId, categoryId, timeId);
			}
			
//			for (RankKeyword rankKeyword : keywordList) {
//				rankKeyword.getKeyword();
//				RankKeywordVO vo = new RankKeywordVO(categoryId, timeId, rankKeyword.getKeyword(), rankKeyword.getCount(), rankKeyword.getRank(),
//						rankKeyword.getCountDiff(), rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
//				mapper.putEntry(siteId, vo);
//			}
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		
		
		return null;
	}

}

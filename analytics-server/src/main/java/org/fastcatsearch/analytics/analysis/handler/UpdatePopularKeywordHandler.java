package org.fastcatsearch.analytics.analysis.handler;

import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdatePopularKeywordHandler extends ProcessHandler {

	String siteId;
	String categoryId;
	String timeId;
	
	public UpdatePopularKeywordHandler(String siteId, String categoryId, String timeId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
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

				int count = mapper.getCount(siteId, categoryId, timeId, null, 0);
				if(count > 0){
					mapper.updateClean(siteId, categoryId, timeId);
				}
				logger.trace("result size : {}", keywordList.size());
				int i = 0;
				for (int inx = 0; inx < keywordList.size(); inx++) {
					RankKeyword rankKeyword  = keywordList.get(inx);
					RankKeywordVO vo = new RankKeywordVO(categoryId, timeId, rankKeyword.getKeyword(), rankKeyword.getCount(), rankKeyword.getRank(),
							rankKeyword.getCountDiff(), rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
					logger.trace("put {} rankKeyword daily {}-{}-{}", inx, categoryId,timeId, vo.getRank());
					try {
						mapper.putEntry(siteId, vo);
						i++;
					} catch (Exception e) {
						logger.error("", e);
					}
				}
//				explainLog("[PopularKeyword] db update count=", i);
			} finally {
				if (mapperSession != null) {
					mapperSession.closeSession();
				}
			}
			
			//이전에 셋팅된 메모리 객체를 없앤다. 다음에 최초 호출시 메모리에 다시 로드된다.
			statisticsService.clearPopularKeywordList();
		}
		return null;
	}

}

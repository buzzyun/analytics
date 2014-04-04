package org.fastcatgroup.analytics.analysis.handler;

import java.util.List;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordEmptyMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateEmptyKeywordHandler extends ProcessHandler {

	String siteId;
	String categoryId;
	String timeId;
	
	public UpdateEmptyKeywordHandler(String siteId, String categoryId, String timeId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		if (parameter != null) {
			@SuppressWarnings("unchecked")
			List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
			// db입력.
			
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<SearchKeywordEmptyMapper> mapperSession = dbService.getMapperSession(SearchKeywordEmptyMapper.class);
			try {
				SearchKeywordEmptyMapper mapper = mapperSession.getMapper();

				int count = mapper.getCount(siteId, categoryId, timeId);
				if(count > 0){
					mapper.updateClean(siteId, categoryId, timeId);
				}
				logger.debug("result size : {}", keywordList.size());
				for (int inx = 0; inx < keywordList.size(); inx++) {
					RankKeyword rankKeyword  = keywordList.get(inx);
					RankKeywordVO vo = new RankKeywordVO(categoryId, timeId, rankKeyword.getKeyword(), rankKeyword.getCount(), rankKeyword.getRank(),
							rankKeyword.getCountDiff(), rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
					logger.trace("put {} rankKeyword daily {}-{}-{}", inx, categoryId,timeId, vo.getRank());
					try {
						mapper.putEntry(siteId, vo);
					} catch (Exception e) {
						logger.error("", e);
					}
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

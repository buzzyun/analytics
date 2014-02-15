package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;

/*
 * 인기키워드 테이블.
 * int id가 자동증가 pk이다. 
 * */
public interface SearchKeywordRankMapper extends AnalyticsMapper {
	
	public List<RankKeywordVO> getEntryList(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId, @Param("rankDiffType") String rankDiffType, @Param("rankDiffOver") int rankDiffOver, @Param("start") int start, @Param("length") int length) throws Exception;
	
	public int getCount(@Param("siteId") String siteId, @Param("categoryId") String categoryId, @Param("timeId") String timeId
			, @Param("rankDiffType") String rankDiffType, @Param("rankDiffOver") int rankDiffOver) throws Exception;
	
	public void putEntry(@Param("siteId") String siteId, @Param("vo") RankKeywordVO vo) throws Exception;
	
	public void updateEntry(@Param("siteId") String siteId, @Param("vo") RankKeywordVO vo);
	
	public int updateClean(@Param("siteId") String siteId, @Param("categoryId") String categoryId, @Param("timeId") String timeId) throws Exception;
	
}

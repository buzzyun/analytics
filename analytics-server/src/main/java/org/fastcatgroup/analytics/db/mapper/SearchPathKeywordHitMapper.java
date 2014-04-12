package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchPathKeywordHitVO;

public interface SearchPathKeywordHitMapper extends AnalyticsMapper {
	
	public List<SearchPathKeywordHitVO> getEntryList(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("keyword") String keyword) throws Exception;
	
	
	public int updateClear(@Param("siteId") String siteId,
			@Param("timeId") String timeId, @Param("keyword") String keyword) throws Exception;
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("searchType") String searchType, @Param("hit") int hit);
	
}

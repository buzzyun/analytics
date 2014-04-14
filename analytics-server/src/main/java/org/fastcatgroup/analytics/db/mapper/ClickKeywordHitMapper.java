package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.ClickKeywordHitVO;

public interface ClickKeywordHitMapper extends AnalyticsMapper {
	
	public List<ClickKeywordHitVO> getKeywordEntryList(@Param("siteId") String siteId,
			@Param("timeId") String timeId, @Param("topCount") int topCount) throws Exception;
	
	public Integer getKeywordClickCount(@Param("siteId") String siteId,
			@Param("timeId") String timeId, @Param("keyword") String keyword, @Param("clickType") String clickType) throws Exception;
	
	public int updateClear(@Param("siteId") String siteId,
			@Param("timeId") String timeId) throws Exception;
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("clickType") String clickType, @Param("hit") int hit);
	
	
}

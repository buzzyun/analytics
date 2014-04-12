package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.ClickKeywordTargetHitVO;

public interface ClickKeywordTargetHitMapper extends AnalyticsMapper {
	
	public List<ClickKeywordTargetHitVO> getEntryList(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("keyword") String keyword) throws Exception;
	
	
	public int updateClear(@Param("siteId") String siteId,
			@Param("timeId") String timeId, @Param("keyword") String keyword) throws Exception;
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId, @Param("keyword") String keyword, 
			@Param("clickId") String clickId, @Param("clickType") String clickType, @Param("hit") int hit);
	
}

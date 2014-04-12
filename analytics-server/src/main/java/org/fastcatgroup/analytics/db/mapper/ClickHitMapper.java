package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.ClickHitVO;
/**
 * 
 * */
public interface ClickHitMapper extends AnalyticsMapper {
	
	public ClickHitVO getEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("clickType") String clickType) throws Exception;
	
	public Integer getTypeHit(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("clickType") String clickType) throws Exception;
	
	public Integer getHit(@Param("siteId") String siteId,
			@Param("timeId") String timeId) throws Exception;
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("clickType") String clickType, 
			@Param("hit")int hit) throws Exception;
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId, 
			@Param("clickType") String clickType,
			@Param("hit")int hit) throws Exception;
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("clickType") String clickType,
			@Param("timeId") String timeId) throws Exception;

	public List<ClickHitVO> getEntryListBetween(@Param("siteId") String siteId,
			@Param("clickType") String clickType,
			@Param("from") String from, @Param("to") String to) throws Exception;
	
}

package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchPathHitVO;
/**
 * 
 * */
public interface SearchPathHitMapper extends AnalyticsMapper {
	
	public SearchPathHitVO getEntry(@Param("siteId") String siteId,
			@Param("timeId") String timeId,
			@Param("searchId") String searchId) throws Exception;
	
	public List<SearchPathHitVO> getEntryByTimeId(@Param("siteId") String siteId,
			@Param("timeId") String timeId) throws Exception;
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("searchId") String searchId,
			@Param("timeId") String timeId,
			@Param("hit") int hit) throws Exception;
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("searchId") String searchId,
			@Param("timeId") String timeId) throws Exception;
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("searchId") String searchId,
			@Param("timeId") String timeId) throws Exception;

	public List<SearchPathHitVO> getEntryListBetween(@Param("siteId") String siteId,
			@Param("searchId") String searchId,
			@Param("from") String from, @Param("to") String to) throws Exception;
	
}
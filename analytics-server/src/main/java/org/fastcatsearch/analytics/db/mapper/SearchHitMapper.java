package org.fastcatsearch.analytics.db.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
/**
 * 
 * */
public interface SearchHitMapper extends AnalyticsMapper {
	
	public SearchHitVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId)
			throws Exception;
	
	public List<SearchHitVO> getEntryListBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId,
			@Param("from") String from, @Param("to") String to);

	public SearchHitVO getMinEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;

	public SearchHitVO getMaxEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;
	
	public Map<String, Object> getCalcHitAndTime(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
	
	public int getCountBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);

	public int getSumBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit, @Param("avgTime") int avgTime, @Param("maxTime") int maxTime);
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit, @Param("avgTime") int avgTime, @Param("maxTime") int maxTime);
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
}
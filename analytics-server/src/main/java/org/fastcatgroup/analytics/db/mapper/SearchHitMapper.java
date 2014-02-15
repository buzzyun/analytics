package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;

public interface SearchHitMapper extends AnalyticsMapper {
	
	public SearchHitVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchHitVO getEntryList(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId)
			throws Exception;
	
	public List<SearchHitVO> getEntryListBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeType") String timeType,
			@Param("from") String from, @Param("to") String to);

	
	
	public SearchHitVO getMinEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;

	public SearchHitVO getMaxEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;

	
	
	public int getCountBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);

	public int getSumBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
}
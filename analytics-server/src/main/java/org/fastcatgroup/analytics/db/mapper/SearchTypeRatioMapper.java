package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchTypeRatioVO;

public interface SearchTypeRatioMapper extends AnalyticsMapper {
	
	public SearchTypeRatioVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchTypeRatioVO getMinEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public SearchTypeRatioVO getMaxEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public List<String> listTypes(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("from") String from, @Param("to") String to);

	public List<SearchTypeRatioVO> getEntryListBetween(
			@Param("siteId") String siteId, @Param("categoryId") String categoryId,
			@Param("stype") String stype, @Param("dFilter") String dFilter,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to, @Param("isGroup") boolean isGroup);
	
	public int getCountBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int getSumBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to);
}

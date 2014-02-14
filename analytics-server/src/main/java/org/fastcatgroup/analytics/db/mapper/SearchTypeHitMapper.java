package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;

public interface SearchTypeHitMapper extends AnalyticsTypeMapper {
	
	public SearchTypeHitVO getEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dtype") String dtype, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchTypeHitVO getMinEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public SearchTypeHitVO getMaxEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public List<String> listTypes(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("from") String from, @Param("to") String to);

	public List<SearchTypeHitVO> getEntryListBetween(
			@Param("siteId") String siteId, @Param("typeId") String typeId, 
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to, @Param("isGroup") boolean isGroup);
	
	public int getCountBetween(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int getSumBetween(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dtype") String dtype, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to);
}

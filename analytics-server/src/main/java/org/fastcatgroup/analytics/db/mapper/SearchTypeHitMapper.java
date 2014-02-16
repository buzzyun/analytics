package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;

public interface SearchTypeHitMapper extends AnalyticsMapper {
	
	public SearchTypeHitVO getEntry(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId, @Param("typeId") String typeId, 
			@Param("dtype") String dtype) throws Exception;
	
	public int getCount(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId, @Param("typeId") String typeId) throws Exception;
	
	public List<SearchTypeHitVO> getEntryList(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId, @Param("typeId") String typeId)	throws Exception;
	
	public List<SearchTypeHitVO> getEntryListBetween(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("typeId") String typeId, @Param("from") String from, @Param("to") String to)	throws Exception;
	
	public int putEntry(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId, @Param("typeId") String typeId, 
			@Param("dtype") String dtype, @Param("hit") int hit) throws Exception;
	
	public int updateClear(@Param("siteId") String siteId, @Param("categoryId") String categoryId, 
			@Param("timeId") String timeId,	@Param("typeId") String typeId) throws Exception;
	
}

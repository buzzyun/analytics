package org.fastcatgroup.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;

public interface SearchTypeHitMapper extends AnalyticsTypeMapper {
	
	public SearchTypeHitVO getEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId, 
			@Param("dtype") String dtype) throws Exception;
	
	public SearchTypeHitVO getEntryList(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId)	throws Exception;
	
	public int putEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("dtype") String dtype, @Param("hit") int hit) throws Exception;
	
	public int updateEntry(@Param("siteId") String siteId, @Param("typeId") String typeId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("dtype") String dtype, @Param("hit") int hit) throws Exception;
	
}

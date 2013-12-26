package org.fastcatgroup.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;

public interface SearchKeywordHitMapper {
	
	public void createTable(@Param("site") String site, @Param("category") String category);

	public void createIndex(@Param("site") String site, @Param("category") String category) throws Exception;
	
	public void validateTable(@Param("site") String site, @Param("category") String category) throws Exception;
	
	public void dropTable(@Param("site") String site, @Param("category") String category) throws Exception;
}

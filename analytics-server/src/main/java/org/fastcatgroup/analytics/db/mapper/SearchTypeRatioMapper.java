package org.fastcatgroup.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;

public interface SearchTypeRatioMapper {
	
	public void createTable(@Param("site") String site, @Param("category") String category, @Param("stype") String stype);

	public void createIndex(@Param("site") String site, @Param("category") String category, @Param("stype") String stype) throws Exception;
	
	public void validateTable(@Param("site") String site, @Param("category") String category, @Param("stype") String stype) throws Exception;
	
	public void dropTable(@Param("site") String site, @Param("category") String category, @Param("stype") String stype) throws Exception;
}

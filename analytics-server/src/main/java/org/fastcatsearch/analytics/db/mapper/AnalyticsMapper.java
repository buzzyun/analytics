package org.fastcatsearch.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;


public interface AnalyticsMapper {
	public void createTable(@Param("siteId") String siteId, @Param("option") String option) throws Exception;
	
	public void createIndex(@Param("siteId") String siteId) throws Exception;
	
	public void validateTable(@Param("siteId") String siteId) throws Exception;
	
	public void dropTable(@Param("siteId") String siteId) throws Exception;
	
	public int truncate(@Param("siteId") String siteId) throws Exception;
}

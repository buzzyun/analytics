package org.fastcatgroup.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;

/*
 * 테이블명이 siteId와 typeId로 구분되는 경우.
 * */
public interface AnalyticsTypeMapper {
	public void createTable(@Param("siteId") String siteId, @Param("typeId") String typeId) throws Exception;
	
	public void createIndex(@Param("siteId") String siteId, @Param("typeId") String typeId) throws Exception;
	
	public void validateTable(@Param("siteId") String siteId, @Param("typeId") String typeId) throws Exception;
	
	public void dropTable(@Param("siteId") String siteId, @Param("typeId") String typeId) throws Exception;
	
	public int truncate(@Param("siteId") String siteId, @Param("typeId") String typeId) throws Exception;
}

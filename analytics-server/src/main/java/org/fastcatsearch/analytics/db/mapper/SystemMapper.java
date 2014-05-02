package org.fastcatsearch.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;


public interface SystemMapper {
	public void createTable(@Param("option") String option) throws Exception;
	
	public void createIndex() throws Exception;
	
	public void validateTable() throws Exception;
	
	public void dropTable() throws Exception;
	
	public int truncate() throws Exception;
}

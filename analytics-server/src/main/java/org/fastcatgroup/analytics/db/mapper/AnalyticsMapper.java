package org.fastcatgroup.analytics.db.mapper;


public interface AnalyticsMapper {
	public void createTable(String site) throws Exception;
	
	public void createIndex(String site) throws Exception;
	
	public void validateTable(String site) throws Exception;
	
	public void dropTable(String site) throws Exception;
	
	public int truncate(String siteId) throws Exception;
}

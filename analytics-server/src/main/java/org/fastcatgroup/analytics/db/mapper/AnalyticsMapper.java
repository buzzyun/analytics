package org.fastcatgroup.analytics.db.mapper;

public interface AnalyticsMapper {
	public void createTable(String site, String category) throws Exception;
	
	public void createIndex(String site, String category) throws Exception;
	
	public void validateTable(String site, String category) throws Exception;
	
	public void dropTable(String site, String category) throws Exception;
}

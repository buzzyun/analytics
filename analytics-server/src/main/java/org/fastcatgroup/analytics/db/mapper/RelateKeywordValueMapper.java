package org.fastcatgroup.analytics.db.mapper;

import org.apache.ibatis.annotations.Param;

/*
 * 연관키워드 테이블.
 * */
public interface RelateKeywordValueMapper extends AnalyticsMapper {
	
	public void putEntry(@Param("siteId")String siteId, @Param("keyId") int keyId, @Param("value") String value) throws Exception;
}

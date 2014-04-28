package org.fastcatsearch.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.RelateKeywordVO;

/*
 * 연관키워드 테이블.
 * */
public interface RelateKeywordMapper extends AnalyticsMapper {
	
	public RelateKeywordVO getEntry(@Param("siteId") String siteId, @Param("keyword") String keyword) throws Exception;
	
	public RelateKeywordVO getEntryById(@Param("siteId") String siteId, @Param("id") String id) throws Exception;
	
	public List<RelateKeywordVO> getEntryList(@Param("siteId") String siteId, @Param("start")int start, @Param("end")int end) throws Exception;
	
	public List<RelateKeywordVO> getEntryListByWhereCondition(@Param("siteId") String siteId, @Param("whereCondition") String whereCondition ,@Param("start")int start, @Param("end")int end) throws Exception; 
	
	public List<RelateKeywordVO> getEntryListByKeyword(@Param("siteId") String siteId, @Param("exactMatch") boolean exactMatch, @Param("keyword") String keyword ,@Param("start")int start, @Param("end")int end) throws Exception; 
	
	public void updateEntry(@Param("siteId") String siteId, @Param("entry") RelateKeywordVO vo) throws Exception;
	
	public void putEntry(@Param("siteId") String siteId, @Param("entry") RelateKeywordVO vo) throws Exception;
	
	public void deleteEntry(@Param("siteId") String siteId, @Param("id") int id) throws Exception;
	
	public void deleteEntryList(@Param("siteId") String siteId, @Param("idList") String idList) throws Exception;
	
	public int getCount(@Param("siteId") String siteId) throws Exception;
	
	public int getCountByWhereCondition(@Param("siteId") String siteId, @Param("whereCondition") String whereCondition) throws Exception;
	
	public int getCountByKeyword(@Param("siteId") String siteId, @Param("exactMatch") boolean exactMatch, @Param("keyword") String keyword) throws Exception;
	
}

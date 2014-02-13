package org.fastcatgroup.analytics.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;

/*
 * 연관키워드 테이블.
 * */
public interface RelateKeywordMapper extends AnalyticsMapper {
	
	public RelateKeywordVO getEntry(@Param("siteId") String siteId, @Param("keyword") String keyword) throws Exception;
	
	public List<RelateKeywordVO> getEntryList(@Param("siteId") String siteId) throws Exception;
	
	public List<RelateKeywordVO> getEntryListByWhereCondition(@Param("siteId") String siteId, @Param("whereCondition") String whereCondition ,@Param("start")int start, @Param("end")int end) throws Exception; 
	
	public List<String> getKeywordList(@Param("siteId") String siteId, @Param("keyword") String keyword);
	
	public List<String> getKeywordListByWhereCondition(@Param("siteId") String siteId, @Param("keyword") String keyword, @Param("whereCondition") String whereCondition);
	
	public void updateEntry(@Param("siteId") String siteId, @Param("keyword") String keyword, @Param("value") String value, @Param("updateTime") Date updateTime, @Param("id") int id );
	
	public void putEntry(@Param("siteId") String siteId, @Param("entry") RelateKeywordVO vo) throws Exception;
	
	public void deleteEntry(@Param("siteId") String siteId, @Param("id") int id);
	
	public void deleteEntryList(@Param("siteId") String siteId, @Param("idList") String idList);
	
	public int getCount(@Param("siteId") String siteId) throws Exception;
	
	public int getCountByWhereCondition(@Param("siteId") String siteId, @Param("whereCondition") String whereCondition) throws Exception;
	
}

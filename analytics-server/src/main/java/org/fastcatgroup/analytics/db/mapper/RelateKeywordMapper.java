package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;

/*
 * 연관키워드 테이블.
 * */
public interface RelateKeywordMapper extends AnalyticsMapper {
	
	public RelateKeywordVO getEntry(@Param("site") String site, @Param("category") String category, @Param("keyword") String keyword) throws Exception;
	
	public List<RelateKeywordVO> getEntryList(@Param("site") String site, @Param("category") String category) throws Exception;
	
	public List<RelateKeywordVO> getEntryListByWhereCondition(@Param("site") String site, @Param("category") String category, @Param("whereCondition") String whereCondition ,@Param("start")int start, @Param("end")int end) throws Exception; 
	
	public void putEntry(RelateKeywordVO vo) throws Exception;
	
	public void deleteEntry(@Param("id") int id);
	
	public int getCount(@Param("site") String site, @Param("category") String category) throws Exception;
	
	public int getCountByWhereCondition(@Param("site") String site, @Param("category") String category, @Param("whereCondition") String whereCondition) throws Exception;
	
}

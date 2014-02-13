package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.PopularKeywordVO;

/*
 * 인기키워드 테이블.
 * int id가 자동증가 pk이다. 
 * */
public interface PopularKeywordMapper extends AnalyticsMapper {
	
	public List<PopularKeywordVO> getEntryList(@Param("categoryId") String categoryId, @Param("time") String time) throws Exception;
	
	public List<PopularKeywordVO> getTopEntryList(@Param("categoryId") String categoryId, @Param("time") String time, @Param("limit") int limit) throws Exception;
	
	public void putEntry(PopularKeywordVO vo) throws Exception;
	
	public void updateEntry(PopularKeywordVO vo);
	
	public void deleteEntryById(@Param("id") int id);
	
	public void deleteElderThan(@Param("categoryId") String categoryId, @Param("time") String time);

	public PopularKeywordVO getRankEntry(@Param("categoryId") String categoryId, @Param("time") String time, @Param("rank") int rank);

}

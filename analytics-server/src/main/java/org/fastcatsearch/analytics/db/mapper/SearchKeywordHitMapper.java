package org.fastcatsearch.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;

public interface SearchKeywordHitMapper extends AnalyticsMapper {
	
	public SearchHitVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword) throws Exception;
	
	public int getCount(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId) throws Exception;
	
	public int updateClear(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId) throws Exception;
	
	public List<SearchHitVO> getEntryListBetween(
			@Param("siteId") String siteId, @Param("categoryId") String categoryId,
			@Param("keyword") String keyword,
			@Param("from") String from, @Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("hit") int hit);
	
	
//	public SearchHitVO getMinEntry(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter, 
//			@Param("keyword") String keyword) throws Exception;
//	
//	public SearchHitVO getMaxEntry(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
//			@Param("keyword") String keyword) throws Exception;

//	public int getCountBetween(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
//			@Param("keyword") String keyword, @Param("from") String from,
//			@Param("to") String to);
//	
//	public int getSumBetween(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId,
//			@Param("keyword") String keyword, @Param("from") String from,
//			@Param("to") String to);
	
	
//	public int updateEntry(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
//			@Param("keyword") String keyword, @Param("hit") int hit);
	
//	public int deleteEntry(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
//			@Param("keyword") String keyword);
	
//	public int deleteEntryBetween(@Param("siteId") String siteId,
//			@Param("categoryId") String categoryId,
//			@Param("keyword") String keyword, @Param("from") String from,
//			@Param("to") String to);
	
	
}

package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchKeywordHitVO;

public interface SearchKeywordHitMapper extends AnalyticsMapper {
	
	@Override
	public void createTable(@Param("siteId") String siteId);

	@Override
	public void createIndex(@Param("siteId") String siteId) throws Exception;

	@Override
	public void validateTable(@Param("siteId") String siteId) throws Exception;

	@Override
	public void dropTable(@Param("siteId") String siteId) throws Exception;
	
	@Override
	public int truncate(@Param("siteId") String siteId) throws Exception;
	
	public SearchKeywordHitVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword) throws Exception;
	
	public SearchKeywordHitVO getMinEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter, 
			@Param("keyword") String keyword) throws Exception;
	
	public SearchKeywordHitVO getMaxEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("keyword") String keyword) throws Exception;

	public List<String> searchKeyword(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("search") String search,
			@Param("from") String from, @Param("to") String to);
	
	public List<SearchKeywordHitVO> getEntryListBetween(
			@Param("siteId") String siteId, @Param("categoryId") String categoryId,
			@Param("dFilter") String dFilter, @Param("keyword") String keyword,
			@Param("from") String from, @Param("to") String to,
			@Param("isGroup") boolean isGroup);
	
	public int getCountBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	public int getSumBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("hit") int hit);
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("hit") int hit);
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("keyword") String keyword);
	
	public int deleteEntryBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	
}

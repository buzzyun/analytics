package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchKeywordHitVO;

public interface SearchKeywordHitMapper {
	
	public void createTable(@Param("site") String site,
			@Param("category") String category);

	public void createIndex(@Param("site") String site,
			@Param("category") String category) throws Exception;

	public void validateTable(@Param("site") String site,
			@Param("category") String category) throws Exception;

	public void dropTable(@Param("site") String site,
			@Param("category") String category) throws Exception;
	
	public SearchKeywordHitVO getEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("keyword") String keyword) throws Exception;
	
	public SearchKeywordHitVO getMinEntry(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter, 
			@Param("keyword") String keyword) throws Exception;
	
	public SearchKeywordHitVO getMaxEntry(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter,
			@Param("keyword") String keyword) throws Exception;

	public List<String> searchKeyword(@Param("site") String site,
			@Param("category") String category, @Param("search") String search,
			@Param("from") String from, @Param("to") String to);
	
	public List<SearchKeywordHitVO> getEntryListBetween(
			@Param("site") String site, @Param("category") String category,
			@Param("dFilter") String dFilter, @Param("keyword") String keyword,
			@Param("from") String from, @Param("to") String to,
			@Param("isGroup") boolean isGroup);
	
	public int getCountBetween(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	public int getSumBetween(@Param("site") String site,
			@Param("category") String category,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("hit") int hit);
	
	public int updateEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("keyword") String keyword, @Param("hit") int hit);
	
	public int deleteEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("keyword") String keyword);
	
	public int deleteEntryBetween(@Param("site") String site,
			@Param("category") String category,
			@Param("keyword") String keyword, @Param("from") String from,
			@Param("to") String to);
	
	public int truncate(@Param("site") String site,
			@Param("category") String category);
}

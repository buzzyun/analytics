package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;

public interface SearchHitMapper extends AnalyticsMapper {
	
	@Override
	public void createTable(@Param("siteId") String siteId) throws Exception;

	@Override
	public void createIndex(@Param("siteId") String siteId) throws Exception;
	
	@Override
	public void validateTable(@Param("siteId") String siteId) throws Exception;
	
	@Override
	public void dropTable(@Param("siteId") String siteId) throws Exception;
	
	public SearchHitVO getEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchHitVO getMinEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;

	public SearchHitVO getMaxEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter)
			throws Exception;

	public List<SearchHitVO> getEntryListBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);
	
	public int getCountBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);

	public int getSumBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("siteId") String siteId,
			@Param("categoryId") String categoryId, @Param("from") String from,
			@Param("to") String to);
	
	public int truncate(@Param("siteId") String siteId);
}
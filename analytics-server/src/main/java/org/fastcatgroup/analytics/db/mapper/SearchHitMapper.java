package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;

public interface SearchHitMapper {
	
	public void createTable(@Param("site") String site,
			@Param("category") String category);

	public void createIndex(@Param("site") String site,
			@Param("category") String category) throws Exception;
	
	public void validateTable(@Param("site") String site,
			@Param("category") String category) throws Exception;
	
	public void dropTable(@Param("site") String site,
			@Param("category") String category) throws Exception;
	
	public SearchHitVO getEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchHitVO getMinEntry(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter)
			throws Exception;

	public SearchHitVO getMaxEntry(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter)
			throws Exception;

	public List<SearchHitVO> getEntryListBetween(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);
	
	public int getCountBetween(@Param("site") String site,
			@Param("category") String category, @Param("dFilter") String dFilter,
			@Param("from") String from, @Param("to") String to);

	public int getSumBetween(@Param("site") String site,
			@Param("category") String category, @Param("from") String from,
			@Param("to") String to);
	
	public int putEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("site") String site,
			@Param("category") String category, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("site") String site,
			@Param("category") String category, @Param("from") String from,
			@Param("to") String to);
	
	public int truncate(@Param("site") String site,
			@Param("category") String category);
}
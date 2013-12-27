package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.SearchTypeRatioVO;

public interface SearchTypeRatioMapper {
	
	public void createTable(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype);

	public void createIndex(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype)
			throws Exception;

	public void validateTable(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype)
			throws Exception;

	public void dropTable(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype)
			throws Exception;	
	
	public SearchTypeRatioVO getEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId)
			throws Exception;
	
	public SearchTypeRatioVO getMinEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public SearchTypeRatioVO getMaxEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype)
			throws Exception;
	
	public List<String> listTypes(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("from") String from, @Param("to") String to);

	public List<SearchTypeRatioVO> getEntryListBetween(
			@Param("site") String site, @Param("category") String category,
			@Param("stype") String stype, @Param("dFilter") String dFilter,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to, @Param("isGroup") boolean isGroup);
	
	public int getCountBetween(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int getSumBetween(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dFilter") String dFilter, @Param("dtype") String dtype,
			@Param("from") String from, @Param("to") String to);
	
	public int putEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int updateEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId,
			@Param("hit") int hit);
	
	public int deleteEntry(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("timeId") String timeId);
	
	public int deleteEntryBetween(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype,
			@Param("dtype") String dtype, @Param("from") String from,
			@Param("to") String to);

	public int truncate(@Param("site") String site,
			@Param("category") String category, @Param("stype") String stype);
}

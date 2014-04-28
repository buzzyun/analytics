package org.fastcatsearch.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.KeywordSuggestionVO;

/*
 * 키워드추천(자동완성) 데이터 테이블.
 * */
public interface KeywordSuggestionMapper extends AnalyticsMapper {
	
	//like검색을 수행한다. %keyword%
	public List<KeywordSuggestionVO> getEntryList(@Param("start") int start, @Param("end") int end, @Param("search") String search) throws Exception;
	
	public void putEntry(String keyword) throws Exception;
	
	public void deleteEntry(@Param("id") int id);
	
}

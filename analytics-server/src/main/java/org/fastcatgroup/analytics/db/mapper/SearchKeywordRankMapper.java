package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;

/*
 * 인기키워드 테이블.
 * int id가 자동증가 pk이다. 
 * */
public interface SearchKeywordRankMapper extends AnalyticsTypeMapper {
	
	public List<RankKeywordVO> getEntryList(@Param("siteId") String siteId, @Param("typeId") String typeId, @Param("categoryId") String categoryId, @Param("timeId") String timeId) throws Exception;
	
	public int getCount(@Param("siteId") String siteId, @Param("typeId") String typeId, @Param("categoryId") String categoryId, @Param("timeId") String timeId) throws Exception;
	
	public void putEntry(@Param("siteId") String siteId, @Param("typeId") String typeId, @Param("vo") RankKeywordVO vo) throws Exception;
	
	public void updateEntry(@Param("siteId") String siteId, @Param("typeId") String typeId, @Param("vo") RankKeywordVO vo);
	
}

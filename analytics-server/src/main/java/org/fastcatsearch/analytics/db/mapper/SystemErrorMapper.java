package org.fastcatsearch.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.SystemErrorVO;
/**
 * task 수행 결과 데이터 mapper
 * */
public interface SystemErrorMapper extends SystemMapper {
	
	public List<SystemErrorVO> getEntryList(@Param("start") int start, @Param("end") int end) throws Exception;
	
	public int putEntry(@Param("SystemErrorVO") SystemErrorVO entry) throws Exception;
	
}
package org.fastcatsearch.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatsearch.analytics.db.vo.TaskResultVO;
/**
 * task 수행 결과 데이터 mapper
 * */
public interface TaskResultMapper extends SystemMapper {
	
	public List<TaskResultVO> getEntryList(@Param("siteId") String siteId,
			@Param("targetTime") String targetTime) throws Exception;
	
	public TaskResultVO getEntry(@Param("siteId") String siteId,
			@Param("targetTime") String targetTime, @Param("taskId") String taskId) throws Exception;
	
	public int putEntry(TaskResultVO entry) throws Exception;
	
	public int updateEntry(TaskResultVO entry) throws Exception;
	
	public int deleteEntry(@Param("siteId") String siteId,
			@Param("targetTime") String targetTime) throws Exception;
	
}
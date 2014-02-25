package org.fastcatgroup.analytics.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.fastcatgroup.analytics.db.vo.UserAccountVO;

public interface UserAccountMapper extends AnalyticsMapper {
	
	public UserAccountVO getEntry(@Param("id") int id) throws Exception;
	
	public UserAccountVO getEntryByUserId(@Param("userId") String userId) throws Exception;
	
	public List<UserAccountVO> getEntryList() throws Exception;
	
	public int getCount() throws Exception;
	
	public int getMaxId() throws Exception;
	
	public void putEntry (UserAccountVO vo) throws Exception;
	
	public void updateEntry (UserAccountVO vo) throws Exception;
	
	public void deleteEntry (@Param("id") int id) throws Exception;
}
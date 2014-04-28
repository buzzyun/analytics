package org.fastcatsearch.analytics.http;

import org.fastcatsearch.analytics.db.vo.UserAccountVO;

public class SessionInfo {

	private String userId;
	private Object info;

	public SessionInfo(String userId, Object info) {
		this.userId = userId;
		this.info = info;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isAdmin() {
		if (info != null) {
			return UserAccountVO.TYPE_ADMIN.equals(info.toString());
		} else {
			return false;
		}
	}

}

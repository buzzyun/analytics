package org.fastcatsearch.analytics.http;

/**
 * */
public enum ActionAuthorityLevel {
	USER, ADMIN;
	
	public boolean isLargerThan(ActionAuthorityLevel level){
		//admin은 무조건 가능.
		if(level == ADMIN){
			return true;
		}
		
		//user는 user허용 일때만 가능 
		if(level == USER){
			return this == USER;
		}
		
		return false;
		
	}
}

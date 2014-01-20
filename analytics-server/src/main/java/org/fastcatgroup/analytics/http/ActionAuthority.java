package org.fastcatgroup.analytics.http;

/**
 * 여기에 action 권한들을 정리한다.
 * */
public enum ActionAuthority {
	NULL ("NULL"), 
	Analysis_Dictionary ("Analysis Dictionary"), 
	Collection_Schema ("Collection Schema"), 
	Collection_Indexing  ("Collection Indexing"),
	Keyword  ("Keyword");
	
	private String name;
	private String code;
	
	private ActionAuthority(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

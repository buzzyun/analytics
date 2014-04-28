package org.fastcatsearch.analytics.db.vo;

import org.fastcatsearch.analytics.util.MessageDigestUtils;


public class UserAccountVO {
	public static final String ADMIN_USER_NAME = "Administrator";
	public static final String ADMIN_USER_ID = "admin";
	
	public static final String TYPE_ADMIN = "ADMIN";
	public static final String TYPE_USER = "USER";
	
	public int id;
	public String name;
	public String userId;
	public String password;
	public String email;
	public String sms;
	
	public UserAccountVO(){ }
	
	public UserAccountVO(String name, String userId, String password, String email, String sms) {
		this.name = name;
		this.userId = userId;
		this.email = email;
		this.sms = sms;
		
		setEncryptedPassword(password);
	}
	
	public void setEncryptedPassword(String password){
		this.password = MessageDigestUtils.getSHA1String(password);
	}
	
	public boolean isEqualsEncryptedPassword(String password){
		return this.password.equalsIgnoreCase(MessageDigestUtils.getSHA1String(password));
	}
}

package org.fastcatsearch.analytics.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target ({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface ActionMapping {
	String value();
	ActionMethod[] method() default {ActionMethod.GET, ActionMethod.POST};
	//기본적으로 USER. admin 은 annotation에 설정해주었을 경우만.
	ActionAuthorityLevel authorityLevel() default ActionAuthorityLevel.USER; 
}

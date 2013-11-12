package org.fastcatgroup.analytics.control;

public class ExecutorMaxCapacityExceedException extends Exception {
	public ExecutorMaxCapacityExceedException(String error){
		super(error);
	}
	
	public ExecutorMaxCapacityExceedException(Throwable e){
		super(e);
	}
}

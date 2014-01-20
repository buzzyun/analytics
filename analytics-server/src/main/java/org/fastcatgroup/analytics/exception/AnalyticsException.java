package org.fastcatgroup.analytics.exception;

public class AnalyticsException extends Exception {

	public AnalyticsException(String message) {
		super(message);
	}
	public AnalyticsException(String message, Throwable e) {
		super(message, e);
	}
	private static final long serialVersionUID = 4554086457129418597L;

}

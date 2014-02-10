package org.fastcatgroup.analytics.analysis;

public class ProcessDropException extends Exception {
	private static final long serialVersionUID = -3509052391318916934L;

	public ProcessDropException(String message) {
		super(message);
	}

	public ProcessDropException(String message, Throwable e) {
		super(message, e);
	}

	public ProcessDropException(Throwable e) {
		super(e);
	}
}

package org.fastcatsearch.analytics.job;

import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.omg.CORBA.portable.Streamable;

public abstract class StreamableJob extends Job implements Streamable {

	private static final long serialVersionUID = -799321199758563930L;

	@Override
	public abstract JobResult doRun() throws AnalyticsException;

}

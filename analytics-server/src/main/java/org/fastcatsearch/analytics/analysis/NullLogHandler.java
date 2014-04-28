package org.fastcatsearch.analytics.analysis;

import java.io.IOException;

import org.fastcatsearch.analytics.analysis.handler.CategoryLogHandler;
import org.fastcatsearch.analytics.analysis.log.LogData;

public class NullLogHandler<LogType extends LogData> extends CategoryLogHandler<LogType> {

	public NullLogHandler(String categoryId) {
		super(categoryId);
	}

	@Override
	public void handleLog(LogType logData) throws IOException { }

	@Override
	public Object done() throws IOException { return null; }
}

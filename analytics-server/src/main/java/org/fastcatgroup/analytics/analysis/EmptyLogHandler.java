package org.fastcatgroup.analytics.analysis;

import java.io.IOException;

import org.fastcatgroup.analytics.analysis.handler.CategoryLogHandler;
import org.fastcatgroup.analytics.analysis.log.LogData;

@SuppressWarnings("rawtypes")
public class EmptyLogHandler extends CategoryLogHandler {

	public EmptyLogHandler(String categoryId) {
		super(categoryId);
	}

	@Override
	public void handleLog(LogData logData) throws IOException { }

	@Override
	public Object done() throws IOException { return null; }
}

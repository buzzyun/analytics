package org.fastcatsearch.analytics.analysis;

import org.fastcatsearch.analytics.analysis.log.LogData;

public interface LogValidator<LogType extends LogData> {

	public boolean isValid(LogType logData);

}

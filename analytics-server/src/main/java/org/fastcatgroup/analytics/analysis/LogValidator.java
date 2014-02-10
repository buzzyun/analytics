package org.fastcatgroup.analytics.analysis;

import org.fastcatgroup.analytics.analysis.log.LogData;

public interface LogValidator<LogType extends LogData> {

	public boolean isValid(LogType logData);

}

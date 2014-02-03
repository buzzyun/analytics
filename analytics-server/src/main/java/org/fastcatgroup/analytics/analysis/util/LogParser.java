package org.fastcatgroup.analytics.analysis.util;

import org.fastcatgroup.analytics.analysis.log.LogData;

/**
 * Raw 로그파일을 한줄씩 해석하여 읽어들이는 reader.
 * */
public abstract class LogParser<LogType extends LogData> {

	public abstract LogType parseLine(String line);

}

package org.fastcatgroup.analytics.analysis.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SourceLogReader<LogType extends LogData> {
	public static Logger logger = LoggerFactory.getLogger(SourceLogReader.class);
	
	public abstract LogType readLog();
	
	public abstract void close();

}

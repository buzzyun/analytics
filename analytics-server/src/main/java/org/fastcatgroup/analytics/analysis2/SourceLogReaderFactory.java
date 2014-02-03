package org.fastcatgroup.analytics.analysis2;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SourceLogReaderFactory<LogType extends LogData> {
	public static Logger logger = LoggerFactory.getLogger(SourceLogReaderFactory.class);
	
	public SourceLogReader<LogType> createReader();
}

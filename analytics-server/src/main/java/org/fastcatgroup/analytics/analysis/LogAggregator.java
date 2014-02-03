package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.util.DirBufferedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LogAggregator<LogType extends LogData> {

	protected static Logger logger = LoggerFactory.getLogger(LogAggregator.class);

	private File[] inFileList;
	private String encoding;
	private List<AbstractLogAggregator<LogType>> handlerList;

	public LogAggregator(File[] inFileList, String encoding, List<AbstractLogAggregator<LogType>> handlerList) {
		this.inFileList = inFileList;
		this.encoding = encoding;
		this.handlerList = handlerList;
	}

	public void aggregate() {

		try {
			DirBufferedReader lineReader = new DirBufferedReader(inFileList, encoding);
			String line = null;
			while ((line = lineReader.readLine()) != null) {
//				logger.debug("line > {}", line);
				// 여러 핸들러가 수행한다.
				for (AbstractLogAggregator<LogType> h : handlerList) {
					h.handleLog(line);
				}
			}
			
			for (AbstractLogAggregator<LogType> h : handlerList) {
				h.done();
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	

}

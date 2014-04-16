package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.AbstractLogAggregator;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.ClickLogReader;
import org.fastcatgroup.analytics.util.Counter;
/**
 * 이전 n 일간 데이터를 머징한다.
 * */
public class MergeNDaysClickTypeCountProcessHandler extends ProcessHandler {
	
	int fileLimitCount;
	String encoding;
	File[] inFileList;
	Map<String, Counter> counterMap;
	AbstractLogAggregator<ClickLog> aggregator;
	int runCase;

	public MergeNDaysClickTypeCountProcessHandler(File[] inFileList,
			String encoding, AbstractLogAggregator<ClickLog> aggregator, int runCase) {
		this.encoding = encoding;
		this.aggregator = aggregator;
		this.inFileList = inFileList;
		this.counterMap = new HashMap<String, Counter>();
		this.runCase = runCase;
	}

	@Override
	public Object process(Object parameter) {
		try {
			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making keyword process due to no working log files at {}");
				return null;
			}

			ClickLogReader logReader = new ClickLogReader(inFileList, encoding);
			
			for (ClickLog log = null; (log = logReader.readLog()) != null;) {
				aggregator.handleLog(log);
			}
			
			aggregator.done();
			
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}
		
		return null;
	}
}
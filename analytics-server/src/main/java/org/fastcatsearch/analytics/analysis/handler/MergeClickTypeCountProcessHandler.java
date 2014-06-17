package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.analytics.analysis.AbstractLogAggregator;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.log.ClickLogReader;

public class MergeClickTypeCountProcessHandler extends ProcessHandler {
	
	public static final int RUN_CASE_CLICK = 1;
	public static final int RUN_CASE_CLICK_KEYWORD = 2;
	public static final int RUN_CASE_CLICK_KEYWORD_TARGET = 3;
	
	String encoding;
	File[] inFileList;
	AbstractLogAggregator<ClickLog> aggregator;
	int runCase;

	public MergeClickTypeCountProcessHandler(File[] inFileList,
			String encoding, AbstractLogAggregator<ClickLog> aggregator, int runCase) {
		this.encoding = encoding;
		this.aggregator = aggregator;
		this.inFileList = inFileList;
		this.runCase = runCase;
	}

	@Override
	public Object process(Object parameter) {
		//logger.debug("start process.. ");
		ClickLogReader logReader = null;
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making keyword process due to no working log files at {}");
				return null;
			}

			//logger.debug("reading files : {} {}", "", inFileList);
			
			logReader = new ClickLogReader(inFileList, encoding);
			
			for (ClickLog log = null; (log = logReader.readLog()) != null;) {
				ClickLog log2 = log;

				switch (runCase) {
				case RUN_CASE_CLICK:
					log2 = new ClickLog("", log.getClickType(), "", "");
					break;
				case RUN_CASE_CLICK_KEYWORD:
					log2 = new ClickLog("", log.getKeyword(), "", log.getClickType());
					break;
				case RUN_CASE_CLICK_KEYWORD_TARGET:
					log2 = new ClickLog("", log.getKeyword(), log.getClickId(), log.getClickType());
					break;
				}

				aggregator.handleLog(log2);
			}
			
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			
			if(logReader!=null) try {
				logReader.close();
			} catch (Exception e) { }
			
			if(aggregator != null) {
				try {
					aggregator.done();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		
		return null;
	}
}
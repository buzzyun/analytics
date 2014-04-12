package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.AbstractLogAggregator;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.ClickLogReader;
import org.fastcatgroup.analytics.util.Counter;

public class MergeClickTypeCountProcessHandler extends ProcessHandler {
	
	public static final int RUN_CASE_CLICK = 1;
	public static final int RUN_CASE_CLICK_KEYWORD = 2;
	public static final int RUN_CASE_CLICK_KEYWORD_TARGET = 3;
	
	int fileLimitCount;
	String encoding;
	File[] inFileList;
	Map<String, Counter> counterMap;
	AbstractLogAggregator<ClickLog> aggregator;
	int runCase;

	public MergeClickTypeCountProcessHandler(File[] inFileList,
			String encoding, AbstractLogAggregator<ClickLog> aggregator, int runCase) {
		this.encoding = encoding;
		this.aggregator = aggregator;
		this.inFileList = inFileList;
		this.counterMap = new HashMap<String, Counter>();
		this.runCase = runCase;
	}

	@Override
	public Object process(Object parameter) {
		//logger.debug("start process.. ");
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making keyword process due to no working log files at {}");
				return null;
			}

			//logger.debug("reading files : {} {}", "", inFileList);
			
			ClickLogReader logReader = new ClickLogReader(inFileList, encoding);
			
			for (ClickLog log = null; (log = logReader.readLog()) != null;) {
				ClickLog log2 = log;
				
				switch(runCase) {
				case RUN_CASE_CLICK:
					log2 = new ClickLog("",log.getClickType(),"","");
					break;
				case RUN_CASE_CLICK_KEYWORD:
					log2 = new ClickLog("",log.getKeyword()+"\t"+log.getClickType(),"","");
					break;
				case RUN_CASE_CLICK_KEYWORD_TARGET:
					log2 = new ClickLog("",log.getKeyword()+"\t"+log.getClickId()+"\t"+log.getClickType(),"","");
					break;
				}
				
				aggregator.handleLog(log2);
				//logger.debug("handling log : {}", log2);
			}
			
			aggregator.done();
			
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}
		
		return null;
	}
}
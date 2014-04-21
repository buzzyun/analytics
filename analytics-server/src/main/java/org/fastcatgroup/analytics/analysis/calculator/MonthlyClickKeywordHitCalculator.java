package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.AbstractLogAggregator;
import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.handler.MergeClickTypeCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateClickKeywordTargetTypeCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateClickKeywordTypeCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateClickTypeCountHandler;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class MonthlyClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	private int topCount;
	
	public MonthlyClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		int minimumHitCount = 0;
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = StatisticsUtils.encoding;
			
			File workingDir = new File(StatisticsUtils.getDayDataDir(baseDir, calendar), siteId);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = StatisticsUtils.getTimeId(calendar, Calendar.MONTH);
			int runKeySize = StatisticsUtils.runKeySize;
			
			//
			//1일부터 현재일자 (DAY_OF_MONTH) 까지.
			int diff = calendar.get(Calendar.DAY_OF_MONTH);
			
			File[] clickLogFiles = new File[diff];
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(new File(timeDir, siteId), CLICK_RAW_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			//1달치의 일자별 click-row log들을 머징한다.
			
			logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			
			/*
			 * 1. type별 클릭수.
			 * */
			EntryParser<KeyCountRunEntry> clickTypeParser = new KeyCountRunEntryParser(new int[] {0}, 3);
			AbstractLogAggregator<ClickLog> clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumHitCount, clickTypeParser);
			ProcessHandler mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK).attachProcessTo(categoryProcess);
			ProcessHandler updateClickTypeCountHandler = new UpdateClickTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
			
			/*
			 * 2. 키워드별 type별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 2}, 3 );
			clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumHitCount, clickTypeParser);
			mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD).appendTo(updateClickTypeCountHandler);
			updateClickTypeCountHandler = new UpdateClickKeywordTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
			
			/*
			 * 3. 키워드별 type별 클릭대상별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumHitCount, clickTypeParser);
			mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD_TARGET).appendTo(updateClickTypeCountHandler);
			updateClickTypeCountHandler = new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
		}
		return categoryProcess;
	}
	
}

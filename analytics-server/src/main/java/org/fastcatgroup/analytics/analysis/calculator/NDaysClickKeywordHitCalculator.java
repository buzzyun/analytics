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
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
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
public class NDaysClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	private int topCount;
	
	public NDaysClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		int nDays = 90; //90일.
		
		int minimumHitCount = 0;
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = SearchStatisticsProperties.encoding;
			
			File workingDir = new File(SearchStatisticsProperties.getDayDataDir(baseDir, calendar), siteId);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DAY_OF_MONTH);
			int runKeySize = SearchStatisticsProperties.runKeySize;
			
			File[] clickLogFiles = new File[nDays];
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < nDays; inx++) {
				File timeDir = SearchStatisticsProperties.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(new File(timeDir, siteId), CLICK_RAW_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			//nDays치의 일자별 click-row log들을 머징한다.
			logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
			File file = new File(workingDir, CLICK_TARGET_FILENAME);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			KeyCountRunEntryParser clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			KeyCountLogAggregator<ClickLog> clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, CLICK_TARGET_FILENAME, runKeySize, encoding, minimumHitCount, clickTypeParser);
			ProcessHandler mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD_TARGET).attachProcessTo(categoryProcess);
			new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
		}
		return categoryProcess;
	}
	
}
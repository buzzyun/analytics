package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
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
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = SearchStatisticsProperties.encoding;
			
			File workingDir = new File(SearchStatisticsProperties.getMonthDataDir(baseDir, calendar), siteId);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.MONTH);
			
			//
			//1일부터 현재일자 (DAY_OF_MONTH) 까지.
			int diff = calendar.get(Calendar.DAY_OF_MONTH);
			
			File[] clickCountFiles = new File[diff];
			File[] clickKeywordCountFiles = new File[diff];
			File[] clickKeywordTargetCountFiles = new File[diff];
			
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = SearchStatisticsProperties.getDayDataDir(baseDir, dailyCalendar);
				clickCountFiles[inx] = new File(new File(timeDir, siteId), CLICK_COUNT_FILENAME);
				clickKeywordCountFiles[inx] = new File(new File(timeDir, siteId), CLICK_KEYWORD_COUNT_FILENAME);
				clickKeywordTargetCountFiles[inx] = new File(new File(timeDir, siteId), CLICK_KEYWORD_TARGET_COUNT_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}

			//1달치의 일자별 click log들을 머징한다.
			logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
			
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			/*
			 * 1. type별 클릭수.
			 * */
			EntryParser<KeyCountRunEntry> clickTypeParser = new KeyCountRunEntryParser(new int[] {0}, 1);
			ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(clickCountFiles, workingDir, RUN_CLICK_TYPE_FILENAME, encoding, clickTypeParser).attachProcessTo(categoryProcess);
			ProcessHandler updateClickTypeCountHandler = new UpdateClickTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
			
			/*
			 * 2. 키워드별 type별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1}, 2 );
			mergeKeyCount = new MergeKeyCountProcessHandler(clickKeywordCountFiles, workingDir, RUN_CLICK_TYPE_FILENAME, encoding, clickTypeParser).appendTo(updateClickTypeCountHandler);
			updateClickTypeCountHandler = new UpdateClickKeywordTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
			
			/*
			 * 3. 키워드별 type별 클릭대상별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			mergeKeyCount = new MergeKeyCountProcessHandler(clickKeywordTargetCountFiles, workingDir, RUN_CLICK_TYPE_FILENAME, encoding, clickTypeParser).appendTo(updateClickTypeCountHandler);
			updateClickTypeCountHandler = new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding).appendTo(mergeKeyCount);
			file.delete();
		}
		return categoryProcess;
	}
	
}

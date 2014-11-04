package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatsearch.analytics.analysis.AbstractLogAggregator;
import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.KeyCountLogAggregator;
import org.fastcatsearch.analytics.analysis.NullLogHandler;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.handler.MergeClickTypeCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickKeywordTargetTypeCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickKeywordTypeCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickTypeCountHandler;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.log.KeyCountLog;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class YearlyClickTypeHitCalculator extends Calculator<ClickLog> {
	
	private Calendar prevCalendar;
	
	public YearlyClickTypeHitCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = StatisticsProperties.encoding;
			
			int diff = StatisticsUtils.getMonthDiff(prevCalendar, calendar) + 1;
			
			File monthlyBaseDir = StatisticsUtils.getMonthDataDir(baseDir, calendar);
			
			File workingDir = StatisticsUtils.getYearDataDir(baseDir, calendar);
			
			if(!monthlyBaseDir.exists()) {
				try {
					FileUtils.forceMkdir(monthlyBaseDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = StatisticsUtils.getTimeId(calendar, Calendar.YEAR);
			int runKeySize = StatisticsProperties.runKeySize;
			
			StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
			int minimumClickCount = statisticsSettings.getCtrSetting().getMinimumClickCount();
			
			File[] clickTypeCountFiles = new File[diff];
			File[] clickKeyTypeCountFiles = new File[diff];
			File[] clickKeyTargetTypeCountFiles = new File[diff];
			Calendar monthlyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = StatisticsUtils.getMonthDataDir(baseDir, monthlyCalendar);
				clickTypeCountFiles[inx] = new File(timeDir, RUN_CLICK_TYPE_FILENAME);
				clickKeyTypeCountFiles[inx] = new File(timeDir, RUN_CLICK_KEY_TYPE_FILENAME);
				clickKeyTargetTypeCountFiles[inx] = new File(timeDir, RUN_CLICK_KEY_TARGET_TYPE_FILENAME);
				monthlyCalendar.add(Calendar.MONTH, -1);
			}
			
			//년도별은 월별 로그의 합산으로 처리.
			
			logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
			
			
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			/**
			 * type별 클릭수.
			 **/
			
			EntryParser<KeyCountRunEntry> entryParser = new KeyCountRunEntryParser();
			ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(clickTypeCountFiles, 
					workingDir, RUN_CLICK_TYPE_FILENAME, encoding, true, entryParser).attachProcessTo(categoryProcess);
			ProcessHandler updateClickTypeCountHandler = new UpdateClickTypeCountHandler(siteId, timeId, file
					,encoding, true).appendTo(mergeKeyCount);
			
			/*
			 * 2. 키워드별 type별 클릭수.
			 * */
			entryParser = new KeyCountRunEntryParser(new int[] {0, 1}, 2);
			mergeKeyCount = new MergeKeyCountProcessHandler(clickKeyTypeCountFiles, 
					workingDir, RUN_CLICK_TYPE_FILENAME, encoding, true, entryParser).appendTo(updateClickTypeCountHandler);
			//updateClickTypeCountHandler = new UpdateClickKeywordTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			
			
			/*
			 * 3. 키워드별 type별 클릭대상별 클릭수.
			 * */
			entryParser = new KeyCountRunEntryParser(new int[] {0, 1, 2}, 3);
			mergeKeyCount = new MergeKeyCountProcessHandler(clickKeyTargetTypeCountFiles, 
					workingDir, RUN_CLICK_TYPE_FILENAME, encoding, true, entryParser).appendTo(mergeKeyCount);
			//updateClickTypeCountHandler = new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
		}
		return categoryProcess;
	}
	
}

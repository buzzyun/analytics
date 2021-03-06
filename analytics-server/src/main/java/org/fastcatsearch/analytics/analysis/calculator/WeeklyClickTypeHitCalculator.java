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
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickKeywordTargetTypeCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickKeywordTypeCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateClickTypeCountHandler;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class WeeklyClickTypeHitCalculator extends Calculator<ClickLog> {
	
	private Calendar prevCalendar;
	
	public WeeklyClickTypeHitCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = StatisticsProperties.encoding;
			
			File dailyBaseDir = StatisticsUtils.getDayDataDir(baseDir, calendar);
			
			File workingDir = StatisticsUtils.getWeekDataDir(baseDir, calendar);
			
			if(!dailyBaseDir.exists()) {
				try {
					FileUtils.forceMkdir(dailyBaseDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = StatisticsUtils.getTimeId(calendar, Calendar.WEEK_OF_YEAR);
			int runKeySize = StatisticsProperties.runKeySize;
			StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
			int minimumClickCount = statisticsSettings.getCtrSetting().getMinimumClickCount();
			//
			//이번주말부터 이번주초까지
			int diff = StatisticsUtils.getDateDiff(prevCalendar, calendar) + 1;
			
			File[] clickLogFiles = new File[diff];
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(timeDir, CLICK_RAW_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			//1주일치의 일자별 click-row log들을 머징한다.
			
			logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			
			/**
			 * type별 클릭수.
			 **/
			EntryParser<KeyCountRunEntry> clickTypeParser = new KeyCountRunEntryParser(new int[] {0}, 3);
			AbstractLogAggregator<ClickLog> clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumClickCount, clickTypeParser);
			ProcessHandler mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK).attachProcessTo(categoryProcess);
			ProcessHandler updateClickTypeCountHandler = new UpdateClickTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			
			/*
			 * 키워드별 type별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 2}, 3 );
			clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumClickCount, clickTypeParser);
			mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD).appendTo(updateClickTypeCountHandler);
			//updateClickTypeCountHandler = new UpdateClickKeywordTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumClickCount, clickTypeParser);
			mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD_TARGET).appendTo(mergeKeyCount);
			//updateClickTypeCountHandler = new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			file.delete();
		}
		return categoryProcess;
	}
	
}

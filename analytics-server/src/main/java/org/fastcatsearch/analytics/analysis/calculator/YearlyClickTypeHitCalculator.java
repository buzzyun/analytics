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
			
			int diff = StatisticsUtils.getMonthDiff(prevCalendar, calendar);
			
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
			
			File[] clickKeyCountFiles = new File[diff];
			Calendar monthlyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = StatisticsUtils.getMonthDataDir(baseDir, monthlyCalendar);
				clickKeyCountFiles[inx] = new File(timeDir, CLICK_KEY_COUNT_FILENAME);
				monthlyCalendar.add(Calendar.MONTH, -1);
			}
			
			//년도별은 월별 로그의 합산으로 처리.
			
			logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
			
			
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			/**
			 * type별 클릭수.
			 **/
			
			KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
			
			ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(clickKeyCountFiles, 
					workingDir, RUN_CLICK_TYPE_FILENAME, encoding, entryParser).attachProcessTo(categoryProcess);
			
			new UpdateClickTypeCountHandler(siteId, timeId, file
					,new File(workingDir, RUN_CLICK_TYPE_FILENAME)
					,encoding).appendTo(mergeKeyCount);
			file.delete();
		}
		return categoryProcess;
	}
	
}

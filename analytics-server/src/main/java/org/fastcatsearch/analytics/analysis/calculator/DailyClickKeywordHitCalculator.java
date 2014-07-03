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
public class DailyClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	public DailyClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		int minimumClickCount = statisticsSettings.getCtrSetting().getMinimumClickCount();
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		//FIXME:클릭raw 로그를 읽어들이는 리딩소스는 1개이므로 loghandler 를 이용하도록 수정.
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
			String encoding = StatisticsProperties.encoding;
			File workingDir = StatisticsUtils.getDayDataDir(baseDir, calendar);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			String timeId = StatisticsUtils.getTimeId(calendar, Calendar.DAY_OF_MONTH);
			int runKeySize = StatisticsProperties.runKeySize;
			
			//
			//현재날자 클릭로그 
			File[] clickLogFiles = new File[] { new File(StatisticsUtils.getDayDataDir(baseDir, calendar), CLICK_RAW_FILENAME) };
			
			logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
			File file = new File(workingDir, RUN_CLICK_TYPE_FILENAME);
			/*
			 * type별 클릭수.
			 * */
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
			updateClickTypeCountHandler = new UpdateClickKeywordTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, RUN_CLICK_TYPE_FILENAME, runKeySize, encoding, minimumClickCount, clickTypeParser);
			mergeKeyCount = new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD_TARGET).appendTo(updateClickTypeCountHandler);
			updateClickTypeCountHandler = new UpdateClickKeywordTargetTypeCountHandler(siteId, timeId, file, encoding, true).appendTo(mergeKeyCount);
			file.delete();
		}
		return categoryProcess;
	}
	
}

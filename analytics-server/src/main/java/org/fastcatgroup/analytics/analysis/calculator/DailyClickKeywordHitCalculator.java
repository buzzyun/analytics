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
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class DailyClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	private int topCount;
	
	public DailyClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		int minimumHitCount = 0;
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		//FIXME:클릭raw 로그를 읽어들이는 리딩소스는 1개이므로 loghandler 를 이용하도록 수정.
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
			String encoding = StatisticsUtils.encoding;
			File workingDir = new File(StatisticsUtils.getDayDataDir(baseDir, calendar), siteId);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			int runKeySize = StatisticsUtils.runKeySize;
			
			//
			//현재날자 클릭로그 
			File[] clickLogFiles = new File[] { new File(
				new File(StatisticsUtils.getDayDataDir(baseDir,
					calendar), siteId), CLICK_RAW_FILENAME) };
			
			logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			EntryParser<KeyCountRunEntry> clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1, 2}, 3 );
			AbstractLogAggregator<ClickLog> clickTypeLogAggregator = new KeyCountLogAggregator<ClickLog>(workingDir, CLICK_COUNT_FILENAME, runKeySize, encoding, minimumHitCount, clickTypeParser);
			new MergeClickTypeCountProcessHandler(
					clickLogFiles, encoding, clickTypeLogAggregator,
					MergeClickTypeCountProcessHandler.RUN_CASE_CLICK_KEYWORD_TARGET).attachProcessTo(categoryProcess);
		}
		return categoryProcess;
	}
	
}

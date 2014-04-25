package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.StatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class NDaysClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	private int nDays;
	private String targetFilePath;
	
	public NDaysClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, int nDays, String targetFilePath) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.nDays = nDays;
		this.targetFilePath = targetFilePath;
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
			
		if(categoryId.equals("_root")) {
		
			String encoding = StatisticsProperties.encoding;
			
			File workingDir = new File(StatisticsUtils.getDayDataDir(baseDir, calendar), siteId);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			File[] clickLogFiles = new File[nDays];
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < nDays; inx++) {
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(new File(timeDir, siteId), CLICK_COUNT_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			//nDays치의 일자별 click-row log들을 머징한다.
			logger.debug("Process Dir = {}, nDays = {}", workingDir.getAbsolutePath(), nDays);
			//File file = new File(workingDir, CLICK_TARGET_FILENAME);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			KeyCountRunEntryParser clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1}, 3 );
			
			File file = new File(workingDir, CLICK_TARGET_FILENAME);
			MergeKeyCountProcessHandler mergeProcessHandler = new MergeKeyCountProcessHandler(clickLogFiles, workingDir, CLICK_TARGET_FILENAME, encoding, clickTypeParser);
			
			logger.trace("targetFilePath:{}", targetFilePath);
			
			if(targetFilePath!=null && !"".equals(targetFilePath)) {
				
				try {
					logger.trace("click log copy {} -> {}", file.getAbsolutePath(), targetFilePath);
					FileUtils.copyFile(file, new File(targetFilePath));
				} catch (IOException e) {
					logger.error("", e);
				};
			}
			
			//가중값배열.
			float[] weightList = mergeProcessHandler.weightList();
			for(int inx=0;inx<weightList.length;inx++) {
				if(inx > 0) {
					//인덱스에서 멀어질 수록 낮은 가중치를 가지도록.
					weightList[inx] = weightList[inx - 1] * 0.9f;
				}
			}
			mergeProcessHandler.attachProcessTo(categoryProcess);
		}
		return categoryProcess;
	}
}

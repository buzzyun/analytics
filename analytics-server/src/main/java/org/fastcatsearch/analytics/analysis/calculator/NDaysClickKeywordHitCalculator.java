package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatsearch.analytics.analysis.NullLogHandler;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CTRSetting;
import org.fastcatsearch.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.MoveFileHandler;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class NDaysClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	public NDaysClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		
		CTRSetting ctrSetting = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getCtrSetting();
		Integer nDays = ctrSetting.getDumpFileDaySize();
		String targetFilePath = ctrSetting.getTargetFilePath();
		logger.debug("targetFilePath:{}", targetFilePath);

		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);

		if(nDays == null || nDays <= 0){
			return categoryProcess;
		}

		if(categoryId.equals("_root")) {
		
			String encoding = StatisticsProperties.encoding;
			
			File workingDir = StatisticsUtils.getDayDataDir(baseDir, calendar);
			
			if(!workingDir.exists()) {
				try {
					FileUtils.forceMkdir(workingDir);
				} catch (IOException ignore) { }
			}
			
			//가중값배열.
			float[] weightList = new float[nDays];
			Arrays.fill(weightList, 1.0f);
			float decayFactor = ctrSetting.getFileDailyDecayFactor();
			
			File[] clickLogFiles = new File[nDays];
			Calendar dailyCalendar = (Calendar) calendar.clone();
			for (int inx = 0; inx < nDays; inx++) {
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(timeDir, CLICK_COUNT_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
				if(inx > 0) {
					//인덱스에서 멀어질 수록 낮은 가중치를 가지도록.
					weightList[inx] = weightList[inx - 1] * decayFactor;
				}
			}
			
			
			//nDays치의 일자별 click-row log들을 머징한다.
			logger.debug("Process Dir = {}, nDays = {}", workingDir.getAbsolutePath(), nDays);
			//File file = new File(workingDir, CLICK_TARGET_FILENAME);
			
			/*
			 * 키워드별 type별 클릭대상별 클릭수.
			 * */
			KeyCountRunEntryParser clickTypeParser = new KeyCountRunEntryParser(new int[]{0, 1}, 3 );
			
			File file = new File(workingDir, CLICK_TARGET_FILENAME);
			MergeKeyCountProcessHandler mergeProcessHandler = new MergeKeyCountProcessHandler(clickLogFiles, weightList, workingDir, CLICK_TARGET_FILENAME, encoding, true, clickTypeParser);
			
			mergeProcessHandler.attachProcessTo(categoryProcess);
			
			if (targetFilePath != null && !"".equals(targetFilePath)) {
				File destFile = new File(targetFilePath);
				new MoveFileHandler(workingDir, CLICK_TARGET_FILENAME, destFile.getParentFile(), destFile.getName()).appendTo(mergeProcessHandler);
			}
		}
		return categoryProcess;
	}
}

package org.fastcatsearch.analytics.analysis.calculator;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RELATE_KEY_COUNT_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RELATE_KEY_COUNT_RANK_FILENAME;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatsearch.analytics.analysis.RelateSearchLogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.RelateSearchLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateRelateKeywordHandler;
import org.fastcatsearch.analytics.analysis.log.RelateKeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.RelateSearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

/**
 * 연관검색어 계산기.
 * */
public class DailyRelateKeywordCalculator extends Calculator<RelateSearchLog> {
	
	//minimumHitCount번 이상 출현해야 연관어로 인정.
	
	public DailyRelateKeywordCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<RelateSearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		int maxKeywordLength = statisticsSettings.getSiteProperties().getMaxKeywordLength();
		int runKeySize = StatisticsProperties.runKeySize;
		Set<String> banWords = statisticsSettings.getSiteProperties().getBanwordSet();
		int minimumHitCount = statisticsSettings.getRelateKeywordSetting().getMinimumHitCount();
		
		logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
		RelateKeyCountRunEntryParser entryParser = new RelateKeyCountRunEntryParser();
		RelateSearchLogValidator logValidator = new RelateSearchLogValidator(banWords, maxKeywordLength);
		CategoryProcess<RelateSearchLog> categoryProcess = new CategoryProcess<RelateSearchLog>(categoryId);
		new RelateSearchLogKeyCountHandler(categoryId, workingDir, RELATE_KEY_COUNT_FILENAME, minimumHitCount, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		/* 1. count로 정렬하여 relate-key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, RELATE_KEY_COUNT_FILENAME, RELATE_KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).attachProcessTo(categoryProcess);
		
		/* 2. 구해진 연관검색어를 저장한다. */
		new UpdateRelateKeywordHandler(siteId, new File(workingDir, RELATE_KEY_COUNT_RANK_FILENAME), encoding).appendTo(logSort);
		
		return categoryProcess;
	}
	
}

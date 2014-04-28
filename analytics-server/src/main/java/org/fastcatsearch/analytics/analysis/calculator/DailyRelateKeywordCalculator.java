package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatsearch.analytics.analysis.RelateSearchLogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.RelateSearchLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateRelateKeywordHandler;
import org.fastcatsearch.analytics.analysis.log.RelateKeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.RelateSearchLog;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 연관검색어 계산기.
 * */
public class DailyRelateKeywordCalculator extends Calculator<RelateSearchLog> {
	
	private Set<String> banWords;
	//minimumHitCount번 이상 출현해야 연관어로 인정.
	private int minimumHitCount;
	
	public DailyRelateKeywordCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
	}
	
	@Override
	protected CategoryProcess<RelateSearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		
		int runKeySize = StatisticsProperties.runKeySize;

		logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
		RelateKeyCountRunEntryParser entryParser = new RelateKeyCountRunEntryParser();
		RelateSearchLogValidator logValidator = new RelateSearchLogValidator(banWords);
		CategoryProcess<RelateSearchLog> categoryProcess = new CategoryProcess<RelateSearchLog>(categoryId);
		new RelateSearchLogKeyCountHandler(categoryId, workingDir, RELATE_KEY_COUNT_FILENAME, minimumHitCount, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		/* 1. count로 정렬하여 relate-key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, RELATE_KEY_COUNT_FILENAME, RELATE_KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).attachProcessTo(categoryProcess);
		
		/* 2. 구해진 연관검색어를 저장한다. */
		new UpdateRelateKeywordHandler(siteId, new File(workingDir, RELATE_KEY_COUNT_RANK_FILENAME)).appendTo(logSort);
		
		return categoryProcess;
	}
	
}

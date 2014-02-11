package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.RelateSearchLogValidator;
import org.fastcatgroup.analytics.analysis.SearchLogValidator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.RelateSearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateRelateKeywordHandler;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.RelateKeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;

/**
 * 연관검색어 계산기.
 * */
public class DailyRelateKeywordCalculator extends Calculator<RelateSearchLog> {
	
	private Set<String> banWords;
	//minimumHitCount번 이상 출현해야 연관어로 인정.
	private int minimumHitCount;
	private int topCount;
	
	public DailyRelateKeywordCalculator(String name, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, baseDir, siteId, categoryIdList);
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<RelateSearchLog> newCategoryProcess(String categoryId){
		String encoding = SearchStatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		
		int runKeySize = SearchStatisticsProperties.runKeySize;

		String RELATE_KEY_COUNT_FILENAME = "relate-key-count.log";
		String RELATE_KEY_COUNT_RANK_FILENAME = "relate-key-count-rank.log";
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		RelateKeyCountRunEntryParser entryParser = new RelateKeyCountRunEntryParser();
		RelateSearchLogValidator logValidator = new RelateSearchLogValidator(banWords);
		CategoryProcess<RelateSearchLog> categoryProcess = new CategoryProcess<RelateSearchLog>(categoryId);
		new RelateSearchLogKeyCountHandler(categoryId, workingDir, RELATE_KEY_COUNT_FILENAME, minimumHitCount, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		/* 1. count로 정렬하여 relate-key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, RELATE_KEY_COUNT_FILENAME, RELATE_KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).attachProcessTo(categoryProcess);
		
		/* 2. 구해진 연관검색어를 저장한다. */
		new UpdateRelateKeywordHandler(siteId, categoryId, new File(workingDir, RELATE_KEY_COUNT_RANK_FILENAME)).appendTo(logSort);
		
		return categoryProcess;
	}
	
}

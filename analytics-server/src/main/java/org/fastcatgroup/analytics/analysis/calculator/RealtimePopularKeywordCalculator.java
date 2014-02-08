package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.RealtimePopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

/**
 * 실시간 인기검색어 계산기.
 * 
 * */
public class RealtimePopularKeywordCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public RealtimePopularKeywordCalculator(String name, File baseDir, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, baseDir, categoryIdList);
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = SearchStatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		int runKeySize = SearchStatisticsProperties.runKeySize;

		String KEY_COUNT_FILENAME = "key-count.log";
		String KEY_COUNT_RANK_FILENAME = "key-count-rank.log";
		String KEY_COUNT_RANK_PREV_FILENAME = "key-count-rank-prev.log";
		
		logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, KEY_COUNT_RANK_PREV_FILENAME, encoding, runKeySize);
		
		/* 2. 이전일과 비교하여 key-count-diff.log */
		ProcessHandler rankDiff = new KeywordRankDiffHandler(topCount, encoding).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new RealtimePopularKeywordResultHandler(workingDir, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		new UpdatePopularKeywordHandler().appendTo(popularKeywordResultHandler);
		
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>();
		categoryProcess.setLogHandler(new SearchLogKeyCountHandler(categoryId, workingDir, banWords, minimumHitCount));
		categoryProcess.setProcessHandler(logSort);
		
		return categoryProcess;
	}
	
}

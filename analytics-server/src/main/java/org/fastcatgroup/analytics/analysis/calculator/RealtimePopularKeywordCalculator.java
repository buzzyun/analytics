package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateRealtimePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

/**
 * 실시간 인기검색어 계산기.
 * 
 * */
public class RealtimePopularKeywordCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public RealtimePopularKeywordCalculator(String name, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, baseDir, siteId, categoryIdList);
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
		String REALTIME_POPULAR_FILENAME = "popular.log";
		
		
		//TODO 0.log, 1.log 사용하는 부분 구현필요.
		
		
		
		
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize);
		
		/* 2. 이전일과 비교하여 key-count-diff.log */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(workingDir, KEY_COUNT_RANK_PREV_FILENAME);
		File popularKeywordLogFile = new File(workingDir, REALTIME_POPULAR_FILENAME);
		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		new UpdateRealtimePopularKeywordHandler(siteId, categoryId).appendTo(popularKeywordResultHandler);
		
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>();
		categoryProcess.setLogHandler(new SearchLogKeyCountHandler(categoryId, workingDir, banWords, minimumHitCount));
		categoryProcess.setProcessHandler(logSort);
		
		return categoryProcess;
	}
	
}

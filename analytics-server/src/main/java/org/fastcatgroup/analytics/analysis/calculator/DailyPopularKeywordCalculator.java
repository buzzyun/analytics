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
import org.fastcatgroup.analytics.analysis.handler.UpdateDailyPopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateRealtimePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

public class DailyPopularKeywordCalculator extends Calculator<SearchLog> {
	
	private File prevDir;
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public DailyPopularKeywordCalculator(String name, File baseDir, File prevDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, baseDir, siteId, categoryIdList);
		this.prevDir = prevDir;
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = SearchStatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		File prevWorkingDir = new File(prevDir, categoryId);
		
		int runKeySize = SearchStatisticsProperties.runKeySize;

		String KEY_COUNT_FILENAME = "key-count.log";
		String KEY_COUNT_RANK_FILENAME = "key-count-rank.log";
		String POPULAR_FILENAME = "popular.log";
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>();
		new SearchLogKeyCountHandler(categoryId, workingDir, KEY_COUNT_FILENAME, banWords, minimumHitCount).attachLogHandlerTo(categoryProcess);
		
		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize).attachProcessTo(categoryProcess);
		
		/* 2. 이전일과 비교하여 key-count-diff.log */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(prevWorkingDir, KEY_COUNT_RANK_FILENAME);
		File popularKeywordLogFile = new File(workingDir, POPULAR_FILENAME);
		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		new UpdateDailyPopularKeywordHandler(siteId, categoryId).appendTo(popularKeywordResultHandler);
		
		return categoryProcess;
	}
	
}

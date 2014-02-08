package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.RealtimePopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis2.handler.SearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis2.handler.UpdatePopularKeywordHandler;

public class DailyPopularKeywordCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public DailyPopularKeywordCalculator(String name, File baseDir, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
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

		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, encoding, runKeySize);
		
		/* 2. 이전일과 비교하여 key-count-diff.log */
		ProcessHandler rankDiff = new KeywordRankDiffHandler(topCount, encoding).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new RealtimePopularKeywordResultHandler(workingDir, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		new UpdatePopularKeywordHandler().appendTo(popularKeywordResultHandler);
		
		CategoryProcess<SearchLog> calculatorProcess = new CategoryProcess<SearchLog>();
		calculatorProcess.setLogHandler(new SearchLogKeyCountHandler(categoryId, workingDir, banWords, minimumHitCount));
		calculatorProcess.setProcessHandler(logSort);
		
		return calculatorProcess;
	}
	
}

package org.fastcatsearch.analytics.analysis.calculator;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_RANK_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_RANK_PREV_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.REALTIME_POPULAR_FILENAME;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatsearch.analytics.analysis.SearchLogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.handler.CheckFileEmptyHandler;
import org.fastcatsearch.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatsearch.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatsearch.analytics.analysis.handler.MergeRealtimeKeyCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.MoveFileHandler;
import org.fastcatsearch.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.RealtimeSearchLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateRealtimePopularKeywordHandler;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.SearchLog;

/**
 * 실시간 인기검색어 계산기.
 * 
 * */
public class RealtimePopularKeywordCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	private int maxKeywordLength;
	private int realtimeSearchLogLimit;
	 
	public RealtimePopularKeywordCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount
			, int maxKeywordLength, int realtimeSearchLogLimit) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
		this.maxKeywordLength = maxKeywordLength;
		this.realtimeSearchLogLimit = realtimeSearchLogLimit;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		int runKeySize = StatisticsProperties.runKeySize;

		File storeDir = new File(workingDir, "store");
		String tmpLogFilename = "0.log";
//		int maxKeywordLength = StatisticsUtils.maxKeywordLength;
//		int realtimeSearchLogLimit = StatisticsUtils.realtimeSearchLogLimit;
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		/* 1. store디렉토리에 기존 #.log를 shift하고 저장한다. */
		new RealtimeSearchLogKeyCountHandler(categoryId, storeDir, tmpLogFilename, minimumHitCount, realtimeSearchLogLimit, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		/* 2. store/#.log파일들을 모아서 하나의 key-count.log로 저장한다. */
		ProcessHandler mergeKeyCount = new MergeRealtimeKeyCountProcessHandler(storeDir, workingDir, KEY_COUNT_FILENAME, encoding, realtimeSearchLogLimit, entryParser).attachProcessTo(categoryProcess);
		
		// key-count가 비어있으면 중지.
		ProcessHandler checkKeyCountFile = new CheckFileEmptyHandler(new File(workingDir, KEY_COUNT_FILENAME)).appendTo(mergeKeyCount);
		
		/* 3. 기존 key-count-rank.log 를 key-count-rank-prev 로 이동. */
		ProcessHandler backupKeyCountRank = new MoveFileHandler(workingDir, KEY_COUNT_RANK_FILENAME, KEY_COUNT_RANK_PREV_FILENAME).appendTo(checkKeyCountFile);
		
		/* 4. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(backupKeyCountRank);
		
		/* 5. 이전일과 비교하여 diff생성. */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(workingDir, KEY_COUNT_RANK_PREV_FILENAME);
		File popularKeywordLogFile = new File(workingDir, REALTIME_POPULAR_FILENAME);
		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding, entryParser).appendTo(logSort);
		
		/* 6. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		/* 7. 인기검색어 객체 업데이트 */
		new UpdateRealtimePopularKeywordHandler(siteId, categoryId).appendTo(popularKeywordResultHandler);
		
		return categoryProcess;
	}
	
}

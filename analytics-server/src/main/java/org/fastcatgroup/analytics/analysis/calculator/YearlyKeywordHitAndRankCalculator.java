package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.SearchLogValidator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateKeywordHitHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchHitHandler;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class YearlyKeywordHitAndRankCalculator extends Calculator<SearchLog> {
	
	private Calendar prevCalendar;
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public YearlyKeywordHitAndRankCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		
		logger.debug("category:{} / process:{}", categoryId, this);
		
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getMonthDiff(prevCalendar, calendar);
		
		File workingDir = new File(new File(SearchStatisticsProperties.getYearDataDir(baseDir, calendar), siteId), categoryId);
		File prevWorkingDir = new File(new File(SearchStatisticsProperties.getYearDataDir(baseDir, prevCalendar), siteId), categoryId);
		
		if(!workingDir.exists()) {
			try {
				FileUtils.forceMkdir(workingDir);
			} catch (IOException ignore) { }
		}
		
		if(!prevWorkingDir.exists()) {
			try {
				FileUtils.forceMkdir(prevWorkingDir);
			} catch (IOException ignore) { }
		}
		
		String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.YEAR);
		int maxKeywordLength = SearchStatisticsProperties.maxKeywordLength;
		int runKeySize = SearchStatisticsProperties.runKeySize;
		
		//logger.debug("daily calendar : {}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		File[] files = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			files[inx] = new File(new File(new File(
				SearchStatisticsProperties.getMonthDataDir(baseDir,
				dailyCalendar), siteId), categoryId),
				KEY_COUNT_FILENAME);
			dailyCalendar.add(Calendar.MONTH, -1);
		}
		
		String dateFrom = SearchStatisticsProperties.getTimeId(dailyCalendar, Calendar.DAY_OF_MONTH);
		String dateTo = SearchStatisticsProperties.getTimeId(calendar, Calendar.DAY_OF_MONTH);
		
		//12달치 파일을 돌면서 key-count / key-count-rank / popular 를 머징한다.
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		
		KeyCountLogAggregator<SearchLog> aggregator = new KeyCountLogAggregator<SearchLog>(baseDir, KEY_COUNT_FILENAME, runKeySize, encoding, minimumHitCount, entryParser);
		new SearchLogKeyCountHandler(categoryId, aggregator, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(files, workingDir, KEY_COUNT_FILENAME, encoding, entryParser).attachProcessTo(categoryProcess);
		
		ProcessHandler hitCounter = new KeyCountProcessHandler(siteId,
				categoryId, workingDir, KEY_COUNT_FILENAME, dateFrom, dateTo,
				encoding).appendTo(mergeKeyCount);
		
		/* 0. 갯수를 db로 저장한다. */
		ProcessHandler updateSearchHitHandler = new UpdateSearchHitHandler(siteId, categoryId, timeId).appendTo(hitCounter);
		
		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(updateSearchHitHandler);
		/* 2. 이전일과 비교하여 diff 생성. */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(prevWorkingDir, KEY_COUNT_RANK_FILENAME);
		File popularKeywordLogFile = new File(workingDir, POPULAR_FILENAME);
		
		//카테고리가 _root이면 10000개, 나머지는 100개씩.
		if(categoryId.equals("_root")){
			topCount = 10000;
		}else{
			topCount = 100;
		}
		
		//키워드별 count 를 바로 저장한다.
		new UpdateKeywordHitHandler(siteId, categoryId, timeId, rankLogFile, topCount, encoding, entryParser).appendTo(logSort);
		
		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding, entryParser).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		new UpdatePopularKeywordHandler(siteId, categoryId, timeId).appendTo(popularKeywordResultHandler);
		
		return categoryProcess;
	}
}

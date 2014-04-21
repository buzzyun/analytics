package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchLogValidator;
import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchHourLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateHourlySearchHitHandler;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class HourlyKeywordHitAndRankCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	private int minimumHitCount;
	private int topCount;
	
	public HourlyKeywordHitAndRankCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords, int minimumHitCount, int topCount) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsUtils.encoding;
		File workingDir = new File(baseDir, categoryId);
		
		int maxKeywordLength = StatisticsUtils.maxKeywordLength;
		int runKeySize = StatisticsUtils.runKeySize;
		
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		new SearchHourLogKeyCountHandler(categoryId, workingDir, KEY_COUNT_FILENAME, minimumHitCount, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler hourlySearchLogKeyCountHandler = new UpdateHourlySearchHitHandler(siteId, categoryId, calendar).attachProcessTo(categoryProcess);
		
		return categoryProcess;
	}
	
}

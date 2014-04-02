package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.SearchLogValidator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateKeywordHitHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchHitHandler;
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
		String encoding = SearchStatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		//각 raw-log를 돌면서 시간대별로 기록한다.
		return categoryProcess;
	}
	
}

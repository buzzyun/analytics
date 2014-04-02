package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.DailyRawLogger;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyKeywordHitAndRankCalculator;
import org.fastcatgroup.analytics.analysis.calculator.HourlyKeywordHitAndRankCalculator;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.log.SearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 시간대별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 이미 raw-log 를 가지고 있으므로 데이터베이스에만 뿌려준다.
 * */
public class HourlySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	DailyRawLogger dailyRawLogger;
	
	public HourlySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger dailyRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.dailyRawLogger = dailyRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		Set<String> banWords = null;
		int minimumHitCount = 1;
		int topCount = 10;

		//당일치 로그만 이용한다.
		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		String encoding = SearchStatisticsProperties.encoding;
		try {
			logReader = new SearchLogReader(new File[] { logFile }, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new HourlyKeywordHitAndRankCalculator("Hourly popular keyword calculator", calendar, baseDir, siteId, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(popularKeywordCalculator);
		
	}
	
	@Override
	protected void preProcess() {
		if (dailyRawLogger != null) {
			dailyRawLogger.rolling();
		}
	}

}

package org.fastcatsearch.analytics.analysis.task;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RAW_LOG_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatsearch.analytics.analysis.DailyRawLogger;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.HourlyKeywordHitCalculator;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.log.SearchLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.service.ServiceManager;

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
		File dir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		File baseDir = StatisticsUtils.getDayDataDir(dir, calendar);
		
		//당일치 로그만 이용한다.
		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		try {
			logReader = new SearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
			//logger.debug("logReader:{}", logReader);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new HourlyKeywordHitCalculator("Hourly popular keyword calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
		
	}
	
	@Override
	protected void preProcess() {
		if (dailyRawLogger != null) {
			dailyRawLogger.rolling();
		}
	}

}

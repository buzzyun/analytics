package org.fastcatsearch.analytics.analysis.task;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RAW_LOG_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.RollingRawLogger;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.RealtimePopularKeywordCalculator;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.log.SearchLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 실시간 인기검색어 통계용 task
 * 
 * */
public class RealtimeSearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	private RollingRawLogger realtimeRawLogger;

	public RealtimeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, RollingRawLogger realtimeRawLogger) {
		super("REALTIME_SP", "RealtimeSearchLogAnalyticsTask", siteId, categoryIdList, schedule, priority);
		this.realtimeRawLogger = realtimeRawLogger;
	}

	@Override
	public void prepare(Calendar calendar) {
		// baseDir : statistics/[siteId]/rt/data 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "rt", "data");
		
		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		try {
			logReader = new SearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> calculator = new RealtimePopularKeywordCalculator("Realtime popular keyword calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(calculator);
	}

	@Override
	protected void preProcess() {
		if (realtimeRawLogger != null) {
			realtimeRawLogger.rolling();
		}
	}

}

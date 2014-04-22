package org.fastcatgroup.analytics.analysis.task;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.RAW_LOG_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.RollingRawLogger;
import org.fastcatgroup.analytics.analysis.StatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.RealtimePopularKeywordCalculator;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.log.SearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.service.ServiceManager;

/**
 * 실시간 인기검색어 통계용 task
 * 
 * */
public class RealtimeSearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	private RollingRawLogger realtimeRawLogger;

	public RealtimeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, RollingRawLogger realtimeRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.realtimeRawLogger = realtimeRawLogger;
	}

	@Override
	public void prepare(Calendar calendar) {
		// baseDir : statistics/search/rt/data 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "rt", "data", siteId);
		int topCount = 10;
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		Set<String> banWords = statisticsSettings.getSiteProperties().getBanwordSet();
		int maxKeywordLength = statisticsSettings.getSiteProperties().getMaxKeywordLength();
		int realtimeSearchLogLimit = statisticsSettings.getRealtimePopularKeywordSetting().getRecentCount();
		int minimumHitCount = statisticsSettings.getRealtimePopularKeywordSetting().getMinimumHitCount();
		
		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		try {
			logReader = new SearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> calculator = new RealtimePopularKeywordCalculator("Realtime popular keyword calculator", calendar, baseDir, siteId, categoryIdList, banWords, minimumHitCount,
				topCount, maxKeywordLength, realtimeSearchLogLimit);
		addCalculator(calculator);
	}

	@Override
	protected void preProcess() {
		if (realtimeRawLogger != null) {
			realtimeRawLogger.rolling();
		}
	}

}

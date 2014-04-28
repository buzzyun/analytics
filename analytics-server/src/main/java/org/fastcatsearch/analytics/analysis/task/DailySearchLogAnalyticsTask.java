package org.fastcatsearch.analytics.analysis.task;

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
import org.fastcatsearch.analytics.analysis.calculator.DailyKeywordHitAndRankCalculator;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.log.SearchLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class DailySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	DailyRawLogger dailyRawLogger;
	
	public DailySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger dailyRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.dailyRawLogger = dailyRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) {
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		Set<String> banWords = statisticsSettings.getSiteProperties().getBanwordSet();
		SiteAttribute siteAttribute = statisticsSettings.getSiteAttribute();
		List<ServiceSetting> serviceTypeList = siteAttribute.getServiceList();
		
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
		File baseDir = new File(StatisticsUtils.getDayDataDir(dir, calendar), siteId);
		File prevDir = new File(StatisticsUtils.getDayDataDir(dir, prevCalendar), siteId);
		int minimumHitCount = 1;
		int topCount = 10;

		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		try {
			logReader = new SearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new DailyKeywordHitAndRankCalculator("Daily popular keyword calculator", calendar, baseDir, prevDir, siteId, categoryIdList, banWords, serviceTypeList, minimumHitCount, topCount);
		addCalculator(popularKeywordCalculator);
		
	}
	
	@Override
	protected void preProcess() {
		if (dailyRawLogger != null) {
			dailyRawLogger.rolling();
		}
	}

}
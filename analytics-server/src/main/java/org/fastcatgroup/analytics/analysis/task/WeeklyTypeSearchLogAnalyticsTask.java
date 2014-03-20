package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.DailyRawLogger;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.calculator.WeeklyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class WeeklyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	DailyRawLogger dailyTypeRawLogger;
	
	public WeeklyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger dailyTypeRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.dailyTypeRawLogger = dailyTypeRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/W##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		String[] typeList = environment.settingManager().getSystemSettings().getStringArray("db.typeList", ",");
		logger.debug("@@@@typeList > {} {}", "", typeList);
		
		//주의 최초로 되돌린다.
		calendar.add(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_WEEK) * -1 + 6);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -7);
		File baseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, calendar), siteId);
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getDateDiff(prevCalendar, calendar);
		//일주일치의 일자별 raw.log를 머징한다.
		File[] files = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			File dailyBaseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, dailyCalendar), siteId);
			files[inx] = new File(dailyBaseDir, "type_raw.log");
			dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		try {
			logReader = new TypeSearchLogReader(files, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> weeklyTypeHitCalculator = new WeeklyTypeHitCalculator("Daily type hit calculator", calendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(weeklyTypeHitCalculator);
	}

	@Override
	protected void preProcess() {
		if (dailyTypeRawLogger != null) {
			dailyTypeRawLogger.rolling();
		}
	}
}
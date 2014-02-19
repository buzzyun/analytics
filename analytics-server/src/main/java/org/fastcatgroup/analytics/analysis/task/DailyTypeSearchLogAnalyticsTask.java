package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.DailyRawLogger;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class DailyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	DailyRawLogger dailyTypeRawLogger;
	
	public DailyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger dailyTypeRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.dailyTypeRawLogger = dailyTypeRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		String[] typeList = environment.settingManager().getSystemSettings().getStringArray("db.typeList", ",");
		logger.debug("@@@@typeList > {} {}", "", typeList);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
		File baseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, calendar), siteId);

		File logFile = new File(baseDir, "type_raw.log");
		String encoding = SearchStatisticsProperties.encoding;
		try {
			if(logFile.exists()){
				logReader = new TypeSearchLogReader(logFile, encoding);
			}
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> dailyTypeHitCalculator = new DailyTypeHitCalculator("Daily type hit calculator", calendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(dailyTypeHitCalculator);
		
	}

	@Override
	protected void preProcess() {
		if (dailyTypeRawLogger != null) {
			dailyTypeRawLogger.rolling();
		}
	}
}

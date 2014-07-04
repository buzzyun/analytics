package org.fastcatsearch.analytics.analysis.task;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.TYPE_RAW_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.DailyRawLogger;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.DailyTypeHitCalculator;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class DailyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	public DailyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super("DAILY_TYPE", "DailyTypeSearchLogAnalyticsTask", siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File dir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
		File baseDir = StatisticsUtils.getDayDataDir(dir, calendar);

		File logFile = new File(baseDir, TYPE_RAW_FILENAME);
		try {
			if(logFile.exists()){
				logReader = new TypeSearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
			}
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> dailyTypeHitCalculator = new DailyTypeHitCalculator("Daily type hit calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(dailyTypeHitCalculator);
		
	}
}

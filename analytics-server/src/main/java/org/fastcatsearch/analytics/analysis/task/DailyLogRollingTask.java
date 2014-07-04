package org.fastcatsearch.analytics.analysis.task;

import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.DailyRawLogger;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class DailyLogRollingTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	DailyRawLogger[] dailyRawLogger;
	
	public DailyLogRollingTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger[] dailyRawLogger) {
		super("DAILY_SP", "DailyLogRollingTask", siteId, categoryIdList, schedule, priority);
		this.dailyRawLogger = dailyRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) { }
	
	@Override
	protected void preProcess() {
		if (dailyRawLogger != null) {
			for (DailyRawLogger logger : dailyRawLogger) {
				if (logger != null) {
					logger.rolling();
				}
			}
		}
	}
}

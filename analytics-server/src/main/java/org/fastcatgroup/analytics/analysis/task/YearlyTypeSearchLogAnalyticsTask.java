package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.MonthlyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class YearlyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;
	
	public YearlyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		String[] typeList = environment.settingManager().getSystemSettings().getStringArray("db.typeList", ",");
		logger.debug("@@@@typeList > {} {}", "", typeList);
		
		//해당년도의 마지막 일자로 되돌린다.
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.YEAR, 1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.YEAR, -1);
		File baseDir = new File(SearchStatisticsProperties.getYearDataDir(dir, calendar), siteId);
		
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getMonthDiff(prevCalendar, calendar);
		
		//1년치의 월별 로그를 데이터베이스를 사용하여 머징한다.
		File[] files = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			File dailyBaseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, dailyCalendar), siteId);
			files[inx] = new File(dailyBaseDir, "type_raw.log");
			dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		logger.debug("calculating{} {}", "",files);
		
		try {
			logReader = new TypeSearchLogReader(files, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> monthlyTypeHitCalculator = new MonthlyTypeHitCalculator("Montly type hit calculator", calendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(monthlyTypeHitCalculator);
	}
}

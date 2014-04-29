package org.fastcatsearch.analytics.analysis;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.CLICK_RAW_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.POPULAR_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RAW_LOG_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.TYPE_RAW_FILENAME;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.schedule.EveryDaySchedule;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatsearch.analytics.analysis.task.DailyClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.DailySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.DailyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlyClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.NDaysClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.RealtimeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.RelateSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.YearlySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.YearlyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.control.JobService;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.module.AbstractModule;
import org.fastcatsearch.analytics.module.ModuleException;

public class SiteSearchLogStatisticsModule extends AbstractModule {
	File fileHome;
	String siteId;
	ScheduledTaskRunner realtimeTaskRunner;
	ScheduledTaskRunner dailyTaskRunner;
	ScheduledTaskRunner relateTaskRunner;
	RollingRawLogger realtimeRawLogger;
	DailyRawLogger dailyRawLogger;
	DailyRawLogger dailyTypeRawLogger;
	DailyRawLogger dailyClickRawLogger;
	StatisticsService statisticsService;
	List<String> categoryIdList;
	
	public SiteSearchLogStatisticsModule(StatisticsService statisticsService, File fileHome, String siteId, List<String> categoryIdList, Environment environment, Settings settings) {
		super(environment, settings);
		this.statisticsService = statisticsService;
		this.fileHome = fileHome;
		this.siteId = siteId;
		this.categoryIdList = categoryIdList;
	}

	@Override
	protected boolean doLoad() throws ModuleException {
		File dateKeywordBaseDir = new File(fileHome, "date");
		File realtimeKeywordBaseDir = new File(new File(fileHome, "rt"), "data");

		/*
		 * 실시간 인기검색어 서비스 로딩
		 */
		File siteDir = new File(realtimeKeywordBaseDir, siteId);
		logger.debug("Search stat resultDir > {}", siteDir.getAbsolutePath());
		if (siteDir.exists()) {
			File[] categoryDirList = listCategoryDir(siteDir);
			for (File categoryDir : categoryDirList) {
				String categoryId = categoryDir.getName();
				logger.debug("Search stat categoryDir {}", categoryDir.getAbsolutePath() );
				File f = new File(categoryDir, POPULAR_FILENAME);
				logger.debug("Search stat popular keyword > {}, {}", f.exists(), f.getAbsolutePath());
				if (f.exists()) {
					// load keyword file to dictionary.
					List<RankKeyword> keywordList = loadKeywordListFile(f);
					logger.debug("Load rt-keyword > {} > {}", categoryId, keywordList);
					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
				}
			}
		}

		String logFileName = RAW_LOG_FILENAME;
		String typeLogFileName = TYPE_RAW_FILENAME;
		String clickLogFileName = CLICK_RAW_FILENAME;
		
		realtimeRawLogger = new RollingRawLogger(realtimeKeywordBaseDir, siteId, logFileName);
		dailyRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, siteId, logFileName);
		dailyTypeRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, siteId, typeLogFileName);
		dailyClickRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, siteId, clickLogFileName);
		
		Calendar cal = StatisticsUtils.getCalendar();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int delayInSeconds = 5;
		
		/*
		 * 실시간 인기검색어.
		 */
		realtimeTaskRunner = new ScheduledTaskRunner("rt-search-log-task-runner", JobService.getInstance(), environment);
		int periodInSeconds = 300;
		//Schedule realtimeSchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		Schedule realtimeSchedule =  new EveryDaySchedule(0, delayInSeconds);
		RealtimeSearchLogAnalyticsTask realtimeTask = new RealtimeSearchLogAnalyticsTask(siteId, categoryIdList, realtimeSchedule, 0, realtimeRawLogger);
		realtimeTaskRunner.addTask(realtimeTask);
		realtimeTaskRunner.start();


		/*
		 * 연관검색어 
		 * */
		relateTaskRunner = new ScheduledTaskRunner("daily-search-log-task-runner", JobService.getInstance(), environment);
		Schedule relateSchedule = new EveryDaySchedule(0, delayInSeconds); //매일 0시.
//		Schedule relateSchedule = new FixedSchedule(cal, 10, delayInSeconds);
		RelateSearchLogAnalyticsTask relateSearchLogAnalysisTask = new RelateSearchLogAnalyticsTask(siteId, categoryIdList, relateSchedule, 1);
		relateTaskRunner.addTask(relateSearchLogAnalysisTask);
		relateTaskRunner.start();
		
		/*
		 * 일별 통계 인기검색어. 일,주,월,년
		 */
		dailyTaskRunner = new ScheduledTaskRunner("daily-search-log-task-runner", JobService.getInstance(), environment);
		// 일
		Schedule dailySchedule1 = new EveryDaySchedule(0, delayInSeconds); //매일 0시.
		//Schedule dailySchedule1 = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		DailySearchLogAnalyticsTask dailySearchLogAnalysisTask = new DailySearchLogAnalyticsTask(siteId, categoryIdList, dailySchedule1, 1, dailyRawLogger);
		dailyTaskRunner.addTask(dailySearchLogAnalysisTask);
		
		Schedule dailySchedule2 = new EveryDaySchedule(0, delayInSeconds);
		//Schedule dailySchedule2 = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		DailyTypeSearchLogAnalyticsTask dailyTypeSearchLogAnalyticsTask = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, dailySchedule2, 2, dailyTypeRawLogger);
		dailyTaskRunner.addTask(dailyTypeSearchLogAnalyticsTask);

		/*
		 * 주별 통계 
		 */
		Schedule weeklySchedule = new EveryDaySchedule(0, delayInSeconds);
		//Schedule weeklySchedule = new FixedSchedule(cal, 10, 5);
		WeeklySearchLogAnalyticsTask weeklySearchLogAnalysisTask = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, weeklySchedule, 3);
		dailyTaskRunner.addTask(weeklySearchLogAnalysisTask);
		
		Schedule weeklySchedule2 = new EveryDaySchedule(0, delayInSeconds);
		//Schedule weeklySchedule2 = new FixedSchedule(cal, 10, 5);
		WeeklyTypeSearchLogAnalyticsTask weeklyTypeSearchLogAnalyticsTask = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, weeklySchedule2, 4);
		dailyTaskRunner.addTask(weeklyTypeSearchLogAnalyticsTask);
		
		 
		/*
		 * 월별 통계 
		 */
		Schedule monthlySchedule = new EveryDaySchedule(0, delayInSeconds);
		//Schedule monthlySchedule = new FixedSchedule(cal, 10, 5);
		MonthlySearchLogAnalyticsTask monthlySearchLogAnalysisTask = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, monthlySchedule, 5);
		dailyTaskRunner.addTask(monthlySearchLogAnalysisTask);
		
		Schedule monthlySchedule2 = new EveryDaySchedule(0, delayInSeconds);
		//Schedule monthlySchedule2 = new FixedSchedule(cal, 10, 5);
		MonthlyTypeSearchLogAnalyticsTask monthlyTypeSearchLogAnalyticsTask = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, monthlySchedule2, 6);
		dailyTaskRunner.addTask(monthlyTypeSearchLogAnalyticsTask);
		
		/*
		 * 년도별 통계 
		 */
		Schedule yearlySchedule = new EveryDaySchedule(0, delayInSeconds);
		YearlySearchLogAnalyticsTask yearlySearchLogAnalysisTask = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, yearlySchedule, 7);
		dailyTaskRunner.addTask(yearlySearchLogAnalysisTask);
				
		Schedule yearlySchedule2 = new EveryDaySchedule(0, delayInSeconds);
		YearlyTypeSearchLogAnalyticsTask yearlyTypeSearchLogAnalyticsTask = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, yearlySchedule2, 8);
		dailyTaskRunner.addTask(yearlyTypeSearchLogAnalyticsTask);
		
		/**
		 * 클릭로그 통계
		 */
		/* 1. Daily */
		Schedule clickLogSchedule = new EveryDaySchedule(0, delayInSeconds);
		DailyClickLogAnalyticsTask dailyClickLogAnalyticsTask = new DailyClickLogAnalyticsTask(siteId, categoryIdList, clickLogSchedule, 9);
		dailyTaskRunner.addTask(dailyClickLogAnalyticsTask);
		
		/* 2. Monthly */
		Schedule clickLogMonthlySchedule = new EveryDaySchedule(0, delayInSeconds);
		MonthlyClickLogAnalyticsTask monthlyClickLogAnalyticsTask = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, clickLogMonthlySchedule, 10);
		dailyTaskRunner.addTask(monthlyClickLogAnalyticsTask);
		
		/* 3. N-Days */
		Schedule clickLogNDaysSchedule = new EveryDaySchedule(0, delayInSeconds);
		NDaysClickLogAnalyticsTask nDaysClickLogAnalyticsTask = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, clickLogNDaysSchedule, 11);
		dailyTaskRunner.addTask(nDaysClickLogAnalyticsTask);

		dailyTaskRunner.start();
		return true;
	}

	@Override
	protected boolean doUnload() throws ModuleException {
		if(realtimeRawLogger != null) {
			realtimeRawLogger.close();
		}
		if(dailyRawLogger != null) {
			dailyRawLogger.close();
		}
		if(dailyTypeRawLogger != null) {
			dailyTypeRawLogger.close();
		}
		if(dailyClickRawLogger != null) {
			dailyClickRawLogger.close();
		}
		
		realtimeTaskRunner.cancel();
		dailyTaskRunner.cancel();
		relateTaskRunner.cancel();
		try {
			realtimeTaskRunner.join();
		} catch (InterruptedException e) {
		}
		try {
			dailyTaskRunner.join();
		} catch (InterruptedException e) {
		}
		try {
			relateTaskRunner.join();
		} catch (InterruptedException e) {
		}
		return true;
	}

	public void addLog(String... data) {
		realtimeRawLogger.log(data);
		dailyRawLogger.log(data);
	}
	public void addTypeLog(String... data) {
		dailyTypeRawLogger.log(data);
	}
	public void addClickLog(String... data) {
		dailyClickRawLogger.log(data);
	}

	private File[] listCategoryDir(File dir) {
		return dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
	}

	private List<RankKeyword> loadKeywordListFile(File f) {
		BufferedReader reader = null;
		String line = null;
		List<RankKeyword> list = new ArrayList<RankKeyword>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			int rank = 1;
			while ((line = reader.readLine()) != null) {
				String[] el = line.split("\t");
				int count = Integer.parseInt(el[3]);
				int countDiff = Integer.parseInt(el[4]);
				RankKeyword k = new RankKeyword(el[0], rank, count);
				k.setRankDiffType(RankDiffType.valueOf(el[1]));
				k.setRankDiff(Integer.parseInt(el[2]));
				k.setCountDiff(countDiff);
				list.add(k);
				rank++;
			}
		} catch (IOException e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return list;
	}
}

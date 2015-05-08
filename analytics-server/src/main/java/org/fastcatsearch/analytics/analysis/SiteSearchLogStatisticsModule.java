package org.fastcatsearch.analytics.analysis;

import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.schedule.EveryDaySchedule;
import org.fastcatsearch.analytics.analysis.schedule.FixedSchedule;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatsearch.analytics.analysis.task.*;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.control.JobService;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.module.AbstractModule;
import org.fastcatsearch.analytics.module.ModuleException;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class SiteSearchLogStatisticsModule extends AbstractModule {
	private File siteFileHome;
	private String siteId;
	private ScheduledTaskRunner realtimeTaskRunner;
	private ScheduledTaskRunner dailyTaskRunner;
	private ScheduledTaskRunner relateTaskRunner;
	private RollingRawLogger realtimeRawLogger;
	private DailyRawLogger dailyRawLogger;
	private DailyRawLogger dailyTypeRawLogger;
	private DailyRawLogger dailyClickRawLogger;
	private StatisticsService statisticsService;
	private List<String> categoryIdList;
	
	public SiteSearchLogStatisticsModule(StatisticsService statisticsService, File siteFileHome, String siteId, List<String> categoryIdList, Environment environment, Settings settings) {
		super(environment, settings);
		this.statisticsService = statisticsService;
		this.siteFileHome = siteFileHome;
		this.siteId = siteId;
		this.categoryIdList = categoryIdList;
	}

	@Override
	protected boolean doLoad() throws ModuleException {
		File dateKeywordBaseDir = new File(siteFileHome, "date");
		File realtimeKeywordBaseDir = new File(new File(siteFileHome, "rt"), "data");

		StatisticsSettings statisticsSettings = statisticsService.getStatisticsSetting(siteId);
		/*
		 * 실시간 인기검색어 서비스 로딩
		 */
		logger.debug("Search stat resultDir > {}", realtimeKeywordBaseDir.getAbsolutePath());
		if (realtimeKeywordBaseDir.exists()) {
			File[] categoryDirList = listCategoryDir(realtimeKeywordBaseDir);
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
		
		realtimeRawLogger = new RollingRawLogger(realtimeKeywordBaseDir, logFileName);
		dailyRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, logFileName);
		dailyTypeRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, typeLogFileName);
		dailyClickRawLogger = new DailyRawLogger(StatisticsUtils.getCalendar(), dateKeywordBaseDir, clickLogFileName);
		
		Calendar cal = StatisticsUtils.getCalendar();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int delayInSeconds = statisticsSettings.getSiteProperties().getScheduleDelayInSeconds();
		int dailyScheduleTime = statisticsSettings.getSiteProperties().getDailyScheduleTime();
		
		int priority = 0;
		
		/*
		 * 실시간 인기검색어.
		 */
		realtimeTaskRunner = new ScheduledTaskRunner("rt-search-log-task-runner", JobService.getInstance(), environment);
		int periodInSeconds = statisticsSettings.getRealtimePopularKeywordSetting().getPeriodInSeconds();
		Schedule realtimeSchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		RealtimeSearchLogAnalyticsTask realtimeTask = new RealtimeSearchLogAnalyticsTask(siteId, categoryIdList, realtimeSchedule, priority++, realtimeRawLogger);
		realtimeTaskRunner.addTask(realtimeTask);
		realtimeTaskRunner.start();

		/*
		 * 연관검색어 
		 * */
		relateTaskRunner = new ScheduledTaskRunner("daily-search-log-task-runner", JobService.getInstance(), environment);
		Schedule relateSchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds); //매일 0시.
		RelateSearchLogAnalyticsTask relateSearchLogAnalysisTask = new RelateSearchLogAnalyticsTask(siteId, categoryIdList, relateSchedule, priority++);
		relateTaskRunner.addTask(relateSearchLogAnalysisTask);
		relateTaskRunner.start();
		
		
		/*
		 * 일별 통계 인기검색어. 일,주,월,년
		 */
		dailyTaskRunner = new ScheduledTaskRunner("daily-search-log-task-runner", JobService.getInstance(), environment);
		
		//로그롤링
		Schedule dailySchedule0 = new EveryDaySchedule(0, 0);
		DailyLogRollingTask dailyLogRollingTask = new DailyLogRollingTask(siteId, categoryIdList, dailySchedule0, priority++, new DailyRawLogger[] 
				{ dailyRawLogger, dailyTypeRawLogger, dailyClickRawLogger });
		dailyTaskRunner.addTask(dailyLogRollingTask);
		
		//시간대별
		Schedule dailySchedule1 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		HourlySearchLogAnalyticsTask hourlySearchLogAnalyticsTask = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, dailySchedule1, priority++);
		dailyTaskRunner.addTask(hourlySearchLogAnalyticsTask);
		
		// 일
		Schedule dailySchedule2 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds); //매일 0시.
		DailySearchLogAnalyticsTask dailySearchLogAnalysisTask = new DailySearchLogAnalyticsTask(siteId, categoryIdList, dailySchedule2, priority++);
		dailyTaskRunner.addTask(dailySearchLogAnalysisTask);
		
		Schedule dailySchedule3 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		DailyTypeSearchLogAnalyticsTask dailyTypeSearchLogAnalyticsTask = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, dailySchedule3, priority++);
		dailyTaskRunner.addTask(dailyTypeSearchLogAnalyticsTask);
		

		/*
		 * 주별 통계 
		 */
		Schedule weeklySchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		WeeklySearchLogAnalyticsTask weeklySearchLogAnalysisTask = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, weeklySchedule, priority++);
		dailyTaskRunner.addTask(weeklySearchLogAnalysisTask);
		
		Schedule weeklySchedule2 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		WeeklyTypeSearchLogAnalyticsTask weeklyTypeSearchLogAnalyticsTask = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, weeklySchedule2, priority++);
		dailyTaskRunner.addTask(weeklyTypeSearchLogAnalyticsTask);
		
		 
		/*
		 * 월별 통계 
		 */
		Schedule monthlySchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		MonthlySearchLogAnalyticsTask monthlySearchLogAnalysisTask = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, monthlySchedule, priority++);
		dailyTaskRunner.addTask(monthlySearchLogAnalysisTask);
		
		Schedule monthlySchedule2 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		MonthlyTypeSearchLogAnalyticsTask monthlyTypeSearchLogAnalyticsTask = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, monthlySchedule2, priority++);
		dailyTaskRunner.addTask(monthlyTypeSearchLogAnalyticsTask);
		
		/*
		 * 년도별 통계 
		 */
		Schedule yearlySchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		YearlySearchLogAnalyticsTask yearlySearchLogAnalysisTask = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, yearlySchedule, priority++);
		dailyTaskRunner.addTask(yearlySearchLogAnalysisTask);
				
		Schedule yearlySchedule2 = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		YearlyTypeSearchLogAnalyticsTask yearlyTypeSearchLogAnalyticsTask = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, yearlySchedule2, priority++);
		dailyTaskRunner.addTask(yearlyTypeSearchLogAnalyticsTask);
		
		/**
		 * 클릭로그 통계
		 */
		/* 1. Daily */
		Schedule clickLogSchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		DailyClickLogAnalyticsTask dailyClickLogAnalyticsTask = new DailyClickLogAnalyticsTask(siteId, categoryIdList, clickLogSchedule, priority++);
		dailyTaskRunner.addTask(dailyClickLogAnalyticsTask);
		
		/* 2. Monthly */
		Schedule clickLogMonthlySchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		MonthlyClickLogAnalyticsTask monthlyClickLogAnalyticsTask = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, clickLogMonthlySchedule, priority++);
		dailyTaskRunner.addTask(monthlyClickLogAnalyticsTask);
		
		/* 3. N-Days */
		Schedule clickLogNDaysSchedule = new EveryDaySchedule(dailyScheduleTime, delayInSeconds);
		NDaysClickLogAnalyticsTask nDaysClickLogAnalyticsTask = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, clickLogNDaysSchedule, priority++);
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

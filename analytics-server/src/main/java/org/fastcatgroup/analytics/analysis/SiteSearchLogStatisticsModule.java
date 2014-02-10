package org.fastcatgroup.analytics.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.EveryDaySchedule;
import org.fastcatgroup.analytics.analysis.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis.task.DailySearchLogAnalysisTask;
import org.fastcatgroup.analytics.analysis.task.RealtimeSearchLogAnalysisTask;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.db.vo.PopularKeywordVO.RankDiffType;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.module.AbstractModule;
import org.fastcatgroup.analytics.module.ModuleException;

public class SiteSearchLogStatisticsModule extends AbstractModule {
	File fileHome;
	String siteId;
	ScheduledTaskRunner<SearchLog> realtimeTaskRunner;
	ScheduledTaskRunner<SearchLog> dailyTaskRunner;
	RollingRawLogger realtimeRawLogger;
	DailyRawLogger dailyRawLogger;
	StatisticsService statisticsService;

	public SiteSearchLogStatisticsModule(StatisticsService statisticsService, File fileHome, String siteId, Environment environment, Settings settings) {
		super(environment, settings);
		this.statisticsService = statisticsService;
		this.fileHome = fileHome;
		this.siteId = siteId;
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
				File f = new File(categoryDir, "popular.log");
				logger.debug("Search stat popular keyword > {}, {}", f.exists(), f.getAbsolutePath());
				if (f.exists()) {
					// load keyword file to dictionary.
					List<RankKeyword> keywordList = loadKeywordListFile(f);
					logger.debug("Load rt-keyword > {} > {}", categoryId, keywordList);
					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
				}
			}
		}

		String logFileName = "raw.log";
		realtimeRawLogger = new RollingRawLogger(realtimeKeywordBaseDir, siteId, logFileName);
		dailyRawLogger = new DailyRawLogger(Calendar.getInstance(), dateKeywordBaseDir, siteId, logFileName);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int delayInSeconds = 5;
		
		List<String> categoryIdList = new ArrayList<String>();
		categoryIdList.add("_root");
		categoryIdList.add("cat1");
		categoryIdList.add("cat2");
		/*
		 * 실시간 인기검색어.
		 */
		realtimeTaskRunner = new ScheduledTaskRunner<SearchLog>("rt-search-log-task-runner", JobService.getInstance(), environment);
		int periodInSeconds = 300;
		Schedule realtimeSchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		RealtimeSearchLogAnalysisTask realtimeTask = new RealtimeSearchLogAnalysisTask(siteId, categoryIdList, realtimeSchedule, 0, realtimeRawLogger);
		realtimeTaskRunner.addTask(realtimeTask);
		realtimeTaskRunner.start();

		/*
		 * 일별 통계 인기검색어. 일,주,월,년
		 */
		dailyTaskRunner = new ScheduledTaskRunner<SearchLog>("daily-search-log-task-runner", JobService.getInstance(), environment);

		// 일
		Schedule dailySchedule = new EveryDaySchedule(0, delayInSeconds); //매일 0시.
		
		DailySearchLogAnalysisTask dailySearchLogAnalysisTask = new DailySearchLogAnalysisTask(siteId, categoryIdList, dailySchedule, 1);
		dailyTaskRunner.addTask(dailySearchLogAnalysisTask);
		
		
		// 주
		// Schedule weeklySchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		// AnalysisTask<SearchLog> weeklyTask = makeAnalysisTask(weeklySchedule, realtimeKeywordBaseDir, logFileName, encoding);
		// dailyTaskRunner.addTask(weeklyTask);
		// 월
		// Schedule monthlySchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		// AnalysisTask<SearchLog> monthlyTask = makeAnalysisTask(monthlySchedule, realtimeKeywordBaseDir, logFileName, encoding);
		// dailyTaskRunner.addTask(monthlyTask);
		// 년
		// Schedule yearlySchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		// AnalysisTask<SearchLog> yearlyTask = makeAnalysisTask(yearlySchedule, realtimeKeywordBaseDir, logFileName, encoding);
		// dailyTaskRunner.addTask(yearlyTask);

		dailyTaskRunner.start();
		
		return true;
	}

	@Override
	protected boolean doUnload() throws ModuleException {
		realtimeTaskRunner.cancel();
		dailyTaskRunner.cancel();
		try {
			realtimeTaskRunner.join();
		} catch (InterruptedException e) {
		}
		try {
			dailyTaskRunner.join();
		} catch (InterruptedException e) {
		}
		return true;
	}

	public void log(String... data) {
		realtimeRawLogger.log(data);
		dailyRawLogger.log(data);
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

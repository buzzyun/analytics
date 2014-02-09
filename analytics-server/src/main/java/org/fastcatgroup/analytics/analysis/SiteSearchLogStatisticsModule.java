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
		File resultDir = new File(realtimeKeywordBaseDir, "result");
		if (resultDir.exists()) {
			File[] categoryDirList = listCategoryDir(resultDir);
			for (File categoryDir : categoryDirList) {
				String categoryId = categoryDir.getName();

				File f = new File(categoryDir, "rt-popular.txt");
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

		int periodInSeconds = 10;
		int delayInSeconds = 5;
		
		List<String> categoryIdList = new ArrayList<String>();
		categoryIdList.add("_root");
		categoryIdList.add("cat1");
		categoryIdList.add("cat2");
		/*
		 * 실시간 인기검색어.
		 */
		realtimeTaskRunner = new ScheduledTaskRunner<SearchLog>("rt-search-log-task-runner", JobService.getInstance(), environment);
		Schedule realtimeSchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		RealtimeSearchLogAnalysisTask realtimeTask = new RealtimeSearchLogAnalysisTask(siteId, categoryIdList, realtimeSchedule, 0, realtimeRawLogger);
		realtimeTaskRunner.addTask(realtimeTask);
		realtimeTaskRunner.start();

		/*
		 * 일별 통계 인기검색어. 일,주,월,년
		 */
		dailyTaskRunner = new ScheduledTaskRunner<SearchLog>("daily-search-log-task-runner", JobService.getInstance(), environment);

		// 일
		Schedule dailySchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		
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

//	private AnalysisTask<SearchLog> makeRealtimePopularKeywordAnalysisTask(Schedule schedule, File logDir, String fileName, String encoding) {
//		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(new File(logDir, fileName), encoding);
//
//		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
//
//		File workingDir = new File(logDir, "working");
//		File resultDir = new File(logDir, "result");
//		if (!workingDir.exists()) {
//			workingDir.mkdirs();
//		}
//		if (!resultDir.exists()) {
//			resultDir.mkdirs();
//		}
//
//		int topCount = 10;
//		int fileLimitCount = 6;
//		int runKeySize = 10 * 10000;
//		String tempFileName = "tmp.log"; // 각 카테고리하위에 tempFileName이름으로 키워드로그가 만들어진다.
//		String outFileName = "0.log";
//		Calculator<SearchLog> calculator = new Calculator<SearchLog>("Realtime popular keyword calculator", new CategorySearchLogHandler(workingDir, tempFileName));
//
//		/* 1. 카테고리별로 키워드-갯수를 계산하여 0.log에 쓴다. */
//		Set<String> banWords = null;
//		int minimumHitCount = 1;
//
//		calculator.appendProcess(new RollingLogProcessHandler(workingDir, fileLimitCount));
//		calculator.appendProcess(new KeyCountProcessHandler(workingDir, tempFileName, outFileName, runKeySize, banWords, minimumHitCount, encoding));
//
//		/* 2. 0.log, 1.log ..를 취합하여 key-count.log 로 통합한다. */
//		calculator.appendProcess(new MergeKeyCountProcessHandler(workingDir, resultDir, fileLimitCount, encoding));
//
//		/* 3. count로 정렬하여 key-count-rank.log로 저장. */
//		calculator.appendProcess(new KeyCountLogSortHandler(resultDir, encoding, runKeySize));
//
//		/* 4. 구해진 인기키워드를 저장한다. */
//		calculator.appendProcess(new KeywordRankDiffHandler(topCount, encoding));
//		calculator.appendProcess(new RealtimePopularKeywordResultHandler(resultDir, encoding));
//		calculator.postProcess(new PostProcess() {
//
//			@Override
//			public void handle(String categoryId, Object parameter) {
//				if (parameter != null) {
//					List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
//					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
//				}
//			}
//		});
//
//		task.preProcess(new Runnable() {
//
//			@Override
//			public void run() {
//				logger.debug("Rolling log file. {}", realtimeRawLogger);
//				realtimeRawLogger.rolling();
//			}
//		});
//
//		task.addCalculator(calculator);
//
//		return task;
//	}

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
				RankKeyword k = new RankKeyword(el[0], rank, count);
				k.setRankDiff(Integer.parseInt(el[1]));
				k.setRankDiffType(RankDiffType.valueOf(el[2]));
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

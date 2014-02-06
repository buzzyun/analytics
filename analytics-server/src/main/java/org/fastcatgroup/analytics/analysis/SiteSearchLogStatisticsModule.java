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
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.analysis2.AnalysisTask;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.Calculator.PostProcess;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReaderFactory;
import org.fastcatgroup.analytics.analysis2.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis2.handler.CategorySearchLogHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis2.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.RealtimePopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis2.handler.RollingLogProcessHandler;
import org.fastcatgroup.analytics.analysis2.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
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
	RollingRawLogger dailyRawLogger;
	StatisticsService statisticsService;

	public SiteSearchLogStatisticsModule(StatisticsService statisticsService, File fileHome, String siteId, Environment environment, Settings settings) {
		super(environment, settings);
		this.statisticsService = statisticsService;
		this.fileHome = fileHome;
		this.siteId = siteId;
	}

	@Override
	protected boolean doLoad() throws ModuleException {
		File realtimeKeywordBaseDir = new File(new File(fileHome, siteId), "rt");
		File dailyKeywordBaseDir = new File(new File(fileHome, siteId), "daily");

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
		realtimeRawLogger = new RollingRawLogger(realtimeKeywordBaseDir, logFileName);
		dailyRawLogger = new RollingRawLogger(dailyKeywordBaseDir, logFileName);

		String encoding = "utf-8";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int periodInSeconds = 30;
		int delayInSeconds = 5;
		/*
		 * 실시간 인기검색어.
		 */
		realtimeTaskRunner = new ScheduledTaskRunner<SearchLog>("rt-search-log-task-runner", JobService.getInstance());
		Schedule realtimeSchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		AnalysisTask<SearchLog> task = makeRealtimePopularKeywordAnalysisTask(realtimeSchedule, realtimeKeywordBaseDir, logFileName, encoding);
		realtimeTaskRunner.addTask(task);
		realtimeTaskRunner.start();

		/*
		 * 일별 통계 인기검색어. 일,주,월,년
		 */
		dailyTaskRunner = new ScheduledTaskRunner<SearchLog>("daily-search-log-task-runner", JobService.getInstance());

		// 일

		Schedule dailySchedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		AnalysisTask<SearchLog> dailyTask = makeDailyPopularKeywordAnalysisTask(dailySchedule, dailyKeywordBaseDir, logFileName, encoding);
		dailyTaskRunner.addTask(dailyTask);
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

	private AnalysisTask<SearchLog> makeRealtimePopularKeywordAnalysisTask(Schedule schedule, File logDir, String fileName, String encoding) {
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(new File(logDir, fileName), encoding);

		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);

		File workingDir = new File(logDir, "working");
		File resultDir = new File(logDir, "result");
		if (!workingDir.exists()) {
			workingDir.mkdirs();
		}
		if (!resultDir.exists()) {
			resultDir.mkdirs();
		}

		int topCount = 10;
		int fileLimitCount = 6;
		int runKeySize = 10 * 10000;
		String tempFileName = "tmp.log"; // 각 카테고리하위에 tempFileName이름으로 키워드로그가 만들어진다.
		String outFileName = "0.log";
		Calculator<SearchLog> calculator = new Calculator<SearchLog>("Realtime popular keyword calculator", new CategorySearchLogHandler(workingDir, tempFileName));

		/* 1. 카테고리별로 키워드-갯수를 계산하여 0.log에 쓴다. */
		Set<String> banWords = null;
		int minimumHitCount = 1;

		calculator.appendProcess(new RollingLogProcessHandler(workingDir, fileLimitCount));
		calculator.appendProcess(new KeyCountProcessHandler(workingDir, tempFileName, outFileName, runKeySize, banWords, minimumHitCount, encoding));

		/* 2. 0.log, 1.log ..를 취합하여 key-count.log 로 통합한다. */
		calculator.appendProcess(new MergeKeyCountProcessHandler(workingDir, resultDir, fileLimitCount, encoding));

		/* 3. count로 정렬하여 key-count-rank.log로 저장. */
		calculator.appendProcess(new KeyCountLogSortHandler(resultDir, encoding, runKeySize));

		/* 4. 구해진 인기키워드를 저장한다. */
		calculator.appendProcess(new KeywordRankDiffHandler(topCount, encoding));
		calculator.appendProcess(new RealtimePopularKeywordResultHandler(resultDir, encoding));
		calculator.postProcess(new PostProcess() {

			@Override
			public void handle(String categoryId, Object parameter) {
				if (parameter != null) {
					List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
				}
			}
		});

		task.preProcess(new Runnable() {

			@Override
			public void run() {
				logger.debug("Rolling log file. {}", realtimeRawLogger);
				realtimeRawLogger.rolling();
			}
		});

		task.addCalculator(calculator);

		return task;
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

	private AnalysisTask<SearchLog> makeDailyPopularKeywordAnalysisTask(Schedule schedule, File logDir, String fileName, String encoding) {
		
		//TODO 수정할 사항.
		
//		task에서 처음받는 로그는 FileSearchLogReaderFactory 가 결정하며, task내의 calc들은 모두 동일한 타입의 로그를 사용하게 된다.
//		그러므로, 로그를 cate별로 분류하여 저장하는 CategorySearchLogHandler도 calc내에 있으 필요는 없으며, task에서 처리하고
//		calc는 그후부터 처리하도록 한다.
		//그렇게 되면 calc를 상속받아 구현하는 방식을 사용할수 있다.
		//또, 주,월,년 간 처럼 raw로그가 필요없이 기존 일일 로그를 취합하여 사용하는 방식도 그대로 사용할수 있다. 즉, calc만 구현하여 로직으로만 사용.
		
		
		
		
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(new File(logDir, fileName), encoding);

//		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0);
		//task는 reader가 없어도 된다.
		task.setLogReader(new FileSearchLogReader(new File(logDir, fileName), encoding));
		task.setLogHandler(new CategorySearchLogHandler(new File(logDir, fileName), encoding));
		
		//TODO 
		//task1에는 일일인기검색어 계산 calc과 연관검색어 calc 및 (비율, 횟수 calc등)을 물리고. -> 로그필요.
		//task2에는 주,월,년 인기검색어 계산 calc와 (비율, 횟수 calc등)을 물린다.  -> 로그없음.

		File workingDir = new File(logDir, "working");
		if (!workingDir.exists()) {
			workingDir.mkdirs();
		}

		int topCount = 10;
		int runKeySize = 10 * 10000;
		String tempFileName = "tmp.log";
		String outFileName = "key-count.log";

		
		DailyPopularKeywordCalculator cal = new DailyPopularKeywordCalculator("Daily popular keyword calculator", logDir, fileName, encoding);
		task.add(cal);
		
		
		Calculator<SearchLog> calculator = new Calculator<SearchLog>("Daily popular keyword calculator", new CategorySearchLogHandler(workingDir, tempFileName));

		/* 1. 카테고리별로 키워드-갯수를 계산하여 key-count.log에 쓴다. */
		Set<String> banWords = null;
		int minimumHitCount = 1;
		calculator.appendProcess(new KeyCountProcessHandler(workingDir, tempFileName, outFileName, runKeySize, banWords, minimumHitCount, encoding));

		/* 2. count로 정렬하여 key-count-rank.log로 저장. */
		calculator.appendProcess(new KeyCountLogSortHandler(workingDir, encoding, runKeySize));

		/* 3. 구해진 인기키워드를 저장한다. */
		calculator.appendProcess(new KeywordRankDiffHandler(topCount, encoding));
		calculator.appendProcess(new RealtimePopularKeywordResultHandler(workingDir, encoding));
		calculator.postProcess(new PostProcess() {

			@Override
			public void handle(String categoryId, Object parameter) {
				if (parameter != null) {
					List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
				}
			}
		});

		task.preProcess(new Runnable() {

			@Override
			public void run() {
				logger.debug("Rolling log file. {}", dailyRawLogger);
				dailyRawLogger.rolling();
			}
		});

		task.addCalculator(calculator);

		return task;
	}
}

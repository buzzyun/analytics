package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Calendar;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.AnalysisTask;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReaderFactory;
import org.fastcatgroup.analytics.analysis2.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis2.handler.CategorySearchLogHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.module.AbstractModule;
import org.fastcatgroup.analytics.module.ModuleException;

public class SiteSearchLogStatisticsModule extends AbstractModule {
	File fileHome;
	String siteId;
	ScheduledTaskRunner<SearchLog> realtimeTaskRunner;
	RollingRawLogger rawLogger;
	
	public SiteSearchLogStatisticsModule(File fileHome, String siteId, Environment environment, Settings settings) {
		super(environment, settings);
		this.fileHome = fileHome;
		this.siteId = siteId;
	}

	@Override
	protected boolean doLoad() throws ModuleException {
		
		File realtimeKeywordBaseDir = new File(new File(fileHome, siteId), "rt");
		
		String logFileName = "tmp.log";
		
		rawLogger = new RollingRawLogger(realtimeKeywordBaseDir, logFileName);
		
		File logFile = new File(realtimeKeywordBaseDir, logFileName);
		String encoding = "utf-8";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		int periodInSeconds = 300;
		int delayInSeconds = 5;
		
		realtimeTaskRunner = new ScheduledTaskRunner<SearchLog>("rt-search-log-task-runner", JobService.getInstance());
		Schedule schedule = new FixedSchedule(cal, periodInSeconds, delayInSeconds);
		AnalysisTask<SearchLog> task = makeAnalysisTask(schedule, logFile, logFileName, encoding);
		realtimeTaskRunner.addTask(task);

		realtimeTaskRunner.start();

		return true;
	}

	@Override
	protected boolean doUnload() throws ModuleException {
		realtimeTaskRunner.cancel();
		try {
			realtimeTaskRunner.join();
		} catch (InterruptedException e) {
		}
		return true;
	}

	public void log(String... data){
		rawLogger.log(data);
	}
	private AnalysisTask<SearchLog> makeAnalysisTask(Schedule schedule, File logDir, String fileName, String encoding) {
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

		Calculator<SearchLog> calculator = new Calculator<SearchLog>("Realtime popular keyword calculator", new CategorySearchLogHandler(workingDir));

		/* 1. 카테고리별로 키워드-갯수를 계산하여 0.log에 쓴다. */
		Set<String> banWords = null;
		int minimumHitCount = 1;
		calculator.appendProcess(new KeyCountProcessHandler(workingDir, fileLimitCount, runKeySize, banWords, minimumHitCount, encoding));

		/* 2. 0.log, 1.log ..를 취합하여 key-count.log 로 통합한다. */
		calculator.appendProcess(new MergeKeyCountProcessHandler(workingDir, resultDir, fileLimitCount, encoding));

		/* 3. count로 정렬하여 key-count-rank.log로 저장. */
		calculator.appendProcess(new KeyCountLogSortHandler(resultDir, encoding, runKeySize));

		/* 4. 구해진 인기키워드를 저장한다. */
		calculator.appendProcess(new KeywordRankDiffHandler(topCount, encoding));
		calculator.appendProcess(new RealtimePopularKeywordResultHandler(resultDir, encoding));
		
		
		task.preProcess(new Runnable(){

			@Override
			public void run() {
				logger.debug("Rolling log file. {}", rawLogger);
				rawLogger.rolling();
			}
		});
		task.addCalculator(calculator);

		return task;
	}
}

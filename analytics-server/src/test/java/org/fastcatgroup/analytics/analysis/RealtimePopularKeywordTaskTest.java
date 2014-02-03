package org.fastcatgroup.analytics.analysis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.analysis2.AnalysisTask;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReaderFactory;
import org.fastcatgroup.analytics.analysis2.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
import org.fastcatgroup.analytics.job.TestJobExecutor;
import org.junit.Test;

public class RealtimePopularKeywordTaskTest {

	@Test
	public void test() throws InterruptedException {

		ScheduledTaskRunner<SearchLog> taskRunner = new ScheduledTaskRunner<SearchLog>("test", new TestJobExecutor());
		File[] f = new File[]{new File("/Users/swsong/tmp/test.log")};
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(f, "utf-8");
		Schedule schedule = new FixedSchedule(new Date(), 2, 1);
		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
		
		File workingDir = new File("/Users/swsong/tmp/working");
		task.preProcess(new RollingLogFileProcess(workingDir, 6));
		
		Calculator<SearchLog> calculator = new Calculator<SearchLog>(new SearchLogHandler());
		
		calculator.appendProcess(new KeyCountProcessHandler());
		
		task.addCalculator(calculator);
		taskRunner.addTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
		
		
//		RealtimePopularKeywordGenerator g = new RealtimePopularKeywordGenerator(targetDir, inFileList, statisticsSettings, fileEncoding);
		
//		List<RankKeyword> result = g.generate();
	}

}

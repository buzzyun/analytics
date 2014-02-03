package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.AnalysisTask;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReaderFactory;
import org.fastcatgroup.analytics.analysis2.LogHandler;
import org.fastcatgroup.analytics.analysis2.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis2.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
import org.fastcatgroup.analytics.job.TestJobExecutor;
import org.junit.Test;

public class ScheduledTasksTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor());
		File[] f = new File[]{new File("/Users/swsong/tmp/test.log")};
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(f, "utf-8");
		Schedule schedule = new FixedSchedule(new Date(), 2, 1);
		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
		taskRunner.addAnalysisTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	
	@Test
	public void testWithCalculator() throws IOException, InterruptedException {
		
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor());
		File[] f = new File[]{new File("/Users/swsong/tmp/test.log")};
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(f, "utf-8");
		Schedule schedule = new FixedSchedule(new Date(), 2, 1);
		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
		
		Calculator<SearchLog> calculator = new Calculator<SearchLog>(new SimpleLogHandler());
		calculator.appendProcess(new SimpleProcessHandler());
		
		task.addCalculator(calculator);
		taskRunner.addAnalysisTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	

}

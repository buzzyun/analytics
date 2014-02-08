package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis.task.AnalysisTask;
import org.fastcatgroup.analytics.job.TestJobExecutor;
import org.junit.Test;

public class ScheduledTasksTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		ScheduledTaskRunner<SearchLog> taskRunner = new ScheduledTaskRunner<SearchLog>("test", new TestJobExecutor());
		File f = new File("/Users/swsong/tmp/test.log");
		Schedule schedule = new FixedSchedule(Calendar.getInstance(), 2, 1);
		AnalysisTask<SearchLog> task = null;
		taskRunner.addTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	
	@Test
	public void testWithCalculator() throws IOException, InterruptedException {
		
		ScheduledTaskRunner<SearchLog> taskRunner = new ScheduledTaskRunner<SearchLog>("test", new TestJobExecutor());
		File f = new File("/Users/swsong/tmp/test.log");
		Schedule schedule = new FixedSchedule(Calendar.getInstance(), 2, 1);
		AnalysisTask<SearchLog> task = null;
		String categoryId = "cat1";
		taskRunner.addTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	

}

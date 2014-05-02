package org.fastcatsearch.analytics.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.schedule.EveryMinuteSchedule;
import org.fastcatsearch.analytics.analysis.schedule.FixedSchedule;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatsearch.analytics.analysis.task.AnalyticsTask;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.job.TestJobExecutor;
import org.junit.Test;

public class ScheduledTasksTest {
	
	@Test
	public void test() throws IOException, InterruptedException {
		
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor(), new Environment("."));
		File f = new File("/Users/swsong/tmp/test.log");
		String siteId = "a";
		List<String> categoryIdList = null;
		Calendar calendar = StatisticsUtils.getCalendar();
		Schedule schedule = new EveryMinuteSchedule(1);
		AnalyticsTask<SearchLog> task = new TestAnalyticsTask("1111", siteId, categoryIdList, schedule, 1);
		taskRunner.addTask(task);
		
		Schedule schedule2 = new EveryMinuteSchedule(1);
		AnalyticsTask<SearchLog> task2 = new TestAnalyticsTask("2222", siteId, categoryIdList, schedule2, 2);
		taskRunner.addTask(task2);

		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	
	@Test
	public void testWithCalculator() throws IOException, InterruptedException {
		
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor(), new Environment("."));
		File f = new File("/Users/swsong/tmp/test.log");
		Schedule schedule = new FixedSchedule(StatisticsUtils.getCalendar(), 2, 1);
		AnalyticsTask<SearchLog> task = null;
		String categoryId = "cat1";
		taskRunner.addTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
	}
	
	
	class TestAnalyticsTask extends AnalyticsTask<SearchLog> {

		private static final long serialVersionUID = 5431881216955668282L;
		
		public TestAnalyticsTask(String name, String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
			super("TEST", name, siteId, categoryIdList, schedule, priority);
		}

		@Override
		protected void prepare(Calendar calendar) {
			
		}


		
	}

}

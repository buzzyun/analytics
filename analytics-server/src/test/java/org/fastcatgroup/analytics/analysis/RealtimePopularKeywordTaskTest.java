package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis.task.AnalyticsTask;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.job.TestJobExecutor;
import org.junit.Test;

public class RealtimePopularKeywordTaskTest {

	@Test
	public void test() throws InterruptedException {

		
		String encoding = "utf-8";
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor(), new Environment("."));
		File f = new File("/Users/swsong/tmp/test.log");
		Schedule schedule = new FixedSchedule(SearchStatisticsProperties.getCalendar(), 3, 1);
		AnalyticsTask<SearchLog> task = null;
		
		File workingDir = new File("/Users/swsong/tmp/rt/working");
		File resultDir = new File("/Users/swsong/tmp/rt/result");
		if(!workingDir.exists()){
			workingDir.mkdirs();
		}
		if(!resultDir.exists()){
			resultDir.mkdirs();
		}
		
		int topCount = 10;
		int fileLimitCount = 6;
		int runKeySize = 10 * 10000;
		
		taskRunner.join();
	}

}

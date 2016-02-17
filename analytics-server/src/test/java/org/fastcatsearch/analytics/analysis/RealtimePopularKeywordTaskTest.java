package org.fastcatsearch.analytics.analysis;

import java.io.File;

import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.schedule.FixedSchedule;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatsearch.analytics.analysis.task.AnalyticsTask;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.job.TestJobExecutor;
import org.junit.Test;

public class RealtimePopularKeywordTaskTest {

	@Test
	public void test() throws InterruptedException {

		
		String encoding = "utf-8";
		ScheduledTaskRunner taskRunner = new ScheduledTaskRunner("test", new TestJobExecutor(), new Environment("."));
		File f = new File("/Users/swsong/tmp/test.log");
		Schedule schedule = new FixedSchedule(StatisticsUtils.getNowCalendar(), 3, 1);
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

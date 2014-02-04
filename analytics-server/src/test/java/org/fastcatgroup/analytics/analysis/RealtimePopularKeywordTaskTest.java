package org.fastcatgroup.analytics.analysis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.analysis2.AnalysisTask;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReaderFactory;
import org.fastcatgroup.analytics.analysis2.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis2.handler.CategoryMergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.CategorySearchLogHandler;
import org.fastcatgroup.analytics.analysis2.handler.CategoryKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.RollingCategoryLogFileProcess;
import org.fastcatgroup.analytics.analysis2.schedule.FixedSchedule;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
import org.fastcatgroup.analytics.job.TestJobExecutor;
import org.junit.Test;

public class RealtimePopularKeywordTaskTest {

	@Test
	public void test() throws InterruptedException {

		String encoding = "utf-8";
		ScheduledTaskRunner<SearchLog> taskRunner = new ScheduledTaskRunner<SearchLog>("test", new TestJobExecutor());
		File f = new File("/Users/swsong/tmp/test.log");
		FileSearchLogReaderFactory readerFactory = new FileSearchLogReaderFactory(f, encoding);
		Schedule schedule = new FixedSchedule(new Date(), 3, 1);
		AnalysisTask<SearchLog> task = new AnalysisTask<SearchLog>(schedule, 0, readerFactory);
		
		File workingDir = new File("/Users/swsong/tmp/working");
		int limitCount = 6;
		task.preProcess(new RollingCategoryLogFileProcess(workingDir, limitCount));
		
		Calculator<SearchLog> calculator = new Calculator<SearchLog>(new CategorySearchLogHandler(workingDir));
		
		/* 1. 카테고리별로 키워드-갯수를 계산하여 0.log에 쓴다. */
		Set<String> banWords = null;
		int minimumHitCount = 1;
		calculator.appendProcess(new CategoryKeyCountProcessHandler(banWords, minimumHitCount));
		/* 2. 0.log, 1.log ..를 취합하여 key-count.log 로 통합한다. */
		File resultDir = new File("/Users/swsong/tmp/result");
		calculator.appendProcess(new CategoryMergeKeyCountProcessHandler(resultDir, limitCount, encoding));
		
		/* 3. 정렬하여 key-count-rank.log로 저장. */
		
		
		
		task.addCalculator(calculator);
		taskRunner.addTask(task);
		taskRunner.start();
		System.out.println("Started " + taskRunner);
		taskRunner.join();
		
		
//		RealtimePopularKeywordGenerator g = new RealtimePopularKeywordGenerator(targetDir, inFileList, statisticsSettings, fileEncoding);
		
//		List<RankKeyword> result = g.generate();
	}

}

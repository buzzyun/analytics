package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyPopularKeywordCalculator;
import org.fastcatgroup.analytics.analysis.log.FileSearchLogReader;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 실시간 인기검색어 통계용 task
 * 
 * */
public class RealtimeSearchLogAnalysisTask extends AnalysisTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	private List<String> categoryIdList;
	private File baseDir;
	private Set<String> banWords;
	private int minimumHitCount;

	public RealtimeSearchLogAnalysisTask(Schedule schedule, int priority) {
		super(schedule, priority);
	}

	@Override
	public void prepare() {
		categoryIdList = new ArrayList<String>();
		categoryIdList.add("_root");
		categoryIdList.add("cat1");
		categoryIdList.add("cat2");
		categoryIdList.add("cat3");
		// baseDir : statistics/search/rt 경로
		baseDir = environment.filePaths().getStatisticsRoot().file("search", "rt");
		banWords = null;
		minimumHitCount = 1;
		int topCount = 10;

		File logFile = new File(baseDir, "raw.log");
		String encoding = SearchStatisticsProperties.encoding;
		try {
			logReader = new FileSearchLogReader(logFile, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		calculatorList = new ArrayList<Calculator<SearchLog>>();

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> calculator = new DailyPopularKeywordCalculator("Realtime popular keyword calculator", baseDir, categoryIdList, banWords, minimumHitCount, topCount);
		calculatorList.add(calculator);
	}

}

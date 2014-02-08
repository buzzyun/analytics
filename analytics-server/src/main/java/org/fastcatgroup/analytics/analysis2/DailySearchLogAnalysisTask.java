package org.fastcatgroup.analytics.analysis2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.DailyPopularKeywordCalculator;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class DailySearchLogAnalysisTask extends AnalysisTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	private List<String> categoryIdList;
	private File baseDir;
	private Set<String> banWords;
	private int minimumHitCount;

	public DailySearchLogAnalysisTask(Schedule schedule, int priority) {
		super(schedule, priority);
	}

	@Override
	public void prepare() {
		categoryIdList = null; // _root
		// baseDir : statistics/search/date 경로
		baseDir = null;
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
		Calculator<SearchLog> calculator = new DailyPopularKeywordCalculator("Daily popular keyword calculator", baseDir, categoryIdList, banWords, minimumHitCount, topCount);
		calculatorList.add(calculator);
	}

}

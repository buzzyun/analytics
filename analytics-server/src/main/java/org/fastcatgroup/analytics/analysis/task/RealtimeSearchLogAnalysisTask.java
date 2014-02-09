package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.RollingRawLogger;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.RealtimePopularKeywordCalculator;
import org.fastcatgroup.analytics.analysis.log.FileSearchLogReader;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 실시간 인기검색어 통계용 task
 * 
 * */
public class RealtimeSearchLogAnalysisTask extends AnalysisTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	private File baseDir;
	private Set<String> banWords;
	private int minimumHitCount;
	private RollingRawLogger realtimeRawLogger;
	
	public RealtimeSearchLogAnalysisTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, RollingRawLogger realtimeRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.realtimeRawLogger = realtimeRawLogger;
	}

	@Override
	public void prepare() {
		// baseDir : statistics/search/rt/data 경로
		baseDir = environment.filePaths().getStatisticsRoot().file("search", "rt", "data", siteId);
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

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> calculator = new RealtimePopularKeywordCalculator("Realtime popular keyword calculator", baseDir, siteId, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(calculator);
	}

	@Override
	protected void preProcess(){
		realtimeRawLogger.rolling();
	}
	
}

package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyRelateKeywordCalculator;
import org.fastcatgroup.analytics.analysis.log.FileLogReader;
import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 연관검색어 계산 task.
 * 
 * */
public class RelateSearchLogAnalysisTask extends AnalysisTask<RelateSearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public RelateSearchLogAnalysisTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	public void prepare() {
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		Calendar calendar = Calendar.getInstance();
		File baseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, calendar), siteId);
		Set<String> banWords = null;
		int minimumHitCount = 1;
		int topCount = 10;

		File logFile = new File(baseDir, "raw.log");
		String encoding = SearchStatisticsProperties.encoding;
		try {
			logReader = new FileLogReader<RelateSearchLog>(logFile, encoding){

				@Override
				protected RelateSearchLog makeLog(String[] el) {
					return new RelateSearchLog(el[0], el[1], el.length >= 3 ? el[2] : "");
				}
				
			};
		} catch (IOException e) {
			logger.error("", e.getMessage());
		}

		Calculator<RelateSearchLog> relateKeywordCalculator = new DailyRelateKeywordCalculator("Daily relate keyword calculator", baseDir, encoding, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(relateKeywordCalculator);
	}

}

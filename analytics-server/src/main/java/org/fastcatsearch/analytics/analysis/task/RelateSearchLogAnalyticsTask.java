package org.fastcatsearch.analytics.analysis.task;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.RAW_LOG_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.DailyRelateKeywordCalculator;
import org.fastcatsearch.analytics.analysis.log.RelateSearchLog;
import org.fastcatsearch.analytics.analysis.log.RelateSearchLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
/**
 * 일별 연관검색어 계산 task.
 * 
 * */
public class RelateSearchLogAnalyticsTask extends AnalyticsTask<RelateSearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public RelateSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	public void prepare(Calendar calendar) {
		File dir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		File baseDir = new File(StatisticsUtils.getDayDataDir(dir, calendar), siteId);
		
		File logFile = new File(baseDir, RAW_LOG_FILENAME);
		try {
			logReader = new RelateSearchLogReader(new File[] {logFile}, StatisticsProperties.encoding);
		} catch (IOException e) {
			logger.error("", e);
		}

		Calculator<RelateSearchLog> relateKeywordCalculator = new DailyRelateKeywordCalculator("Daily relate keyword calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(relateKeywordCalculator);
	}

}

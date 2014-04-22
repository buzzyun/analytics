package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.DailyRawLogger;
import org.fastcatgroup.analytics.analysis.StatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.service.ServiceManager;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;
/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class DailyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	DailyRawLogger dailyTypeRawLogger;
	
	public DailyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority, DailyRawLogger dailyTypeRawLogger) {
		super(siteId, categoryIdList, schedule, priority);
		this.dailyTypeRawLogger = dailyTypeRawLogger;
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		SiteAttribute siteAttribute = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute();
		List<TypeSetting> typeList = siteAttribute.getTypeList();
		
		logger.debug("@@@@typeList > {} {}", "", typeList);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
		File baseDir = new File(StatisticsUtils.getDayDataDir(dir, calendar), siteId);

		File logFile = new File(baseDir, TYPE_RAW_FILENAME);
		try {
			if(logFile.exists()){
				logReader = new TypeSearchLogReader(new File[] { logFile }, StatisticsProperties.encoding);
			}
		} catch (IOException e) {
			logger.error("", e);
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> dailyTypeHitCalculator = new DailyTypeHitCalculator("Daily type hit calculator", calendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(dailyTypeHitCalculator);
		
	}

	@Override
	protected void preProcess() {
		if (dailyTypeRawLogger != null) {
			dailyTypeRawLogger.rolling();
		}
	}
}

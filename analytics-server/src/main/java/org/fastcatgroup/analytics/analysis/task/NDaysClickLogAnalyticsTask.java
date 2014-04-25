package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.MonthlyClickKeywordHitCalculator;
import org.fastcatgroup.analytics.analysis.calculator.NDaysClickKeywordHitCalculator;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CTRSetting;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.service.ServiceManager;

/**
 * 월별 클릭로그 계산 task
 * 
 * */
public class NDaysClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;
	
	private boolean copyTo;

	public NDaysClickLogAnalyticsTask(String siteId, List<String> categoryIdList, boolean copyTo, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
		this.copyTo = copyTo;
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		CTRSetting ctrSetting = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getCtrSetting();
		Integer nDays = ctrSetting.getDumpFileDaySize();
		String targetFilePath = null;
		if(copyTo) {
			targetFilePath = ctrSetting.getTargetFilePath();
		}
		logger.trace("copyTo:{}, targetFilePath:{}", copyTo, targetFilePath);
		if(nDays == null) {
			return;
		}
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> popularKeywordCalculator = new NDaysClickKeywordHitCalculator("NDays click log calculator", calendar, baseDir, siteId, categoryIdList, nDays, targetFilePath);
		addCalculator(popularKeywordCalculator);
	}
}
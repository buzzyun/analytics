package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankInterface;
import org.fastcatgroup.analytics.analysis.calculator.YearlyKeywordHitAndRankCalculator;
import org.fastcatgroup.analytics.analysis.log.CategoryKeyLogReader;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class YearlySearchLogAnalyticsTask extends AnalyticsTask<SearchLog>  implements KeywordHitAndRankInterface {

	private static final long serialVersionUID = 4212969890908932929L;

	public YearlySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		//해당년도의 최초로 되돌린다.
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.YEAR, 1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.YEAR, -1);
		File baseDir = new File(SearchStatisticsProperties.getYearDataDir(dir, calendar), siteId);
		File prevDir = new File(SearchStatisticsProperties.getYearDataDir(dir, prevCalendar), siteId);
		Set<String> banWords = null;
		int minimumHitCount = 1;
		int topCount = 10;
		
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getMonthDiff(prevCalendar, calendar);
		
		//1년치의 월별 로그를 머징한다.
		//따로 raw데이터가 없기 때문에 말 그대로 사이트 내 월별 카테고리 로그들을 각각 머징한다. (key-count)
		List<File> fileList = new ArrayList<File>();
		Calendar monthlyCalendar = (Calendar) calendar.clone();
		//모든 카테고리 안의 key-count-log 들을 모두 프로세스에 태운다.
		//raw-log 는 카테고리를 포함하고 있지만 월별로그는 key-count-log 만 있기 때문에 카테고리를 파일명에서 추정해서 사용한다.
		for(int inx=0;inx < diff; inx++) {
			//카테고리 목록이 나올것임.
			File monthDir = new File(SearchStatisticsProperties.getMonthDataDir(dir, monthlyCalendar), siteId);
			logger.debug("monthDir:[{}:{}]{}", inx, diff, monthDir);
			if(monthDir!=null && monthDir.exists() && monthDir.isDirectory()) {
				logger.debug("file:{}",monthDir);

				File[] categoryDirs = monthDir.listFiles();
				if(categoryDirs!=null) {
					for(File categoryDir : categoryDirs) {
						if (categoryDir.exists() && categoryDir.isDirectory()) {
							fileList.add(new File(categoryDir, KEY_COUNT_FILENAME));
						}
					}
				}
			}
			monthlyCalendar.add(Calendar.MONTH, -1);
		}
		
		File[] files = new File[fileList.size()];
		
		try {
			logReader = new CategoryKeyLogReader(fileList.toArray(files), encoding);
		} catch (IOException e) {
			logger.error("", e);
		}
		
		logger.debug("calculating{} {}", "",fileList);
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new YearlyKeywordHitAndRankCalculator("Yearly popular keyword calculator", calendar, baseDir, prevDir, siteId, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(popularKeywordCalculator);
	}
}
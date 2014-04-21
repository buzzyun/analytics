package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class DumpPopularKeywordHitCalculator extends Calculator<ClickLog> {
	
	private File targetFile;
	private File tmpFile;
	private Calendar fromDate;
	
	public DumpPopularKeywordHitCalculator(File baseDir, Calendar fromDate, Calendar toDate, File tmpFile, File targetFile, String siteId, List<String> categoryIdList) {
		super("", toDate, baseDir, siteId, categoryIdList);
		this.targetFile = targetFile;
		this.tmpFile = tmpFile;
		this.fromDate = fromDate;
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
		
		int runKeySize = 1000;
		
		if(categoryId.equals("_root")) {
			String encoding = StatisticsUtils.encoding;
			
			Calendar dailyCalendar = (Calendar) calendar.clone();
			
			int diff = StatisticsUtils.getDateDiff(fromDate, calendar);
			
			File workingDir = targetFile.getParentFile();
			
			String tmpFileName = tmpFile.getName();
			
			String fileName = targetFile.getName();
			
			File[] clickLogFiles = new File[diff];
			
			for (int inx = 0; inx < diff; inx++) {
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				clickLogFiles[inx] = new File(new File(new File(timeDir, siteId), categoryId),KEY_COUNT_FILENAME);
				dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
			}
			
			KeyCountRunEntryParser parser = new KeyCountRunEntryParser(new int[]{0}, 1 );
			
			MergeKeyCountProcessHandler mergeProcessHandler = new MergeKeyCountProcessHandler(clickLogFiles, workingDir, tmpFileName, encoding, parser);
			mergeProcessHandler.attachProcessTo(categoryProcess);
			
			new KeyCountLogSortHandler(workingDir, tmpFileName, fileName, encoding, runKeySize, parser).appendTo(mergeProcessHandler);
			
		}
		return categoryProcess;
	}
}

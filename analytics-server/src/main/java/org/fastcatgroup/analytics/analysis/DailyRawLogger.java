package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 월별 디렉토리에 일별로그 데이터를 기록하는 로거.
 * */
public class DailyRawLogger {
	private static final Logger logger = LoggerFactory.getLogger(DailyRawLogger.class);
	
	private BufferedLogger aLogger;
	private File file;

	public DailyRawLogger(Calendar calendar, File baseDir) {
		
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH));
		String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		
		File targetDir = new File(new File(baseDir, year), month);
		targetDir.mkdirs();
		
		file = new File(targetDir, "D"+day+".log");
		aLogger = new BufferedLogger(file, true);
	}

	//0번째 요소는 카테고리Id 이다.
	public void log(String[] data) {
		aLogger.log(data);
	}


	public void close() {
		if(aLogger != null){
			aLogger.close();
		}
	}

}

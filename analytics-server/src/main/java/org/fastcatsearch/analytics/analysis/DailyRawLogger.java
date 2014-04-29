package org.fastcatsearch.analytics.analysis;

import java.io.File;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 읿별 디렉토리에 일별로그 데이터를 기록하는 로거.
 * */
public class DailyRawLogger {
	private static final Logger logger = LoggerFactory.getLogger(DailyRawLogger.class);

	private BufferedLogger writer;
	private File baseDir;
	private String fileName;

	public DailyRawLogger(Calendar calendar, File baseDir, String fileName) {
		this.baseDir = baseDir;
		this.fileName = fileName;
		this.writer = newLogger(StatisticsUtils.getCalendar());
	}

	public void rolling() {
		BufferedLogger prevWriter = writer;
		writer = newLogger(StatisticsUtils.getCalendar());
		
		if (prevWriter != null) {
			prevWriter.close();
		}
	}

	private BufferedLogger newLogger(Calendar calendar) {
		File targetDir = StatisticsUtils.getDayDataDir(baseDir, calendar);
		targetDir.mkdirs();

		File logFile = new File(targetDir, fileName);
		BufferedLogger logger = new BufferedLogger(logFile, true);
		return logger;
	}

	public void log(String[] data) {
		writer.log(data);
	}
	
	public void close() {
		if (writer != null) {
			writer.close();
		}
	}

}

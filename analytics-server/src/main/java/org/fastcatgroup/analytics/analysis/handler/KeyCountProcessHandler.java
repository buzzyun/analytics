package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.log.KeyCountLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountLogReader;
import org.fastcatgroup.analytics.analysis.log.SearchLogResult;

public class KeyCountProcessHandler extends ProcessHandler {

	private File baseDir;
	private String inFileName;
	private String encoding;

	public KeyCountProcessHandler(File baseDir, String inFileName, String encoding) {
		this.baseDir = baseDir;
		this.inFileName = inFileName;
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) {

		File inFile = new File(baseDir, inFileName);
		logger.debug("Process file > {}", inFile);
		KeyCountLogReader reader = null;
		int totalCount = 0;
		try {
			reader = new KeyCountLogReader(new File[]{inFile}, encoding);
			KeyCountLog log = null;
			while ((log = reader.readLog()) != null) {
				totalCount += log.getCount();
			}
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
//		return totalCount;
		
		
		//FIXME max, avgtime을 어떻게 얻어올까?
		
		return new SearchLogResult(totalCount, 0, 0, 0);
	}
}

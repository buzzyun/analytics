package org.fastcatgroup.analytics.analysis2.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;

/**
 * tmp.log 파일을 key-count로 합산한 0.log로 만든다.
 * */
public class CategoryKeyCountProcessHandler extends ProcessHandler {

	private KeyCountLogAggregator handler;
	private Set<String> banWords;
	private int minimumHitCount;
	
	public CategoryKeyCountProcessHandler(Set<String> banWords, int minimumHitCount){
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
	}
	@Override
	public void reset() {

	}

	@Override
	public Object process(Object parameter) {
		Object[] params = (Object[]) parameter;
		File baseDir = (File) params[0];
		Set<String> categoryIdSet = (Set<String>) params[1];

		for (String categoryId : categoryIdSet) {
			File categoryDir = new File(baseDir, categoryId);
			File inFile = new File(categoryDir, "tmp.log");
			logger.debug("Process file > {}", inFile);
			int runKeySize = 10 * 10000;
			String outputEncoding = "utf-8";
			String targetFilename = "0.log";

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), outputEncoding));
				handler = new KeyCountLogAggregator(categoryDir, targetFilename, runKeySize, outputEncoding, banWords, minimumHitCount);
				String line = null;
				while ((line = reader.readLine()) != null) {
					handler.handleLog(line);
				}

				handler.done();
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
		
		return parameter;
	}


}

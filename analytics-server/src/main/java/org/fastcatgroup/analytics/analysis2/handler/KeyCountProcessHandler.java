package org.fastcatgroup.analytics.analysis2.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;

/**
 * tmp.log 파일을 key-count로 합산한 0.log로 만든다.
 * */
public class KeyCountProcessHandler extends ProcessHandler {

	private KeyCountLogAggregator handler;
	private File targetDir;

	@Override
	public void reset() {

	}

	@Override
	public void process(Object[] parameters) {
		File inFile = (File) parameters[0];
		int runKeySize = 10 * 10000;
		String outputEncoding = "utf-8";
		Set<String> banWords = null;
		int minimumHitCount = 5;
		String targetFilename = "0.log";

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), outputEncoding));
			handler = new KeyCountLogAggregator(targetDir, targetFilename, runKeySize, outputEncoding, banWords, minimumHitCount);
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

	@Override
	protected Object[] doDone() {
		return new Object[] { targetDir };
	}

}

package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

public class KeyCountEmptyLogAggregator<LogType extends SearchLog> extends KeyCountLogAggregator<LogType> {

	public KeyCountEmptyLogAggregator(File targetDir, String targetFilename, int runKeySize, String outputEncoding, int minimumHitCount, EntryParser<KeyCountRunEntry> entryParser) {
		super(targetDir, targetFilename, runKeySize, outputEncoding, minimumHitCount, entryParser);
		this.runTmpDir = new File(targetDir, "_emprun");
	}
	
	@Override
	public void handleLog(LogType log) throws IOException {
		//결과갯수가 0 인것만 집계
		if(log.getResultCount() == 0) {
			super.handleLog(log); // key-count log
		}
	}
}

package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

/**
 * 연관검색어를 만들기 위한 handler KeyCountLogAggregator를 구현하여 run파일의 위치와 최종 머저를 생성한다.
 * 
 * */
public class RelateKeyCountLogAggregator extends KeyCountLogAggregator<RelateSearchLog> {

	public RelateKeyCountLogAggregator(File targetDir, String targetFilename, int runKeySize, String outputEncoding, Set<String> banWords, int minimumHitCount, EntryParser<KeyCountRunEntry> entryParser) {
		super(targetDir, targetFilename, runKeySize, outputEncoding, banWords, minimumHitCount, entryParser);
	}

	@Override
	protected boolean checkLog(RelateSearchLog log) {
		if (banWords != null) {
			for (String banWord : banWords) {
				if (log.keyword() == null || log.keyword().contains(banWord) || log.previousKeyword() == null || log.previousKeyword().contains(banWord)) {
					// 금지어의 경우 로그에 기록하지 않는다.
					return false;
				}
			}
		}
		return true;
	}
	
}

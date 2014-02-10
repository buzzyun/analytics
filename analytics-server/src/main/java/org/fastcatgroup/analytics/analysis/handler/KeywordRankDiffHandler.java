package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.util.List;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatgroup.analytics.analysis.util.KeywordLogRankDiffer;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;

public class KeywordRankDiffHandler extends ProcessHandler {
	File rankLogFile;
	File compareRankLogFile;
	int topCount;
	String encoding;
	EntryParser<KeyCountRunEntry> entryParser;
	
	public KeywordRankDiffHandler(File rankLogFile, File compareRankLogFile, int topCount, String encoding, EntryParser<KeyCountRunEntry> entryParser){
		this.rankLogFile = rankLogFile;
		this.compareRankLogFile = compareRankLogFile;
		this.topCount = topCount;
		this.encoding = encoding;
		this.entryParser = entryParser;
	}
	
	@Override
	public Object process(Object parameter) throws Exception {
		/*
		 * key-count-rank.log의 상위 N개를 읽어들여, key-count-rank-prev.log에서의 순위변동을 계산한다. 
		 * key-count-rank-prev.log 파일을 순차적으로 읽으면서 key-count-rank.log의 상위 N개 단어를 확인하고 모두 확인했으면 탐색종료하는 방법을 사용한다. 
		 * 없으면 new, 있으면 +/- 순위변동값.
		 */
		KeywordLogRankDiffer differ = new KeywordLogRankDiffer(rankLogFile, compareRankLogFile, topCount, encoding, entryParser);
		List<RankKeyword> result = differ.diff();
		
		return result;
	}

}

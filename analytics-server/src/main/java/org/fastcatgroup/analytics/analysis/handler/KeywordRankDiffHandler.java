package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.util.List;

import org.fastcatgroup.analytics.analysis.util.KeywordLogRankDiffer;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;

public class KeywordRankDiffHandler extends ProcessHandler {
	int topCount;
	String encoding;
	
	public KeywordRankDiffHandler(int topCount, String encoding){
		this.topCount = topCount;
		this.encoding = encoding;
	}
	
	@Override
	public Object process(Object parameter) throws Exception {
		File[] files = (File[]) parameter;
		File rankFile = files[0];
		File prevRankFile = files[1];
		/*
		 * key-count-rank.log의 상위 N개를 읽어들여, key-count-rank-prev.log에서의 순위변동을 계산한다. 
		 * key-count-rank-prev.log 파일을 순차적으로 읽으면서 key-count-rank.log의 상위 N개 단어를 확인하고 모두 확인했으면 탐색종료하는 방법을 사용한다. 
		 * 없으면 new, 있으면 +/- 순위변동값.
		 */
		KeywordLogRankDiffer differ = new KeywordLogRankDiffer(rankFile, prevRankFile, topCount, encoding);
		List<RankKeyword> result = differ.diff();
		
		return result;
	}

}

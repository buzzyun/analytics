package org.fastcatsearch.analytics.analysis.util;

import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.FileRunEntryReader;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.ir.io.CharVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordLogRankLiteDiffer {

	protected static Logger logger = LoggerFactory.getLogger(KeywordLogRankLiteDiffer.class);

	private File targetFile;
	private File compareFile;
	private int topCount;
	private String encoding;
    private EntryParser<KeyCountRunEntry> entryParser;

	public KeywordLogRankLiteDiffer(File targetFile, File compareFile, int topCount, String encoding, EntryParser<KeyCountRunEntry> entryParser) {
		this.targetFile = targetFile;
		this.compareFile = compareFile;
		this.topCount = topCount;
		this.encoding = encoding;
		this.entryParser = entryParser;
	}

	public List<RankKeyword> diff() {
		List<RankKeyword> result = new ArrayList<RankKeyword>();
		try {
			// 1. target 파일에서 top N개를 뽑아낸다.
			FileRunEntryReader<KeyCountRunEntry> targetReader = null;
			try {
				targetReader = new FileRunEntryReader<KeyCountRunEntry>(targetFile, encoding, entryParser);
				int rank = 1; // 1부터 시작한다.
				while (targetReader.next()) {
					if (rank > topCount) {
						break;
					}
					
					KeyCountRunEntry entry = targetReader.entry();
//					logger.trace("@@rank diff {} > {}", rank, entry);
					
					RankKeyword rankKeyword = new RankKeyword(entry.getKey(), rank++, entry.getCount());
					result.add(rankKeyword);
				}
			} finally {
				if (targetReader != null) {
					targetReader.close();
				}
			}

            Map<String, RankKeyword> compareMap = new HashMap<String, RankKeyword>();
			// 2. compareFile를 순차로 읽으면서 해당 키워드가 있는지 확인한다.
            // Lite 버전에서는 이전 키워드 리스트도 topCount 만큼만 비교한다. 모두 비교할 경우 연산이 너무 길어지는 문제에 대한 해결방안임.
			if (compareFile.exists()) {
                FileRunEntryReader<KeyCountRunEntry> compareReader = null;
                try {
                    compareReader = new FileRunEntryReader<KeyCountRunEntry>(compareFile, encoding, entryParser);
                    int rank = 1; // 1부터 시작한다.
                    while (compareReader.next()) {
                        if (rank > topCount) {
                            break;
                        }

                        KeyCountRunEntry entry = compareReader.entry();
//                        logger.trace("@@rank diff {} > {}", rank, entry);

                        RankKeyword rankKeyword = new RankKeyword(entry.getKey(), rank++, entry.getCount());
//                        result.add(rankKeyword);
                        compareMap.put(entry.getKey(), rankKeyword);
                    }
                } finally {
                    if (compareReader != null) {
                        compareReader.close();
                    }
                }
            }


            for(RankKeyword keyword : result) {
                RankKeyword prevRankKeyword = compareMap.get(keyword.getKeyword());

                if (prevRankKeyword != null) {

                    int rankDiff = prevRankKeyword.getRank() - keyword.getRank();
                    int countDiff = keyword.getCount() - prevRankKeyword.getCount();
                    keyword.setRankDiff(Math.abs(rankDiff));
                    keyword.setCountDiff(countDiff);
                    if (rankDiff == 0) {
                        keyword.setRankDiffType(RankDiffType.EQ);
                    } else if (rankDiff > 0) {
                        keyword.setRankDiffType(RankDiffType.UP);
                    } else {
                        keyword.setRankDiffType(RankDiffType.DN);
                    }
                }
            }

		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
		logger.trace("result size : {} / targetFile:{}", result.size(), targetFile);
		return result;
	}
}

package org.fastcatgroup.analytics.analysis;

import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.analysis2.Calculator;
import org.fastcatgroup.analytics.analysis2.Calculator.PostProcess;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis2.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis2.handler.RealtimePopularKeywordResultHandler;

public class DailyPopularKeywordCalculator extends Calculator<LogType> {
	
	
	
	public void prepare(){
		/* 1. 카테고리별로 키워드-갯수를 계산하여 key-count.log에 쓴다. */
		Set<String> banWords = null;
		int minimumHitCount = 1;
		appendProcess(new KeyCountProcessHandler(workingDir, tempFileName, outFileName, runKeySize, banWords, minimumHitCount, encoding));

		/* 2. count로 정렬하여 key-count-rank.log로 저장. */
		appendProcess(new KeyCountLogSortHandler(workingDir, encoding, runKeySize));

		/* 3. 구해진 인기키워드를 저장한다. */
		appendProcess(new KeywordRankDiffHandler(topCount, encoding));
		appendProcess(new RealtimePopularKeywordResultHandler(workingDir, encoding));
		postProcess(new PostProcess() {

			@Override
			public void handle(String categoryId, Object parameter) {
				if (parameter != null) {
					List<RankKeyword> keywordList = (List<RankKeyword>) parameter;
					statisticsService.updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
				}
			}
		});
		
	}
	
	
	
}

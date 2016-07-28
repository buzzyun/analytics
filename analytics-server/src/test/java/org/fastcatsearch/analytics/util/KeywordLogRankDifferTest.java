package org.fastcatsearch.analytics.util;

import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.util.KeywordLogRankDiffer;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_RANK_FILENAME;
import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_RANK_PREV_FILENAME;

/**
 * Created by swsong on 2016. 7. 28..
 */
public class KeywordLogRankDifferTest {

    @Test
    public void diffYearRoot() {
//        diffYear("_root", 50000);
        diffYear("_root", 20000, 10);
    }

    @Test
    public void diffYearCat1() {
        diffYear("cat1", 20000, -1);
    }

    public void diffYear(String cate, int topCount, int resultCount) {
        KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
        String encoding = "utf-8";
        String workingDir = "src/test/resources/yearly/" + cate;
        File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
        File compareRankLogFile = new File(workingDir, KEY_COUNT_RANK_PREV_FILENAME);
        System.out.println("["+cate+"] Category rank log diff start!");
        System.out.println("rankLogFile : " + rankLogFile + " , " + Formatter.getFormatSize(rankLogFile.length()));
        System.out.println("compareRankLogFile : " + compareRankLogFile + " , " + Formatter.getFormatSize(compareRankLogFile.length()));
        System.out.println("Processing...");
        KeywordLogRankDiffer differ = new KeywordLogRankDiffer(rankLogFile, compareRankLogFile, topCount, encoding, entryParser);
        long st = System.currentTimeMillis();
        List<RankKeyword> result = differ.diff();
        long duration = System.currentTimeMillis() - st;
        System.out.println("Done. time = " + Formatter.getFormatTime(duration));
        printResult(result, resultCount);
    }

    private void printResult(List<RankKeyword> result, int topN) {
        int i = 0;
        for(RankKeyword k : result) {
            i++;
            System.out.println(k);
            if(topN != -1 && i >= topN) {
                break;
            }
        }
    }
}

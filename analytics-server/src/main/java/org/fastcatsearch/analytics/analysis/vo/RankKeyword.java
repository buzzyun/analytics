package org.fastcatsearch.analytics.analysis.vo;

import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.ir.io.CharVector;

/**
 * 로그취합결과로 얻어진 인기키워드.
 * */
public class RankKeyword {

	private CharVector keyword;
	private int rank;
	private int count;
	
	private RankDiffType rankDiffType = RankDiffType.NEW; //기본적으로 NEW이다. 
	private int rankDiff;
	
	private int countDiff;
	
	public RankKeyword(String keyword, int rank) {
		this(keyword, rank, 0);
	}
	public RankKeyword(String keyword, int rank, int count) {
		this.keyword = new CharVector(keyword, true);
		this.rank = rank;
		this.count = count;
	}

	public String getKeyword() {
		return keyword.toString();
	}

    public CharVector getKey() {
        return keyword;
    }


	public void setKeyword(String keyword) {
		this.keyword = new CharVector(keyword, true);
	}
	
	public void setKeyword(CharVector keyword) {
		this.keyword = keyword.clone();
		this.keyword.setIgnoreCase();
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public RankDiffType getRankDiffType() {
		return rankDiffType;
	}

	public void setRankDiffType(RankDiffType rankDiffType) {
		this.rankDiffType = rankDiffType;
	}

	public int getRankDiff() {
		return rankDiff;
	}

	public void setRankDiff(int rankDiff) {
		this.rankDiff = rankDiff;
	}
	
	public int getCountDiff() {
		return countDiff;
	}
	
	public void setCountDiff(int countDiff) {
		this.countDiff = countDiff;
	}
	
	public int getCount() {
		return count;
	}

	@Override
	public String toString(){
		return "["+rank+"] " + keyword + " : " + count + " : " + rankDiffType.toString() + rankDiff + " : " + countDiff;
	}

}

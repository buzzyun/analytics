package org.fastcatsearch.analytics.db.vo;


public class RankKeywordVO {
	public enum RankDiffType {UP, DN, NEW, EQ }

	private String categoryId;
	private String timeId;
	private int rank;
	
	private String keyword;
	private int count;
	private int countDiff;
	private RankDiffType rankDiffType;
	private int rankDiff;
	
	public RankKeywordVO(){
	}
	
	public RankKeywordVO(String categoryId, String timeId, String keyword, int count, int rank, int countDiff, RankDiffType rankDiffType, int rankDiff) {
		this.categoryId = categoryId;
		this.timeId = timeId;
		this.keyword = keyword;
		this.count = count;
		this.countDiff = countDiff;
		this.rank = rank;
		this.rankDiffType = rankDiffType;
		this.rankDiff = rankDiff;
	}
	
	public String getCategorIdy() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getTimeId() {
		return timeId;
	}
	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getCountDiff() {
		return countDiff;
	}
	public void setCountDiff(int countDiff) {
		this.countDiff = countDiff;
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

	
}

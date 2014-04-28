package org.fastcatsearch.analytics.util;

import java.util.ArrayList;
import java.util.List;

public class ListableCounter {
	private List<Integer> count;

	public ListableCounter() {
		count = new ArrayList<Integer>();
	}

	public void increment(int index, Integer n) {
		//set 해야 하는 index 보다 배열이 작은 경우
		//index 만큼 배열을 확장한다.
		//배열사이즈보다 index 가 작으면 이부분 무시됨
		for(int i=count.size();i<=index;i++) {
			count.add(0);
		}
		if (n == null) { n = 0; }
		count.set(index, count.get(index) + n);
	}
	
	public List<Integer> list() {
		return count;
	}

	public int value() {
		int total = 0;
		for(int inx=0;inx<count.size();inx++) {
			total += count.get(inx);
		}
		return total;
	}
	
	@Override
	public String toString(){
		return String.valueOf(count);
	}
}

package org.fastcatgroup.analytics.util;

import java.util.ArrayList;
import java.util.List;

public class ListableCounter {
	private List<Integer> count;

	public ListableCounter(int i) {
		count = new ArrayList<Integer>();
		count.add(i);
	}

	public void increment(int index, Integer n) {
		if(count.size() <= index) {
			for(int i=count.size();i<=index;i++) {
				count.add(0);
			}
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

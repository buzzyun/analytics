package org.fastcatgroup.analytics.util;

public class Counter {
	private int count;

	public Counter(int i) {
		count = i;
	}

	public void increment(int n) {
		count+=n;
	}

	public int value() {
		return count;
	}
	
	@Override
	public String toString(){
		return String.valueOf(count);
	}
}

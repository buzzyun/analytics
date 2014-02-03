package org.fastcatgroup.analytics.util;

public class Counter {
	private int count;

	public Counter(int i) {
		count = i;
	}

	public void increment() {
		count++;
	}

	public int value() {
		return count;
	}
	
	@Override
	public String toString(){
		return String.valueOf(count);
	}
}

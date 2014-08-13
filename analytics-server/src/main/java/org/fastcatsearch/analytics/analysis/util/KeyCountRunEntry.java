package org.fastcatsearch.analytics.analysis.util;

/**
 * 메모리에 합산된 키워드별 갯수 데이터의 일부를 flush한 파일의 한 Entry를 나타낸다. 
 * */
public class KeyCountRunEntry extends RunEntry {
	private String key;
	private int count;

	public KeyCountRunEntry(){
		super(null);
	}
	
	public KeyCountRunEntry(String rawLine, String key, int count) {
		super(rawLine);
		this.key = key;
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(Object o) {
		if(key == null){
			return 1;
		}
		KeyCountRunEntry e = (KeyCountRunEntry) o;
		if(e.key == null){
			return -1;
		}
		String k1 = key.toLowerCase();
		String k2 = e.key.toLowerCase();
		
		if(k1.equals(k2)) {
			return 0;
		}
		
		return k1.compareTo(k2);
	}

	@Override
	public void merge(RunEntry other) {
		KeyCountRunEntry o = (KeyCountRunEntry) other;
		count += o.count;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName()+"] "+key + ": "+ count;
	}

	@Override
	public boolean equals(Object obj) {
		if(key!=null) {
			return key.equalsIgnoreCase(((KeyCountRunEntry)obj).key);
		}
		return false;
	}
}

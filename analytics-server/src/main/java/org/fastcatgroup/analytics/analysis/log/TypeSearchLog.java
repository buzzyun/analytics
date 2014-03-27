package org.fastcatgroup.analytics.analysis.log;

public class TypeSearchLog extends LogData {

	protected String[] el;
	
	protected int count;
	
	public TypeSearchLog(String[] el, int count) {
		this.el = el;
		this.count = count;
	}

	public String categoryId() {
		return el[0];
	}

	public String keyword() {
		return el[1];
	}

	public String getType(int i){
		return el[i + 2];
	}
	
	public int typeLength(){
		return el.length - 2;
	}
	
	@Override
	public String getKey() {
		return el[0];
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		for(String type : el){
			if(sb.length() > 0){
				sb.append("\t");
			}
			sb.append(type);
		}
		return sb.toString();
	}
}

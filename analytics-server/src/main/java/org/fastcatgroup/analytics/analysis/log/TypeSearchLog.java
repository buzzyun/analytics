package org.fastcatgroup.analytics.analysis.log;

public class TypeSearchLog extends LogData {

	protected String[] el;
	
	protected int count;
	
	public TypeSearchLog(String[] el, int count) {
		this.el = el;
		this.count = count;
	}
	
	public String getTime() {
		return el[0];
	}

	public String categoryId() {
		return el[1];
	}

	public String keyword() {
		return el[2];
	}

	public String getType(int i){
		return el[i + 3];
	}
	
	public int typeLength(){
		return el.length - 3;
	}
	
	@Override
	public String getKey() {
		return el[1];
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

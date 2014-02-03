package org.fastcatgroup.analytics.analysis.log;

public class LogData {

	protected String[] data;
	
	public LogData(String[] data){
		this.data = data;
	}

	public String[] getData() {
		return data;
	}
	
	public String getKey() {
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(String d : data){
			sb.append(d);
			sb.append(" ");
		}
		return sb.toString();
	}
}

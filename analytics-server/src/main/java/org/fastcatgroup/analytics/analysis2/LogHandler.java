package org.fastcatgroup.analytics.analysis2;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;

/**
 * 로그파일을 읽어들이면서 통계작업수행.
 * */
public abstract class LogHandler<LogType extends LogData> extends ProcessHandler {
	
	
	/**
	 * 로그가 한줄단위 tab구분된 LogData로 이 메소드로 전달된다.
	 * */
	public abstract void handleLog(LogType logData);

	

}

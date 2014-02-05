package org.fastcatgroup.analytics.analysis2;

import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 로그파일을 읽어들이면서 통계작업수행.
 * */
public abstract class CategoryLogHandler<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(CategoryLogHandler.class);
	
	//초기화.
	public abstract void reset();
	
	/**
	 * 로그가 한줄단위 tab구분된 LogData로 이 메소드로 전달된다.
	 * */
	public abstract void handleLog(LogType logData);

	/**
	 * 마무리작업 및 카테고리 set를 전달한다.
	 * */
	public abstract Set<String> done();

}

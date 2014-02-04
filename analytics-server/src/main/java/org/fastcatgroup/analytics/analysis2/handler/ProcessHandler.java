package org.fastcatgroup.analytics.analysis2.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 앞의 handler의 결과를 받아서 이번 작업을 수행하는 handler
 * */
public abstract class ProcessHandler {
	protected static Logger logger = LoggerFactory.getLogger(ProcessHandler.class);

	//초기화.
	public abstract void reset();
	
	/**
	 * 전달된 parameter 로 통계수행.
	 * @param parameter
	 */
	public abstract Object process(Object parameter);
	
}

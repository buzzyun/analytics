package org.fastcatgroup.analytics.analysis2;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 순차적으로 수행되는 handler를 담고있는 객체.
 * 
 * */
public class Calculator<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(Calculator.class);
	
	protected LogHandler<LogType> logHandler;
	private ProcessHandler tail;
	
	public Calculator(LogHandler<LogType> logHandler) {
		this.logHandler = logHandler;
		tail = logHandler;
	}

	/**
	 * calculate작업의 마무리를 한다.
	 * */
	public final void done() {
		logHandler.done();
	}
	
	public final void offerLog(LogType logData) {
		logHandler.handleLog(logData);
	}

	public ProcessHandler appendProcess(ProcessHandler handler) {
		tail.next(handler);
		tail = handler;
		return handler;
	}

	/**
	 * 초기화
	 * */
	public void reset() {
		if(logHandler != null){
			logHandler.reset();
		}
	}

}

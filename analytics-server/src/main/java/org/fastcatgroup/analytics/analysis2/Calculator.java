package org.fastcatgroup.analytics.analysis2;

import java.util.ArrayList;
import java.util.List;

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
	private List<ProcessHandler> handlerList;
	
	public Calculator(LogHandler<LogType> logHandler) {
		this.logHandler = logHandler;
		handlerList = new ArrayList<ProcessHandler>();
	}

	/**
	 * logHandler로 읽은 로그를 바탕으로 다음 프로세스를 진행한다.
	 * */
	public final void calculate() {
		Object parameter = logHandler.done();
		logger.debug("## calculate {} > {}", this, handlerList);
		for(ProcessHandler handler : handlerList){
			handler.reset();
			logger.debug("# handler process {}", parameter);
			parameter = handler.process(parameter);
		}
	}
	
	public final void offerLog(LogType logData) {
		logHandler.handleLog(logData);
	}

	public void appendProcess(ProcessHandler handler) {
		handlerList.add(handler);
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

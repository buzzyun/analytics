package org.fastcatgroup.analytics.analysis2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis2.Calculator.PostProcess;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 순차적으로 수행되는 handler를 담고있는 객체.
 * 
 * */
public abstract class Calculator<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(Calculator.class);

	protected String name;
	protected CategoryLogHandler<LogType> logHandler;
	private List<ProcessHandler> processList;
	private PostProcess postProcess;
	
	public Calculator(String name) {
		this(name, null);
	}
	public Calculator(String name, CategoryLogHandler<LogType> logHandler) {
		this.name = name;
		this.logHandler = logHandler;
		processList = new ArrayList<ProcessHandler>();
	}

	protected abstract void prepareProcess();
		
	/**
	 * logHandler로 읽은 로그를 바탕으로 다음 프로세스를 진행한다.
	 * */
	public final void calculate() throws Exception {
		prepareProcess();

		Set<String> categoryIdSet = logHandler.done();

		Object parameter = null;
		for (String categoryId : categoryIdSet) {
			logger.debug("## calculate [{}] > {}", categoryId, this);
			for (ProcessHandler handler : processList) {
				handler.reset();
				logger.debug("# handler process {} > {}", categoryId, handler.getClass().getSimpleName());
				parameter = handler.process(categoryId, parameter);
			}
			
			if(postProcess != null){
				postProcess.handle(categoryId, parameter);
			}
		}
		
	}

	public final void offerLog(LogType logData) {
		if(logHandler != null){
			logHandler.handleLog(logData);
		}
	}

	public void appendProcess(ProcessHandler handler) {
		processList.add(handler);
	}

	protected void postProcess(PostProcess postProcess){
		this.postProcess = postProcess;
	}
	
	/**
	 * 초기화
	 * */
	public void reset() {
		if (logHandler != null) {
			logHandler.reset();
		}
	}

	@Override
	public String toString(){
		return getClass().getSimpleName() +" [" + name + "]";
	}
	
	public static abstract class PostProcess {
		public abstract void handle(String categoryId, Object parameter);
	}
}

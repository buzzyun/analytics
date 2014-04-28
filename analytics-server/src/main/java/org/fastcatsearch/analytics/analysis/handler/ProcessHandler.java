package org.fastcatsearch.analytics.analysis.handler;

import org.fastcatsearch.analytics.analysis.calculator.Calculator.CategoryProcess;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 앞의 handler의 결과를 받아서 이번 작업을 수행하는 handler
 * */
public abstract class ProcessHandler {
	protected static Logger logger = LoggerFactory.getLogger(ProcessHandler.class);

	protected ProcessHandler[] nextList;
	
	/**
	 * 전달된 parameter 로 통계수행.
	 * @param parameter2 
	 */
	public abstract Object process(Object parameter) throws Exception;
	
	public void next(ProcessHandler... next){
		if(nextList == null){
			this.nextList = next;
		}else{
			int size = nextList.length;
			int newSize = size + next.length;
			ProcessHandler[] newList = new ProcessHandler[newSize];
			System.arraycopy(nextList, 0, newList, 0, size);
			System.arraycopy(next, 0, newList, size, next.length);
			nextList = newList;
		}
	}
	
	public ProcessHandler[] next(){
		return nextList;
	}
	
	public ProcessHandler appendTo(ProcessHandler prevHandler) {
		prevHandler.next(this);
		return this;
	}
	
	public ProcessHandler attachProcessTo(CategoryProcess categoryProcess) {
		categoryProcess.setProcessHandler(this);
		return this;
	}
	
}

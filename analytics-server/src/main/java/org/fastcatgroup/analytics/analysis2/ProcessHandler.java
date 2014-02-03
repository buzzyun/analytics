package org.fastcatgroup.analytics.analysis2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 앞의 handler의 결과를 받아서 이번 작업을 수행하는 handler
 * */
public abstract class ProcessHandler {
	protected static Logger logger = LoggerFactory.getLogger(ProcessHandler.class);

	protected ProcessHandler nextHandler;

	//초기화.
	protected abstract void reset();
	
	/**
	 * 다음 handler셋팅.
	 * @param handler
	 */
	public void next(ProcessHandler handler) {
		nextHandler = handler;
	}

	/**
	 * 전달된 parameters 로 통계수행.
	 * @param parameters
	 */
	public abstract void process(Object[] parameters);
	

	/**
	 *  이 handler작업끝.
	 */
	public void done() {
		callNext(doDone());
	}

	private void callNext(Object[] parameters) {
		logger.debug("## {} calls {}", getClass().getSimpleName(), nextHandler);
		if (nextHandler != null) {
			nextHandler.reset();
			nextHandler.process(parameters);
			nextHandler.done();
		}
	}

	// 마무리 작업과 다음 handler로 넘겨줄 파라미터들 리턴.
	protected abstract Object[] doDone();
}

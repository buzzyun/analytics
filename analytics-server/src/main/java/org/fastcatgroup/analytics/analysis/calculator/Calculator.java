package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.fastcatgroup.analytics.analysis.handler.CategoryLogHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.log.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 카테고리별로 통계계산을 수행하는 클래스. 어느카테고리인지는 prepareProcess 에서 설정하도록 한다. task에서 호출시 매번 새로 생성하여 사용하므로 multi-thread를 지원할 필요없음.
 * 
 * */
public abstract class Calculator<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(Calculator.class);

	protected String name;
	protected File baseDir;
	protected List<String> categoryIdList;

	// 모든 카테고리의 프로세스를 각각 담고 있다.
	private List<CategoryProcess<LogType>> categoryProcessList;
	private Stack<ProcessHandlerParameter> nextStack;

	public Calculator(String name, File baseDir, List<String> categoryIdList) {
		this.name = name;
		this.baseDir = baseDir;
		this.categoryIdList = categoryIdList;
		this.nextStack = new Stack<ProcessHandlerParameter>();
		
		this.categoryProcessList = new ArrayList<CategoryProcess<LogType>>();
		for (String categoryId : categoryIdList) {
			CategoryProcess<LogType> process = newCategoryProcess(categoryId);
			categoryProcessList.add(process);
		}
	}

	/*
	 * calculate에서 카테고리별로 수행할 CalculatorProcess들을 정의한다.
	 */
	protected abstract CategoryProcess<LogType> newCategoryProcess(String categoryId);

	/**
	 * 순차적으로 프로세스를 진행한다.
	 * */
	public final void calculate() throws Exception {
		logger.debug("## calculate > {}", this);

		for (CategoryProcess<LogType> process : categoryProcessList) {
			try {
				nextStack.clear();
				
				Object parameter = process.logHandler().done();
//				logger.debug("# calculate process > {}", process.getClass().getSimpleName());
				ProcessHandler next = process.processHandler();
				while (next != null) {

					parameter = next.process(parameter);

					ProcessHandler[] nextList = next.next();

					if (nextList == null) {
						ProcessHandlerParameter p = null;
						try {
							p = nextStack.pop();
							next = p.processHandler;
							parameter = p.parameter;
						} catch (EmptyStackException e) {
							// stack도 비어있다면 모두 끝난것이다.
							break;
						}
					} else {
						if (nextList.length > 1) {
							// 여러개이면 뒤부터 stack에 넣는다.
							for (int i = nextList.length - 1; i >= 1; i--) {
								nextStack.push(new ProcessHandlerParameter(nextList[i], parameter));
							}

						}
						next = nextList[0];
					}

				}
			} catch (Exception e) {
				logger.error("{}", e.getMessage());
			}
		}
	}

	public final void offerLog(LogType logData) throws IOException {
		logger.debug("### offerLog {} : {}", logData, categoryProcessList);
		// category별로 모두 입력해준다.
		for (CategoryProcess<LogType> process : categoryProcessList) {
			logger.debug("# calculate process offerLog > {}", process.getClass().getSimpleName());
			process.logHandler().handleLog(logData);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + name + "]";
	}

	// ProcessHandler와 파라미터를 stack에 넣기위한 클래스.
	public static class ProcessHandlerParameter {
		private ProcessHandler processHandler;
		private Object parameter;

		public ProcessHandlerParameter(ProcessHandler processHandler, Object parameter) {
			this.processHandler = processHandler;
			this.parameter = parameter;
		}

		public ProcessHandler processHandler() {
			return processHandler;
		}

		public Object parameter() {
			return parameter;
		}
	}

	public static class CategoryProcess<LogType extends LogData> {
		private ProcessHandler processHandler;
		private CategoryLogHandler<LogType> logHandler;

		public CategoryProcess() {
		}

		public void setLogHandler(CategoryLogHandler<LogType> logHandler) {
			this.logHandler = logHandler;
		}

		public void setProcessHandler(ProcessHandler processHandler) {
			this.processHandler = processHandler;
		}

		public ProcessHandler processHandler() {
			return processHandler;
		}

		public CategoryLogHandler<LogType> logHandler() {
			return logHandler;
		}
	}

}

package org.fastcatsearch.analytics.analysis.handler;

import java.io.IOException;
import java.io.PrintWriter;

import org.fastcatsearch.analytics.analysis.calculator.Calculator.CategoryProcess;
import org.fastcatsearch.analytics.analysis.log.LogData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 로그파일을 읽어들이면서 통계작업수행.
 * */
public abstract class CategoryLogHandler<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(CategoryLogHandler.class);

	protected String categoryId;
	private PrintWriter explainLogWriter;
	
	public CategoryLogHandler(String categoryId) {
		this.categoryId = categoryId;
	}

	public String categoryId() {
		return categoryId;
	}

	public void setExplainLogWriter(PrintWriter explainLogWriter){
		this.explainLogWriter = explainLogWriter;
	}
	protected void explainLog(Object... str) {
		if(explainLogWriter != null) {
			for(Object e : str) {
				explainLogWriter.print(e);
			}
			explainLogWriter.println();
		}
	}
	/**
	 * 로그가 한줄단위 tab구분된 LogData로 이 메소드로 전달된다.
	 * */
	public abstract void handleLog(LogType logData) throws IOException;

	/**
	 * 마무리작업 및 카테고리 set를 전달한다.
	 * */
	public abstract Object done() throws IOException;

	public CategoryLogHandler<LogType> attachLogHandlerTo(CategoryProcess<LogType> categoryProcess) {
		categoryProcess.setLogHandler(this);
		return this;
	}
}

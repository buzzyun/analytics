package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.BufferedLogger;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.LogHandler;

/**
 * search log를 읽어들여 카테고리별로 분류하여 재 저장한다.
 * */
public class CategorySearchLogHandler extends LogHandler<SearchLog> {

	private File baseDir;

	private BufferedLogger rootLogger;
	private Map<String, BufferedLogger> categoryLoggerMap;

	public CategorySearchLogHandler(File baseDir) {
		this.baseDir = baseDir;
		categoryLoggerMap = new HashMap<String, BufferedLogger>();
		rootLogger = newBufferedLogger("_root");
	}

	@Override
	public void handleLog(SearchLog logData) {
		String categoryId = logData.categoryId();
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if (categoryId != null && categoryId.length() > 0) {
				BufferedLogger categoryLogger = categoryLoggerMap.get(categoryId);
				if (categoryLogger == null) {
					categoryLogger = newBufferedLogger(categoryId);
					categoryLoggerMap.put(categoryId, categoryLogger);
				}

				categoryLogger.log(logData.keyword(), logData.previousKeyword());
			}
			// root logger에는 무조건 기록.
			rootLogger.log(logData.keyword(), logData.previousKeyword());
		}
	}

	private BufferedLogger newBufferedLogger(String categoryId) {
		File dir = new File(baseDir, categoryId);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(dir, "tmp.log");
		return new BufferedLogger(f);
	}

	@Override
	public void reset() {
		categoryLoggerMap.clear();
	}

	@Override
	public Object done() {
		rootLogger.close();
		for (Entry<String, BufferedLogger> entry : categoryLoggerMap.entrySet()) {
			entry.getValue().close();
		}

		Set<String> keySet = categoryLoggerMap.keySet();
		Set<String> categoryIdSet = new HashSet<String>(keySet);
		categoryIdSet.add("_root");
		return new Object[] { baseDir, categoryIdSet };
	}

	@Override
	public Object process(Object parameter) {
		// no dot use
		return null;
	}
}

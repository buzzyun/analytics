package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;

/**
 * search log를 읽어들여 카테고리별로 분류하여 재 저장한다.
 * */
public class CategorySearchLogHandler extends CategoryLogHandler<SearchLog> {

	private final static String rootLoggerId = "_root";
	private File baseDir;
	String fileName;
	
	private BufferedWriter rootLogger;
	private Map<String, BufferedWriter> categoryWriterMap;

	public CategorySearchLogHandler(String categoryId, File baseDir, String fileName) {
		super(categoryId);
		this.baseDir = baseDir;
		this.fileName = fileName;
		categoryWriterMap = new HashMap<String, BufferedWriter>();
	}

	@Override
	public void handleLog(SearchLog logData) {
		String categoryId = logData.categoryId();
		String keyword = logData.keyword();
		try {
			if (keyword != null && keyword.length() > 0) {
				if (categoryId != null && categoryId.length() > 0) {
					BufferedWriter categoryWriter = categoryWriterMap.get(categoryId);
					if (categoryWriter == null) {
						categoryWriter = newBuffererWriter(categoryId);
						categoryWriterMap.put(categoryId, categoryWriter);
					}

					categoryWriter.append(logData.keyword());
					categoryWriter.append("\t");
					categoryWriter.append(logData.previousKeyword());
					categoryWriter.append("\n");
				}
				// root logger에는 무조건 기록.
				if (rootLogger == null) {
					rootLogger = newBuffererWriter(rootLoggerId);
				}
				rootLogger.append(logData.keyword());
				rootLogger.append("\t");
				rootLogger.append(logData.previousKeyword());
				rootLogger.append("\n");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private BufferedWriter newBuffererWriter(String categoryId) {
		File dir = new File(baseDir, categoryId);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(dir, fileName);
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
		} catch (FileNotFoundException e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public void reset() {
		categoryWriterMap.clear();
		rootLogger = null;
	}

	@Override
	public Set<String> done() {

		for (Entry<String, BufferedWriter> entry : categoryWriterMap.entrySet()) {
			try {
				entry.getValue().close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}

		Set<String> keySet = categoryWriterMap.keySet();
		Set<String> categoryIdSet = new HashSet<String>(keySet);
		if (rootLogger != null) {
			try {
				rootLogger.close();
			} catch (IOException e) {
				logger.error("", e);
			}
			categoryIdSet.add(rootLoggerId);
		}
		return categoryIdSet;
	}

}

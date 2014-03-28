package org.fastcatgroup.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class CategoryKeyLogReader extends FileListLogReader<SearchLog> {

	public CategoryKeyLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected SearchLog makeLog(String[] el) {
//		logger.debug("log>>> {}, {}", el.length, el);
		return new SearchLog(categoryId(), el[0], el[1]);
	}
}

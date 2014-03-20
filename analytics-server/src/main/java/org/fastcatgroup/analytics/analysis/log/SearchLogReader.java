package org.fastcatgroup.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class SearchLogReader extends FileListLogReader<SearchLog> {

	public SearchLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected SearchLog makeLog(String[] el) {
//		logger.debug("log>>> {}, {}", el.length, el);
		return new SearchLog(el[0], el[1]);
	}

}

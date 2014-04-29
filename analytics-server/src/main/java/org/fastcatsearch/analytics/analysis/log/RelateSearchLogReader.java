package org.fastcatsearch.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class RelateSearchLogReader extends FileListLogReader<RelateSearchLog> {

	public RelateSearchLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected RelateSearchLog makeLog(String[] el) {
		return new RelateSearchLog(el[1], el[2], el.length > 3 ? el[3] : "");
	}
}

package org.fastcatsearch.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class TypeSearchLogReader extends FileListLogReader<TypeSearchLog> {

	public TypeSearchLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected TypeSearchLog makeLog(String[] el) {
		logger.trace("typelog>>> {}, {}", el.length, el);
		return new TypeSearchLog(el, 1);
	}
}

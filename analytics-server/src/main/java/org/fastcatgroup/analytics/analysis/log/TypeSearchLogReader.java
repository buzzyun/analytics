package org.fastcatgroup.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class TypeSearchLogReader extends FileLogReader<TypeSearchLog> {

	public TypeSearchLogReader(File file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected TypeSearchLog makeLog(String[] el) {
		logger.debug("log>>> {}, {}", el.length, el);
		return new TypeSearchLog(el);
	}

}

package org.fastcatsearch.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class KeyCountLogReader extends FileListLogReader<KeyCountLog> {

	public KeyCountLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected KeyCountLog makeLog(String[] el) {
		return new KeyCountLog(el[0], el[1]);
	}
}

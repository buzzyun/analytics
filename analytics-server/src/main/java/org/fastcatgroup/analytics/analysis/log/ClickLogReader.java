package org.fastcatgroup.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class ClickLogReader extends FileListLogReader<ClickLog> {

	public ClickLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected ClickLog makeLog(String[] el) {
//		logger.debug("log>>> {}, {}", el.length, el);
		return new ClickLog(el[0], el[1], el[2], el[3]);
	}
}

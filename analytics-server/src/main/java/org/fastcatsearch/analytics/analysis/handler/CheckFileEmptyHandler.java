package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;

import org.fastcatsearch.analytics.analysis.ProcessDropException;

public class CheckFileEmptyHandler extends ProcessHandler {

	private File file;

	public CheckFileEmptyHandler(File file) {
		this.file = file;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		if (file.exists() && file.length() > 0) {
			return null;
		} else {
			throw new ProcessDropException("Drop process due to file is empty. file=" + file.getAbsolutePath());
		}
	}

}

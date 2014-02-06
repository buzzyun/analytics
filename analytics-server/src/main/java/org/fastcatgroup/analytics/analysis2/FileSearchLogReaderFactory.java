package org.fastcatgroup.analytics.analysis2;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.log.SearchLog;

public class FileSearchLogReaderFactory implements SourceLogReaderFactory<SearchLog> {

	private File file;
	private String encoding;

	public FileSearchLogReaderFactory(File file, String encoding) {
		this.file = file;
		this.encoding = encoding;
	}

	@Override
	public SourceLogReader<SearchLog> createReader() {
		try {
			return new FileSearchLogReader(file, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}
}


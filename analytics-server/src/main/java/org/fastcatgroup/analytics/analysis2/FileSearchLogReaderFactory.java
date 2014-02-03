package org.fastcatgroup.analytics.analysis2;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.util.DirBufferedReader;

public class FileSearchLogReaderFactory implements SourceLogReaderFactory<SearchLog> {

	private File[] files;
	private String encoding;

	public FileSearchLogReaderFactory(File[] files, String encoding) {
		this.files = files;
		this.encoding = encoding;
	}

	@Override
	public SourceLogReader<SearchLog> createReader() {
		// TODO 파일선택 규칙을 구현한다.
		try {
			return new FileSearchLogReader(files, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}
}

class FileSearchLogReader implements SourceLogReader<SearchLog> {

	private File[] files;
	private DirBufferedReader reader;

	public FileSearchLogReader(File[] files, String encoding) throws IOException {
		this.files = files;
		reader = new DirBufferedReader(files, encoding);
	}

	@Override
	public SearchLog readLog() {
		try {
			String line = reader.readLine();
			if (line == null) {
				return null;
			} else {
				return new SearchLog(line.split("\t"));
			}
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}

	@Override
	public void close() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException ignore) {
		}
	}

}

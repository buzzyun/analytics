package org.fastcatgroup.analytics.analysis.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class FileLogReader<LogType extends LogData> implements SourceLogReader<LogType> {

	private File file;
	private BufferedReader reader;

	public FileLogReader(File file, String encoding) throws IOException {
		this.file = file;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
	}

	@Override
	public LogType readLog() {
		try {

			String line = null;
			do {
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				//길이가 0이면 재시도.
			} while (line.trim().length() == 0);

			String[] el = line.split("\t");
			return makeLog(el);
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}
	}

	protected abstract LogType makeLog(String[] el);

	@Override
	public void close() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException ignore) {
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " / " + file.getAbsolutePath();
	}
}

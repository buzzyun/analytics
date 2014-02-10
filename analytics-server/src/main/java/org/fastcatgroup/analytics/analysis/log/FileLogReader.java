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
			String line = reader.readLine();
			if (line == null) {
				return null;
			} else {
				String[] el = line.split("\t");
				return makeLog(el);
//				return new SearchLog(el[0], el[1], el.length >= 3 ? el[2] : "");
			}
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
	public String toString(){
		return getClass().getSimpleName() + " / " + file.getAbsolutePath();
	}
}

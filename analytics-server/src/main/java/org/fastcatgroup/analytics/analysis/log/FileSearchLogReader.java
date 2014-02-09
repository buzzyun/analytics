package org.fastcatgroup.analytics.analysis.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FileSearchLogReader implements SourceLogReader<SearchLog> {
	
	private File file;
	private BufferedReader reader;

	public FileSearchLogReader(File file, String encoding) throws IOException {
		this.file = file;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
	}

	@Override
	public SearchLog readLog() {
		try {
			String line = reader.readLine();
			if (line == null) {
				return null;
			} else {
				String[] el = line.split("\t");
				
				return new SearchLog(el[0], el[1], el.length >= 3 ? el[2] : "");
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
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + " / " + file.getAbsolutePath();
	}
}

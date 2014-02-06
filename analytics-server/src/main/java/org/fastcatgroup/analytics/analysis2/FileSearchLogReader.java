package org.fastcatgroup.analytics.analysis2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fastcatgroup.analytics.analysis.log.SearchLog;

public class FileSearchLogReader implements SourceLogReader<SearchLog> {
	
	private BufferedReader reader;

	public FileSearchLogReader(File file, String encoding) throws IOException {
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
}

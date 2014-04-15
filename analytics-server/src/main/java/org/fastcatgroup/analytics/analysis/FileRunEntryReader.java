package org.fastcatgroup.analytics.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.fastcatgroup.analytics.analysis.util.RunEntry;
import org.fastcatgroup.analytics.analysis.util.RunEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRunEntryReader<EntryType extends RunEntry> extends RunEntryReader<EntryType> {
	protected static Logger logger = LoggerFactory.getLogger(FileRunEntryReader.class);
	protected BufferedReader reader;

	protected EntryType entry;

	protected EntryParser<EntryType> entryParser;
	
	public FileRunEntryReader(InputStream is, String encoding, EntryParser<EntryType> entryParser) throws IOException {
		reader = new BufferedReader(new InputStreamReader(is, encoding));
		this.entryParser = entryParser;
	}

	public FileRunEntryReader(File file, String encoding, EntryParser<EntryType> entryParser) throws IOException {
		if (file != null && file.exists()) {
			logger.debug("open file {}", file.getAbsolutePath());
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			this.entryParser = entryParser;
		}
	}

	@Override
	public boolean next() {
		String line = null;
		try {
			while (reader != null && (line = reader.readLine()) != null) {
				entry = entryParser.parse(line); //exception발생시 종료.
				
//				logger.debug("##FileRunEntryReader parse {} , {}", line, entry);
				if(entry == null){
					// 파싱실패시 다음 라인확인.
					continue;
				}
				
				return true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		
		entry = null;
		return false;
	}

	@Override
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

	@Override
	public EntryType entry() {
		return entry;
	}

}

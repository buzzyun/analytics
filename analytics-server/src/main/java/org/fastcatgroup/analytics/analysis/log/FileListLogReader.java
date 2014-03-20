package org.fastcatgroup.analytics.analysis.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class FileListLogReader<LogType extends LogData> implements SourceLogReader<LogType> {

	private File[] files;
	private int currentInx;
	private BufferedReader reader;
	private String encoding;

	public FileListLogReader(File[] files, String encoding) throws IOException {
		this.files = files;
		this.encoding = encoding;
		if(files.length > 0) {
			reader = openStream(0);
		}
	}
	
	private BufferedReader openStream(int inx) throws IOException {
		try {
			//open new stream from file list
			if(files.length > inx) {
				return new BufferedReader(new InputStreamReader(new FileInputStream(files[inx]), encoding));
			}
		} catch (IOException e) {
			logger.error("file open error {}", files[inx],e);
			throw e;
		} finally {
		}
		return null;
	}

	@Override
	public LogType readLog() {
		try {
			
			//loop for file list
			for (; currentInx < this.files.length;) {
				
				if (reader == null) {
					reader = openStream(currentInx);
					currentInx++;
				}
				
				//read one available line
				for (String rline = null; (rline = reader.readLine()) != null;) {
					if(rline.trim().length() == 0) {
						continue;
					}
					
					String[] el = rline.split("\t");
					
					//
					// return log...
					//
					return makeLog(el);
				}//for
				
				//end of current-file
				
				if(reader!=null) {
					reader.close();
					reader = null;
				}
				//loop for read another file
			}//for

		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
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
		return getClass().getSimpleName() + " / " + files;
	}
}

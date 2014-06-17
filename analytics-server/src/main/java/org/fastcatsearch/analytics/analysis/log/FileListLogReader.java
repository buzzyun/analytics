package org.fastcatsearch.analytics.analysis.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileListLogReader<LogType extends LogData> implements SourceLogReader<LogType> {

	protected static final Logger logger = LoggerFactory.getLogger(FileListLogReader.class);

	private File[] files;
	private int currentInx;
	private BufferedReader reader;
	private String encoding;

	public FileListLogReader(File[] files, String encoding) throws IOException {
		this.files = files;
		this.encoding = encoding;
	}

	private BufferedReader openStream(int inx) throws IOException {
		try {
			// open new stream from file list
			if (files.length > inx) {
				logger.trace("read file {} [{}/{}]", files[inx], inx, files.length);
				if (files[inx].exists()) {
					// parseFileInfo();
					BufferedReader bufferedReader = null;
					try {
						bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(files[inx]), encoding));
					} catch (Exception ex) {
						if (bufferedReader != null) try {
							bufferedReader.close();
							bufferedReader = null;
						} catch (Exception ignore) { }
					}
					return bufferedReader;
				}
				logger.error("file not found {}", files[inx]);
			}
		} catch (Exception e) {
			logger.error("file open error {}", files[inx], e);
			throw new IOException(e);
		} finally {
		}
		return null;
	}

	public int currentIndex() {
		if (currentInx > 0) {
			return currentInx - 1;
		}
		return 0;
	}

	@Override
	public LogType readLog() {
		try {

			// loop for file list
			for (; reader != null || currentInx < this.files.length;) {

				if (reader == null) {
					if (files[currentInx] != null && files[currentInx].exists()) {
						logger.debug("file[{}]:{} exists", currentInx, files[currentInx]);
						reader = openStream(currentInx);
						currentInx++;
						if (reader == null) {
							continue;
						}
					} else {
						logger.debug("file[{}/{}]:{} not exists. continue", currentInx, files.length, files[currentInx]);
						currentInx++;
						continue;
					}
				}

				// read one available line
				for (String rline = null; (rline = reader.readLine()) != null;) {
					if (rline.trim().length() == 0) {
						continue;
					}

					String[] el = rline.split("\t");

					//
					// return log...
					//
					logger.trace("makeLog : {} : {}", this, rline);
					return makeLog(el);
				}// for

				// end of current-file

				if (reader != null) {
					if (currentInx <= files.length) {
						logger.debug("close reader..{} [{}/{}]", files[currentInx - 1], currentInx - 1, files.length);
					}
					reader.close();
				}
				reader = null;
				// loop for read another file
			}// for

		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				File file = null;
				if (currentInx > 0 && currentInx <= files.length) {
					file = files[currentInx - 1];
				}
				logger.error("file : {}", file, e);
			}
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

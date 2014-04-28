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
	private String keyFileName;
	private String siteId;
	private String categoryId;
	private String timeId;
	
	public FileListLogReader(File[] files, String encoding) throws IOException {
		this.files = files;
		this.encoding = encoding;
		this.keyFileName = "";
		this.siteId = "";
		this.categoryId = "";
		this.timeId = "";
				
	}
	
	private BufferedReader openStream(int inx) throws IOException {
		try {
			//open new stream from file list
			if(files.length > inx) {
				logger.trace("read file {} [{}/{}]", files[inx], inx, files.length);
				if(files[inx].exists()) {
					parseFileInfo();
					return new BufferedReader(new InputStreamReader(new FileInputStream(files[inx]), encoding));
				}
				logger.error("file not found {}", files[inx]);
			}
		} catch (IOException e) {
			logger.error("file open error {}", files[inx],e);
			throw e;
		} finally {
		}
		return null;
	}
	
	public int currentIndex () {
		if(currentInx > 0) {
			return currentInx - 1;
		}
		return 0;
	}

	@Override
	public LogType readLog() {
		try {
			
			//loop for file list
			for (; reader != null || currentInx < this.files.length;) {
				
				if (reader == null) {
					if(files[currentInx] != null && files[currentInx].exists()) {
						logger.debug("file[{}]:{} exists", currentInx, files[currentInx]);
						reader = openStream(currentInx);
						currentInx++;
						if(reader == null) {
							continue;
						}
					} else {
						logger.debug("file[{}/{}]:{} not exists. continue", currentInx, files.length, files[currentInx]);
						currentInx++;
						continue;
					}
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
					logger.trace("makeLog : {} : {}", this, rline);
					return makeLog(el);
				}//for
				
				//end of current-file
				
				if(reader!=null) {
					if(currentInx <= files.length) {
						logger.debug("close reader..{} [{}/{}]", files[currentInx - 1], currentInx - 1, files.length);
					}
					reader.close();
				}
				reader = null;
				//loop for read another file
			}//for

		} catch (IOException e) {
			if(logger.isDebugEnabled()) {
				File file = null;
				if(currentInx <= files.length) {
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
	
	public String keyFileName() {
		return keyFileName;
	}

	public String siteId() {
		return siteId;
	}
	
	public String categoryId() {
		return categoryId;
	}

	public String timeId() {
		return timeId;
	}
	
	private void parseFileInfo() {
		
		keyFileName = siteId = categoryId = timeId = "";
		
		if(currentInx > 0) {
			String[] fileName = files[currentInx - 1].getAbsolutePath().split(File.separator);
			int length = fileName.length;
			if(length > 3) {
				keyFileName = fileName[length - 1];
				
				if("data".equals(fileName[length - 3])) {
					siteId = fileName[length - 2];
					categoryId = "";
				} else if("data".equals(fileName[length - 4])) {
					siteId = fileName[length - 3];
					categoryId = fileName[length - 2];
					length--;
				}
				
				String timeCode = fileName[length - 4];
				char c1 = timeCode.charAt(0);
				if(c1 == 'D') {
					String month = fileName[length - 5];
					String year = fileName[length - 6];
					timeId = "D" + year.substring(1) + month.substring(1)
							+ timeCode.substring(1);
				} else if(c1 == 'W') {
					String year = fileName[length - 5];
					timeId = "W" + year.substring(1) + timeCode.substring(1);
				} else if(c1 == 'M') {
					String year = fileName[length - 5];
					timeId = "M" + year.substring(1) + timeCode.substring(1);
				} else if(c1 == 'Y') {
					timeId = timeCode;
				}
			}
		}
		logger.debug("key-name:{} / siteId:{} / categoryId:{} / timeId:{}", keyFileName, siteId, categoryId, timeId);
	}
}

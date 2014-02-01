package org.fastcatgroup.analytics.analysis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 분 단위의 짧은 로그 데이터를 기록하는 로거.
 * */
public class RollingRawLogger {
	private static final Logger logger = LoggerFactory.getLogger(RollingRawLogger.class);

	private BufferedLogger aLogger;
	private File baseDir;
	private String prefix;
	private int rollingLimit;
	private int sequence;

	public RollingRawLogger(File baseDir, String prefix, int rollingLimit) {
		this.baseDir = baseDir;
		this.prefix = prefix;
		this.rollingLimit = rollingLimit;
		this.sequence = readSequence();
		this.aLogger = new BufferedLogger(getTempFile(sequence), true);
	}

	private int readSequence() {

		File sequenceFile = getSequenceFile();
		if (sequenceFile.exists()) {
			DataInputStream is = null;
			int s = 0;
			try {
				is = new DataInputStream(new FileInputStream(getSequenceFile()));
				s = is.readInt();
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ignore) {
					}
				}
			}
			return s;
		} else {
			writeSequence();
			return sequence;
		}

	}

	private void writeSequence() {
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(getSequenceFile()));
			os.writeInt(sequence);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	// 0번째 요소는 카테고리Id 이다.
	public void log(String[] data) {
		aLogger.log(data);
	}

	private File rollingRawLogger() {
		
		BufferedLogger prevLogger = aLogger;
		
		//먼저 새 로거를 만들어서 셋팅한다.
		int newSequence = (sequence + 1) % 2;
		File f = getTempFile(sequence);
		aLogger = new BufferedLogger(f);
		sequence = newSequence;
		writeSequence();
		
		if (prevLogger != null) {
			prevLogger.close();
			return prevLogger.getFile();
		}

		return null;
	}

	private File getSequenceFile() {
		return new File(baseDir, ".sequence");
	}

	private File getTempFile(int index) {
		return new File(baseDir, prefix + ".tmp." + index);
	}

	private File getFile(int index) {
		return new File(baseDir, prefix + "." + index + ".log");
	}

	public void rolling() {

		File prevFile = rollingRawLogger();

		for (int i = rollingLimit - 2; i >= 0; i--) {
			File srcFile = getFile(i);
			if (srcFile.exists()) {
				File destFile = getFile(i + 1);
				if(destFile.exists()){
					destFile.delete();
				}
				try {
					FileUtils.moveFile(srcFile, destFile);
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}

		try {
			FileUtils.moveFile(prevFile, getFile(0));
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public void close() {

		if (aLogger != null) {
			aLogger.close();
		}
	}

}

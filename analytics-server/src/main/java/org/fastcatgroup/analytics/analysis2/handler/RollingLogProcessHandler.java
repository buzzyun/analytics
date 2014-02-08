package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * 0.log, 1.log ... 를 1.log, 2.log.. 로 롤링한다.
 * */
public class RollingLogProcessHandler extends ProcessHandler {

	private File baseDir;
	int fileLimitCount;

	/**
	 * fileLimitCount : 0.log 1.log .. 의 갯수.
	 * */
	public RollingLogProcessHandler(File baseDir, int fileLimitCount) {
		this.baseDir = baseDir;
		this.fileLimitCount = fileLimitCount;
	}

	@Override
	public Object process(Object parameter) {

		File lastFile = getLogFile(baseDir, fileLimitCount - 1);
		if (lastFile.exists()) {
			lastFile.delete();
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignore) {
			}
		}

		for (int i = fileLimitCount - 1; i >= 0; i--) {
			File file = getLogFile(baseDir, i);

			if (file.exists()) {
				File destFile = getLogFile(baseDir, i + 1);
				try {
					FileUtils.moveFile(file, destFile);
				} catch (IOException e) {
					logger.error("", e);
				}
//				logger.debug("[{}] rename {} > {}", categoryDir.getName(), file.getName(), destFile.getName());
			}
		}

		return parameter;
	}


	private File getLogFile(File dir, int number) {
		return new File(dir, number + ".log");
	}
}

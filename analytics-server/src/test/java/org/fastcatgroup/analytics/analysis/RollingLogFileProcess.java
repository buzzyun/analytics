package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;

/**
 * 기존의 0.log, 1.log ... 를 하나씩 뒤로 이동시킨다.
 * 차후 0.log가 생성될 공간을 마련한다.
 * */
public class RollingLogFileProcess extends ProcessHandler {
	private File baseDir;
	private int maxCount;

	public RollingLogFileProcess(File baseDir, int maxCount) {
		this.baseDir = baseDir;
		this.maxCount = maxCount;
	}

	@Override
	public void reset() {
	}

	@Override
	public void process(Object[] parameters) {
		File lastFile = getLogFile(maxCount - 1);
		if (lastFile.exists()) {
			lastFile.delete();
		}

		for (int i = maxCount - 1; i >= 0; i--) {
			File file = getLogFile(i);

			if (file.exists()) {
				File destFile = getLogFile(i + 1);
				try {
					FileUtils.moveFile(file, destFile);
				} catch (IOException e) {
					logger.error("", e);
				}
				logger.debug("rename {} > {}", file.getName(), destFile.getName());
			}
		}
	}

	private File getLogFile(int number) {
		return new File(baseDir, number + ".log");
	}

	@Override
	protected Object[] doDone() {
		return null;
	}

}

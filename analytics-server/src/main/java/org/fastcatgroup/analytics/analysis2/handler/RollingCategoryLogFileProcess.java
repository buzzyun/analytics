package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * 기존의 0.log, 1.log ... 를 하나씩 뒤로 이동시킨다. 차후 0.log가 생성될 공간을 마련한다.
 * */
public class RollingCategoryLogFileProcess extends ProcessHandler {
	private File baseDir;
	private int maxCount;

	public RollingCategoryLogFileProcess(File baseDir, int maxCount) {
		this.baseDir = baseDir;
		this.maxCount = maxCount;
	}

	@Override
	public void reset() {
	}

	@Override
	public Object process(Object parameter) {

		File[] directoryList = baseDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// 디렉토리만 사용한다.
				return pathname.isDirectory();
			}

		});

		for (File categoryDir : directoryList) {

			File lastFile = getLogFile(categoryDir, maxCount - 1);
			if (lastFile.exists()) {
				lastFile.delete();
			}

			for (int i = maxCount - 1; i >= 0; i--) {
				File file = getLogFile(categoryDir, i);

				if (file.exists()) {
					File destFile = getLogFile(categoryDir, i + 1);
					try {
						FileUtils.moveFile(file, destFile);
					} catch (IOException e) {
						logger.error("", e);
					}
					logger.debug("[{}] rename {} > {}", categoryDir.getName(), file.getName(), destFile.getName());
				}
			}

		}
		
		return baseDir;
	}

	private File getLogFile(File dir, int number) {
		return new File(dir, number + ".log");
	}


}

package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.util.DumpFile;

public class MoveFileHandler extends ProcessHandler {

	private File baseDir;
	String srcFileName;
	String destFileName;

	public MoveFileHandler(File baseDir, String srcFileName, String destFileName) {
		this.baseDir = baseDir;
		this.srcFileName = srcFileName;
		this.destFileName = destFileName;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		File srcFile = new File(baseDir, srcFileName);
		File destFile = new File(baseDir, destFileName);
		if (srcFile.exists()) {
			if (destFile.exists()) {
				destFile.delete();
//				Thread.sleep(100);
			}
//			logger.debug("Move file {} > {}({}) > {}({})", baseDir.getName(), srcFile.getName(), srcFile.length() ,destFile.getName(), destFile.length());
//			DumpFile.dump(srcFile);
			FileUtils.moveFile(srcFile, destFile);
//			DumpFile.dump(destFile);
		}

		return null;
	}

}

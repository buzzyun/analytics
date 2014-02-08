package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;

public class RealtimePopularKeywordResultHandler extends ProcessHandler {

	File baseDir;
	String encoding;

	public RealtimePopularKeywordResultHandler(File baseDir, String encoding) {
		this.baseDir = baseDir;
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		File targetFile = new File(baseDir, "rt-popular.txt");

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile)));

		List<RankKeyword> result = (List<RankKeyword>) parameter;
		try {
			for (RankKeyword k : result) {
				writer.append(k.getKeyword());
				writer.append("\t");
				writer.append(Integer.toString(k.getRankDiff()));
				writer.append("\t");
				writer.append(k.getRankDiffType().name());
				writer.append("\t");
				writer.append(Integer.toString(k.getCount()));
				writer.append("\n");
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		
		return result;
	}

}

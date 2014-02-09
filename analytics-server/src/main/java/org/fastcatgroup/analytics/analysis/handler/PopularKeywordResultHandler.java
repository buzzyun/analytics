package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;

public class PopularKeywordResultHandler extends ProcessHandler {

	File popularKeywordLogFile;
	String encoding;

	public PopularKeywordResultHandler(File popularKeywordLogFile, String encoding) {
		this.popularKeywordLogFile = popularKeywordLogFile;
	}

	@Override
	public Object process(Object parameter) throws Exception {

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(popularKeywordLogFile)));

		List<RankKeyword> result = (List<RankKeyword>) parameter;
		logger.debug("##Popular Keyword > {}", result);
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

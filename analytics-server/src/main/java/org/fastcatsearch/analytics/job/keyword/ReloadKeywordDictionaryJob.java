package org.fastcatsearch.analytics.job.keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.io.DataInput;
import org.fastcatsearch.analytics.io.DataOutput;
import org.fastcatsearch.analytics.io.Streamable;
import org.fastcatsearch.analytics.job.Job;
import org.fastcatsearch.analytics.keyword.KeywordService;
import org.fastcatsearch.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatsearch.analytics.service.ServiceManager;

public class ReloadKeywordDictionaryJob extends Job implements Streamable {

	private static final long serialVersionUID = 4992122572477600971L;

	@Override
	public void readFrom(DataInput input) throws IOException {
		int size = input.readVInt();
		Object[] args = new Object[size];
		int listSize = input.readVInt();
		List<String> list = new ArrayList<String>(listSize);
		for (int i = 0; i < listSize; i++) {
			list.add(input.readString());
		}
		args[0] = list;
		args[1] = KeywordDictionaryType.valueOf(input.readString());

		if (size > 2) {
			args[2] = input.readInt();
		}

		this.args = args;
	}

	@Override
	public void writeTo(DataOutput output) throws IOException {
		Object[] args = (Object[]) getArgs();
		int size = args.length;
		output.writeVInt(size);

		List<String> categoryIdList = (List<String>) args[0];
		output.writeVInt(categoryIdList.size());
		for (String categoryId : categoryIdList) {
			output.writeString(categoryId);
		}

		KeywordDictionaryType dictionaryType = (KeywordDictionaryType) args[1];
		output.writeString(dictionaryType.name());

		if (size > 2) {
			output.writeInt((Integer) args[2]);
		}
	}

	@Override
	public JobResult doRun() throws AnalyticsException {
		Object[] args = (Object[]) getArgs();
		List<String> categoryIdList = (List<String>) args[0];
		KeywordDictionaryType dictionaryType = (KeywordDictionaryType) args[1];
		int interval = 1;
		if (args.length > 2) {
			interval = (Integer) args[2];
		}

		KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);
		for (String categoryId : categoryIdList) {
			try {
				keywordService.loadPopularKeywordDictionary(categoryId, dictionaryType, interval);
			} catch (IOException e) {
				logger.error("error reload keyword dictionary >> " + categoryId + ", " + dictionaryType + ", " + dictionaryType, e);
			}
		}

		return new JobResult(true);
	}

}

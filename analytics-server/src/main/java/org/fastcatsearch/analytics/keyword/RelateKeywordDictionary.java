package org.fastcatsearch.analytics.keyword;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fastcatsearch.analytics.io.DataInput;
import org.fastcatsearch.analytics.io.DataOutput;
import org.fastcatsearch.analytics.io.InputStreamDataInput;
import org.fastcatsearch.analytics.io.OutputStreamDataOutput;

public class RelateKeywordDictionary implements KeywordDictionary {

	public static final String fileName = "relate.dict";

	private Map<String, String> keywordMap;

	public RelateKeywordDictionary() {
		keywordMap = new HashMap<String, String>(0);
	}

	public RelateKeywordDictionary(File dictionaryFile) throws IOException {
		if (!dictionaryFile.exists()) {
			keywordMap = new HashMap<String, String>();
			logger.error("키워드사전파일이 존재하지 않습니다. file={}", dictionaryFile.getAbsolutePath());
			throw new IOException("dictionary file not found: " + dictionaryFile.getAbsolutePath());
		}
		InputStream istream = null;
		try {
			istream = new FileInputStream(dictionaryFile);
			readFrom(istream);
			istream.close();
		} finally {
			if (istream != null) try {
				istream.close();
			} catch (IOException e) { }
		}
		
	}

	public String getRelateKeyword(String keyword) {
		return keywordMap.get(keyword.toUpperCase());
	}

	public void putRelateKeyword(String keyword, String value) {
		keywordMap.put(keyword.toUpperCase(), value);
	}

	@Override
	public void readFrom(InputStream in) throws IOException {
		DataInput input = new InputStreamDataInput(in);

		keywordMap = new HashMap<String, String>();

		int size = input.readVInt();
		for (int entryInx = 0; entryInx < size; entryInx++) {
			String keyword = input.readString();
			String value = input.readString();

			keywordMap.put(keyword, value);
		}

	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		DataOutput output = new OutputStreamDataOutput(out);
		int size = keywordMap.size();
		output.writeVInt(size);
		
		if(size > 0){
			for(Entry<String, String> entry : keywordMap.entrySet()){
				output.writeString(entry.getKey());
				output.writeString(entry.getValue());
			}
		}
	}

}

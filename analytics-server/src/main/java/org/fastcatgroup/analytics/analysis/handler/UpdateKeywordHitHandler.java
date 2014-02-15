package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

public class UpdateKeywordHitHandler extends ProcessHandler {
	File rankLogFile;
	int topCount;
	String encoding;
	EntryParser<KeyCountRunEntry> entryParser;
	
	public UpdateKeywordHitHandler(File rankLogFile, int topCount, String encoding, EntryParser<KeyCountRunEntry> entryParser){
		this.rankLogFile = rankLogFile;
		this.topCount = topCount;
		this.encoding = encoding;
		this.entryParser = entryParser;
	}
	
	@Override
	public Object process(Object parameter) throws Exception {
		
		
		
		return null;
	}

}

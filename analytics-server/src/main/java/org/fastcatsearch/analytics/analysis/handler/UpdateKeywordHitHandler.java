package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.util.Calendar;

import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.FileRunEntryReader;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdateKeywordHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	String timeId;
	
	File rankLogFile;
	int topCount;
	String encoding;
	EntryParser<KeyCountRunEntry> entryParser;
	
	public UpdateKeywordHitHandler(String siteId, String categoryId, String timeId, File rankLogFile, int topCount, String encoding, EntryParser<KeyCountRunEntry> entryParser){
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
		this.rankLogFile = rankLogFile;
		this.topCount = topCount;
		this.encoding = encoding;
		this.entryParser = entryParser;
	}
	
	@Override
	public Object process(Object parameter) throws Exception {
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchKeywordHitMapper> mapperSession = dbService.getMapperSession(SearchKeywordHitMapper.class);
		try {
			SearchKeywordHitMapper mapper = mapperSession.getMapper();
			int count = mapper.getCount(siteId, categoryId, timeId);
			if(count > 0){
				mapper.updateClear(siteId, categoryId, timeId);
			}
			
			// 1. target 파일에서 top N개를 뽑아낸다.
			FileRunEntryReader<KeyCountRunEntry> targetReader = new FileRunEntryReader<KeyCountRunEntry>(rankLogFile, encoding, entryParser);
			try {
				int i = 0;
				while (targetReader.next()) {
					if (i > topCount) {
						break;
					}
					
					KeyCountRunEntry entry = targetReader.entry();
					logger.trace("#### UpdateKeywordHit {} >> {} > {}", timeId, categoryId, entry);
					
					mapper.putEntry(siteId, categoryId, timeId, entry.getKey(), entry.getCount());
					i++;
				}
			} finally {
				if (targetReader != null) {
					targetReader.close();
				}
			}
			
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		return null;
	}

}

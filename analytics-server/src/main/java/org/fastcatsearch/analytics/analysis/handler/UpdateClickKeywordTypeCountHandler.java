package org.fastcatsearch.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.ClickKeywordHitMapper;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdateClickKeywordTypeCountHandler extends ProcessHandler {
	String siteId;
	String timeId;
	File file;
	String encoding;
	
	public UpdateClickKeywordTypeCountHandler(String siteId, String timeId, File file, String encoding) {
		this.siteId = siteId;
		this.timeId = timeId;
		this.file = file;
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		
		BufferedReader br = null;
		
		/*
		 * 1. 검색횟수기록.
		 */
		MapperSession<ClickKeywordHitMapper> mapperSession = dbService.getMapperSession(ClickKeywordHitMapper.class);
		try {
			
			if(file.exists()) {
			
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
				ClickKeywordHitMapper mapper = mapperSession.getMapper();
				
				mapper.updateClear(siteId, timeId);
				mapperSession.commit();
				for (String rline = null; (rline = br.readLine()) != null;) {
					String[] data = rline.split("\t");

					String keyword = data[0];
					String clickType = data[1];

					int count = 0;
					try {
						count = Integer.parseInt(data[2]);
					} catch (NumberFormatException ignore) {
					}

					logger.trace("#### UpdateClickKeywordTypeHit {} >> {} > {} / {}", timeId, clickType, mapper);
					mapper.putEntry(siteId, timeId, keyword, clickType, count);

				}
			}
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
			
			if (br != null)
				try {
					br.close();
				} catch (IOException ignore){}
		}
		
		return parameter;
	}

}

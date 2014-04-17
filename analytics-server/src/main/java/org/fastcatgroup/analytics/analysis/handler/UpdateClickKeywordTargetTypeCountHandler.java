package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.ClickKeywordTargetHitMapper;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateClickKeywordTargetTypeCountHandler extends ProcessHandler {
	String siteId;
	String timeId;
	File file;
	String encoding;
	
	public UpdateClickKeywordTargetTypeCountHandler(String siteId, String timeId, File file, String encoding) {
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
		MapperSession<ClickKeywordTargetHitMapper> mapperSession = dbService.getMapperSession(ClickKeywordTargetHitMapper.class);
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			ClickKeywordTargetHitMapper mapper = mapperSession.getMapper();
			
			mapper.updateClear(siteId, timeId);
			mapperSession.commit();
			
			for(String rline = null; (rline = br.readLine())!=null;) {
				String[] data = rline.split("\t");
				
				if(data.length < 4) {
					logger.debug("unparsable data:{} / {}",rline, file);
					continue;
				}
				
				String keyword = data[0];
				String target = data[1];
				String clickType = data[2];
				
				int count = 0;
				try {
					count = Integer.parseInt(data[3]);
				} catch (NumberFormatException ignore) { }
				
				logger.trace("#### UpdateClickKeywordTargetTypeHit {} >> {} > {} / {}", timeId, clickType, mapper);
				mapper.putEntry(siteId, timeId, keyword, target, clickType, count);
				
			}
		} finally {
			if (mapperSession != null) {
				mapperSession.commit();
				mapperSession.closeSession();
			}
			
			if(br!=null) try {
				br.close();
			} catch (IOException ignore){}
		}
		
		return parameter;
	}

}

package org.fastcatsearch.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.ClickHitMapper;
import org.fastcatsearch.analytics.db.vo.ClickHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;

public class UpdateClickTypeCountHandler extends ProcessHandler {
	String siteId;
	String timeId;
	File file;
	String encoding;
	
	public UpdateClickTypeCountHandler(String siteId, String timeId, File file, String encoding) {
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
		MapperSession<ClickHitMapper> mapperSession = dbService.getMapperSession(ClickHitMapper.class);
		try {
			
			if(file.exists()) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
				ClickHitMapper mapper = mapperSession.getMapper();
				
				for(String rline = null; (rline = br.readLine())!=null;) {
					String[] data = rline.split("\t");
					
					String clickType = data[0];
					
					int count = 0;
					try {
						count = Integer.parseInt(data[1]);
					} catch (NumberFormatException ignore) { }
					
					logger.debug("#### UpdateClickTypeHit {} >> {} > {} / {}", timeId, clickType, mapper);
					
					ClickHitVO vo = mapper.getEntry(siteId, timeId, clickType);
					
					if(vo != null){
						mapper.updateEntry(siteId, timeId, clickType, count);
					} else {
						mapper.putEntry(siteId, timeId, clickType, count);
					}
					
				}
			}
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
			
			if(br!=null) try {
				br.close();
			} catch (IOException ignore){}
		}
		
		
		return parameter;
	}

}

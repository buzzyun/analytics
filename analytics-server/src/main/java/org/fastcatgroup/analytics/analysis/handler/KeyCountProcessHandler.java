package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.log.KeyCountLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountLogReader;
import org.fastcatgroup.analytics.analysis.log.SearchLogResult;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.service.ServiceManager;

public class KeyCountProcessHandler extends ProcessHandler {

	private File baseDir;
	private String inFileName;
	private String encoding;
	private String dateFrom;
	private String dateTo;
	private String siteId;
	private String categoryId;

	public KeyCountProcessHandler(String siteId, String categoryId,
			File baseDir, String inFileName, String dateFrom, String dateTo,
			String encoding) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.baseDir = baseDir;
		this.inFileName = inFileName;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) {

		File inFile = new File(baseDir, inFileName);
		logger.debug("Process file > {}", inFile);
		KeyCountLogReader reader = null;
		int totalCount = 0;
		
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> mapperSession = null;
		
		int sumCount = 0;
		int avgTime = 0;
		int maxTime = 0;
		
		try {
			mapperSession = dbService.getMapperSession(SearchHitMapper.class);
			
			reader = new KeyCountLogReader(new File[]{inFile}, encoding);
			KeyCountLog log = null;
			while ((log = reader.readLog()) != null) {
				totalCount += log.getCount();
			}
			
			Map<String, Object> calc = mapperSession.getMapper().getCalcHitAndTime(siteId, categoryId, dateFrom, dateTo);
			try {
				sumCount = Integer.parseInt(String.valueOf(calc.get("hit")));
			} catch (NumberFormatException ignore) { }
			try {
				avgTime = Integer.parseInt(String.valueOf(calc.get("avgTime")));
			} catch (NumberFormatException ignore) { }
			try {
				maxTime = Integer.parseInt(String.valueOf(calc.get("maxTime")));
			} catch (NumberFormatException ignore) { }
			
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		return new SearchLogResult(totalCount, sumCount, avgTime, maxTime);
	}
}

package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchPathHitMapper;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateServiceTypeHitHandler extends ProcessHandler {
	private String siteId;
	private String timeId;
	private File sourceFile;
	private String encoding;

	public UpdateServiceTypeHitHandler(String siteId, String timeId, File workingDir, String fileName, String encoding) {
		this.siteId = siteId;
		this.timeId = timeId;
		this.sourceFile = new File(workingDir, fileName);
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) throws Exception {

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		
		BufferedReader br = null;
		MapperSession<SearchPathHitMapper> mapperSession = dbService.getMapperSession(SearchPathHitMapper.class);
		
		try {
			SearchPathHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), encoding));
			
			for(String rline = null; (rline = br.readLine())!=null;) {
				String[] data = rline.split("\t");
				
				String searchId = data[0];
				int count = 0;
				
				try {
					count = Integer.parseInt(data[1]);
				} catch (NumberFormatException ignore) { }
				
				mapper.deleteEntry(siteId, searchId, timeId);
				mapper.putEntry(siteId, searchId, timeId, count);
			}

		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
			
			if(br!=null) try {
				br.close();
			} catch (IOException ignore) { }
		}
		return parameter;
	}
}

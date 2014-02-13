package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateRelateKeywordHandler extends ProcessHandler {

	String siteId;
	File file;

	public UpdateRelateKeywordHandler(String siteId, File file) {
		this.siteId = siteId;
		this.file = file;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		if (file != null && file.exists()) {

			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<RelateKeywordMapper> mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			MapperSession<RelateKeywordValueMapper> vmapperSession = dbService.getMapperSession(RelateKeywordValueMapper.class);
			BufferedReader reader = null;
			
			Map<String, List<String>> keywordMap = new HashMap<String, List<String>>();
			try {
				RelateKeywordMapper mapper = mapperSession.getMapper();
				RelateKeywordValueMapper vmapper = vmapperSession.getMapper();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = null;

				// file 한줄씩 읽어서 입력.
				while ((line = reader.readLine()) != null) {
					// value 파싱.
					String[] el = line.split("\t");
					String keyword = el[1];
					String value = el[0];
					RelateKeywordVO vo = mapper.getEntry(siteId, keyword);
					
					if (vo == null || vo.getId() == 0) {
						vo = new RelateKeywordVO(keyword, timestamp);
						mapper.putEntry(siteId, vo);
					}
					
					List<String>relate = keywordMap.get(vo.getKeyword());
					if(relate == null) {
						relate = new ArrayList<String>();
						keywordMap.put(vo.getKeyword(), relate);
					}
					
					
					if(!relate.contains(value)) {
						relate.add(value);
					}
					
					logger.debug("put relate {} / {} / {}", siteId, vo.getId(), value);
					vmapper.putEntry(vo.getId(), value);
				}
				
				StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
				service.updateRelativeKeywordMap(siteId, keywordMap);
				
			} finally {
				if (mapperSession != null) {
					mapperSession.closeSession();
				}
				if (vmapperSession != null) {
					vmapperSession.closeSession();
				}
				if (reader != null) {
					reader.close();
				}
			}
		}
		return null;
	}
}

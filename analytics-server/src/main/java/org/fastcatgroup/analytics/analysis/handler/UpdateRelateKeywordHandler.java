package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
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
			
			StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
			//map은 기존 데이터와 머징이 되어야 한다.
			Map<String, List<String>> relateKeywordMap = service.getRelateKeywordMap(siteId);
			
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
					
					
					List<String> list = relateKeywordMap.get(keyword);
					
					//서비스 키워드에 없으면 입력해야함. 서비스 키워드는 db와 동일하기 때문.
					boolean needToInsert = false;
					if(list == null){
						list = new ArrayList<String>();
						relateKeywordMap.put(keyword, list);
						list.add(value);
						needToInsert = true;
					}else{
						//없으면 입력.
						if(!list.contains(value)){
							list.add(value);
							needToInsert = true;
						}
					}
					logger.debug("##needToInsert {} : {} : {}", needToInsert, keyword, value);
					if(needToInsert){
						RelateKeywordVO vo = mapper.getEntry(siteId, keyword);
						
						if (vo == null || vo.getId() == 0) {
							//keyword 새로입력.
							vo = new RelateKeywordVO(keyword, timestamp);
							
							mapper.putEntry(siteId, vo);
							vmapper.putEntry(siteId, vo.getId(), value);
							logger.debug("##Put relate {} / {} / {}", siteId, vo.getId(), value);
						}else{
							//업데이트..
							vmapper.putEntry(siteId, vo.getId(), value);
							logger.debug("##Update relate {} / {} / {}", siteId, vo.getId(), value);
						}
					
					}
					
				}
				
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

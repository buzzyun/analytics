package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;

public class UpdateSearchHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	String timeId;
	
	public UpdateSearchHitHandler(String siteId, String categoryId, String timeId) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		Integer count = (Integer) parameter;
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
		try {
			SearchHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			logger.debug("#### UpdateSearchHit {} >> {} > {} / {}", timeId, categoryId, count, mapper);
			
			SearchHitVO vo = mapper.getEntry(siteId, categoryId, timeId);
			if(vo != null){
				mapper.updateEntry(siteId, categoryId, timeId, count);
			} else {
				mapper.putEntry(siteId, categoryId, timeId, count);
			}
			
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		return null;
	}

}

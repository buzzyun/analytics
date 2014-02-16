package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.Counter;

public class UpdateSearchTypeHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	String timeId;
	String[] typeList;

	public UpdateSearchTypeHitHandler(String siteId, String categoryId, String timeId, String[] typeList) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
		this.typeList = typeList;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		Map<String, Counter>[] typeCouterList = (Map<String, Counter>[]) parameter;

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			for (int i = 0; i < typeList.length; i++) {
				String typeId = typeList[i];
				Map<String, Counter> typeCounterMap = typeCouterList[i];

				int count = mapper.getCount(siteId, categoryId, timeId, typeId);
				if (count > 0) {
					mapper.updateClear(siteId, categoryId, timeId, typeId);
				}

				for (Entry<String, Counter> entry : typeCounterMap.entrySet()) {
					String dtype = entry.getKey();
					Counter counter = entry.getValue();
					logger.debug("Update Type hit {} : {} : {}", typeId, dtype, counter.value());
					mapper.putEntry(siteId, categoryId, timeId, typeId, dtype, counter.value());
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

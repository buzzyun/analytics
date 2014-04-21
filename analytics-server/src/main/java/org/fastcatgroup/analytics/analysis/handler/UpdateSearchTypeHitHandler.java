package org.fastcatgroup.analytics.analysis.handler;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.Counter;

public class UpdateSearchTypeHitHandler extends ProcessHandler {
	String siteId;
	String categoryId;
	String timeId;
	List<TypeSetting> typeList;

	public UpdateSearchTypeHitHandler(String siteId, String categoryId, String timeId, List<TypeSetting> typeList) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeId = timeId;
		this.typeList = typeList;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Counter>[] typeCounterList = (Map<String, Counter>[]) parameter;

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			Calendar cal = StatisticsUtils.getCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			for (int i = 0; i < typeList.size(); i++) {
				String typeId = typeList.get(i).getId();
				Map<String, Counter> typeCounterMap = typeCounterList[i];

				int count = mapper.getCount(siteId, categoryId, timeId, typeId);
				if (count > 0) {
					mapper.updateClear(siteId, categoryId, timeId, typeId);
				}

				for (Entry<String, Counter> entry : typeCounterMap.entrySet()) {
					String dtype = entry.getKey();
					Counter counter = entry.getValue();
					//logger.debug("Update Type hit {} : {} : {}", typeId, dtype, counter.value());
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

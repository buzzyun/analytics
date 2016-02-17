package org.fastcatsearch.analytics.analysis.handler;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.Counter;
import org.fastcatsearch.ir.io.CharVector;

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
		Map<CharVector, Counter>[] typeCounterList = (Map<CharVector, Counter>[]) parameter;

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			// 기준시각.
			Calendar cal = StatisticsUtils.getNowCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1);

			for (int i = 0; i < typeList.size(); i++) {
				String typeId = typeList.get(i).getId();
				Map<CharVector, Counter> typeCounterMap = typeCounterList[i];

				int count = mapper.getCount(siteId, categoryId, timeId, typeId);
				if (count > 0) {
					mapper.updateClear(siteId, categoryId, timeId, typeId);
				}

				for (Entry<CharVector, Counter> entry : typeCounterMap.entrySet()) {
					CharVector dtype = entry.getKey();
					Counter counter = entry.getValue();
					//logger.debug("Update Type hit {} : {} : {}", typeId, dtype, counter.value());
					mapper.putEntry(siteId, categoryId, timeId, typeId, dtype.toString(), counter.value());
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

package org.fastcatsearch.analytics.analysis.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatsearch.analytics.db.vo.SearchTypeHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.Counter;
import org.fastcatsearch.ir.io.CharVector;

public class SearchTypeDatabaseProcessHandler extends ProcessHandler {

	private String siteId;
	private String categoryId;
	private String timeFrom;
	private String timeTo;
	private Map<CharVector, Integer> typeMap;
	
	public SearchTypeDatabaseProcessHandler(String siteId, String categoryId, String timeFrom, String timeTo, List<TypeSetting> typeList) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		
		typeMap = new HashMap<CharVector, Integer>();
		for (int inx = 0; inx < typeList.size(); inx++) {
			typeMap.put(new CharVector(typeList.get(inx).getId(), true), inx);
		}
	}

	@Override
	public Object process(Object parameter) {
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<SearchTypeHitMapper> mapperSession = null;
			List<SearchTypeHitVO> entryList = null;
			try {
				//전부 머지되서 나오기 때문에 형식만 맞추어서 바로 넘겨준다.
				mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
				SearchTypeHitMapper mapper = mapperSession.getMapper();
				entryList = mapper.getEntryListGroupByType(siteId, categoryId, timeFrom, timeTo);
				@SuppressWarnings("unchecked")
				Map<CharVector, Counter>[] map = new Map[typeMap.size()];
				for (int inx = 0; inx < typeMap.size(); inx++) {
					map[inx] = new HashMap<CharVector, Counter>();
				}
				
				for (int inx = 0; inx < entryList.size(); inx++) {
					SearchTypeHitVO vo = entryList.get(inx);
					Integer colInx = typeMap.get(new CharVector(vo.getTypeId(), true));
					if(colInx != null && colInx >= 0 && colInx < map.length) {
						map[colInx].put(new CharVector(vo.getDtype(), true), new Counter(vo.getHit()));
					}
				}
				return map;
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				if(mapperSession != null) try {
					mapperSession.closeSession();
				} catch (Exception ignore) { }
			}
			return null;
	}
}

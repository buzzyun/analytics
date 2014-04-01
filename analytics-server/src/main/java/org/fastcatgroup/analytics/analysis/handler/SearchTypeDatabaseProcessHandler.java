package org.fastcatgroup.analytics.analysis.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.Counter;

public class SearchTypeDatabaseProcessHandler extends ProcessHandler {

	private String siteId;
	private String categoryId;
	private String timeFrom;
	private String timeTo;
	private Map<String, Integer> typeMap;
	
	public SearchTypeDatabaseProcessHandler(String siteId, String categoryId, String timeFrom, String timeTo, String[] typeList) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		
		typeMap = new HashMap<String, Integer>();
		for (int inx = 0; inx < typeList.length; inx++) {
			typeMap.put(typeList[inx], inx);
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
				Map<String, Counter>[] map = new Map[typeMap.size()];
				for (int inx = 0; inx < typeMap.size(); inx++) {
					map[inx] = new HashMap<String, Counter>();
				}
				
				for (int inx = 0; inx < entryList.size(); inx++) {
					SearchTypeHitVO vo = entryList.get(inx);
					Integer colInx = typeMap.get(vo.getTypeId());
					if(colInx != null && colInx >= 0 && colInx < map.length) {
						map[colInx].put(vo.getDtype(), new Counter(vo.getHit()));
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

package org.fastcatgroup.analytics.analysis.log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;

public class TypeSearchDatabaseReader extends DatabaseLogReader<TypeSearchLog, SearchTypeHitMapper, Map<String, Object>> {
	
	private String siteId;
	private String from;
	private String to;
	private String[] typeList;

	public TypeSearchDatabaseReader(
			MapperSession<SearchTypeHitMapper> mapperSession, String siteId,
			String[] typeList,
			String from, String to) throws IOException {
		super(mapperSession);
		this.siteId = siteId;
		this.typeList = typeList;
		this.from = from;
		this.to = to;
	}

	@Override
	protected List<Map<String, Object>> prepareLog(SearchTypeHitMapper mapper) {
		logger.debug("prepare log..");
		try {
			List<Map<String, Object>> list = mapper.getAllTypeEntryListBetween(siteId, from, to);
			
			logger.debug("db log : {}", list);
			return list;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return null;
	}

	@Override
	protected TypeSearchLog makeLog(Map<String, Object> dataMap) {
		int count = 0;
		try {
			count = (Integer)(dataMap.get("hit"));
		} catch (NumberFormatException ignore) { };
		
		String[] data = new String[ 3 + typeList.length ];
		
		data[0] = "";
		data[1] = (String)dataMap.get("categoryId");
		data[2] = (String)dataMap.get("keyword");
		
		for(int inx=0; inx<typeList.length; inx ++) {
			data[inx + 2] = (String)dataMap.get("d"+inx);
		}
		
		TypeSearchLog typeSearchLog = new TypeSearchLog(data, count);
		logger.debug("make log : {} : {}", typeSearchLog, count);
		return typeSearchLog;
	}
}

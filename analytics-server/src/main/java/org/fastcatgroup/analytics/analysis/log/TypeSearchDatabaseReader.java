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

	public TypeSearchDatabaseReader(
			MapperSession<SearchTypeHitMapper> mapperSession, String siteId,
			String from, String to) throws IOException {
		super(mapperSession);
		this.siteId = siteId;
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
	protected TypeSearchLog makeLog(Map<String, Object> data) {
		int count = 0;
		try {
			count = (Integer)(data.get("hit"));
		} catch (NumberFormatException ignore) { };
		
		TypeSearchLog typeSearchLog = new TypeSearchLog(new String[] { 
				(String)data.get("category"),
				(String)data.get("keyword"),
				(String)data.get("d0"),
				(String)data.get("d1"),
				(String)data.get("d2"),
				(String)data.get("d3"),
				(String)data.get("d4"),
				(String)data.get("d5"),
				(String)data.get("d6") }, count);
		logger.debug("make log : {} : {}", typeSearchLog, count);
		return typeSearchLog;
	}
}

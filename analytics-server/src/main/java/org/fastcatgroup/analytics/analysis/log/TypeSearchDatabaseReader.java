package org.fastcatgroup.analytics.analysis.log;

import java.io.IOException;
import java.util.List;

import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;

public class TypeSearchDatabaseReader extends DatabaseLogReader<TypeSearchLog, SearchTypeHitMapper, SearchTypeHitVO> {
	
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
	protected List<SearchTypeHitVO> prepareLog(SearchTypeHitMapper mapper) {
		logger.debug("prepare log..");
		try {
			List<SearchTypeHitVO> list = mapper.getAllTypeEntryListBetween(siteId, from, to);
			
			logger.debug("db log : {}", list);
			return list;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return null;
	}

	@Override
	protected TypeSearchLog makeLog(SearchTypeHitVO vo) {
		TypeSearchLog typeSearchLog = new TypeSearchLog(new String[] { vo.getCategoryId(), "-",
				vo.getDtype() }, vo.getHit());
		logger.debug("make log : {} : {}", typeSearchLog, vo.getHit());
		return typeSearchLog;
	}
}

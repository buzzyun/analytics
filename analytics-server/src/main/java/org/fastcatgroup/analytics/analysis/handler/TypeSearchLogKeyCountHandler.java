package org.fastcatgroup.analytics.analysis.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.util.Counter;

/**
 * search log를 읽어들여 key-count를 계산한다.
 * */
public class TypeSearchLogKeyCountHandler extends CategoryLogHandler<TypeSearchLog> {

	Map<String, Counter>[] typeCounterList;
	String[] typeList;

	public TypeSearchLogKeyCountHandler(String categoryId, String[] typeList) {
		super(categoryId);
		logger.trace("##TypeSearchLogKeyCountHandler > {}", categoryId);
		this.typeList = typeList;
		typeCounterList = new Map[typeList.length];
		for (int i = 0; i < typeList.length; i++) {
			typeCounterList[i] = new HashMap<String, Counter>();
		}
	}

	@Override
	public void handleLog(TypeSearchLog logData) throws IOException {
		logger.trace("##TypeSearchLogKeyCountHandler > {} > {}", categoryId, logData);
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if (categoryId.equals(logData.categoryId())) {
				// 해당 카테고리만
				for (int i = 0; i < logData.typeLength(); i++) {
					String type = logData.getType(i);
					if (type.equals("-")) {
						continue;
					}
					Counter counter = null;
					
					if(typeCounterList.length > i) {
						counter = typeCounterList[i].get(type);
						if (counter == null) {
							counter = new Counter(logData.getCount());
							typeCounterList[i].put(type, counter);
						} else {
							counter.increment(logData.getCount());
						}
						logger.trace("##TypeSearchLogKeyCountHandler1 type-count > {}:{} > {}:{}", categoryId, logData.categoryId(), type, counter);
					}
				}
			} else if (categoryId.equals("_root")) {
				// root는 모두다.
				for (int i = 0; i < logData.typeLength(); i++) {
					String type = logData.getType(i);
					if (type.equals("-")) {
						continue;
					}
					Counter counter = null;
					
					if(typeCounterList.length > i) {
						counter = typeCounterList[i].get(type);
						if (counter == null) {
							counter = new Counter(1);
							typeCounterList[i].put(type, counter);
						} else {
							counter.increment(logData.getCount());
						}
						logger.trace("##TypeSearchLogKeyCountHandler2 type-count > {}:{} > {}:{}", categoryId, logData.categoryId(), type, counter);
					}
				}
			}

		}
	}

	@Override
	public Object done() throws IOException {
		return typeCounterList;
	}

}

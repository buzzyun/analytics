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

	Map<String, Counter>[] typeCouterList;
	String[] typeList;

	public TypeSearchLogKeyCountHandler(String categoryId, String[] typeList) {
		super(categoryId);
		logger.debug("##TypeSearchLogKeyCountHandler > {}", categoryId);
		this.typeList = typeList;
		typeCouterList = new Map[typeList.length];
		for (int i = 0; i < typeList.length; i++) {
			typeCouterList[i] = new HashMap<String, Counter>();
		}
	}

	@Override
	public void handleLog(TypeSearchLog logData) throws IOException {
		logger.debug("##TypeSearchLogKeyCountHandler > {} > {}", categoryId, logData);
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if (categoryId.equals(logData.categoryId())) {
				// 해당 카테고리만
				for (int i = 0; i < logData.typeLength(); i++) {
					String type = logData.getType(i);
					if (type.equals("-")) {
						continue;
					}
					Counter counter = typeCouterList[i].get(type);
					if (counter == null) {
						counter = new Counter(1);
						typeCouterList[i].put(type, counter);
					} else {
						counter.increment();
					}
					logger.debug("##TypeSearchLogKeyCountHandler1 type-count > {}:{} > {}:{}", categoryId, logData.categoryId(), type, counter);
				}
			} else if (categoryId.equals("_root")) {
				// root는 모두다.
				for (int i = 0; i < logData.typeLength(); i++) {
					String type = logData.getType(i);
					if (type.equals("-")) {
						continue;
					}
					Counter counter = typeCouterList[i].get(type);
					if (counter == null) {
						counter = new Counter(1);
						typeCouterList[i].put(type, counter);
					} else {
						counter.increment();
					}
					logger.debug("##TypeSearchLogKeyCountHandler2 type-count > {}:{} > {}:{}", categoryId, logData.categoryId(), type, counter);
				}
			}

		}
	}

	@Override
	public Object done() throws IOException {
		return typeCouterList;
	}

}

package org.fastcatgroup.analytics.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.CategoryLogHandler;
import org.fastcatgroup.analytics.util.Counter;

public class TestSearchLogHandler extends CategoryLogHandler<SearchLog> {

	Map<String, Counter> map;

	public TestSearchLogHandler(){
		map = new HashMap<String, Counter>();
	}
	
	@Override
	public void reset() {
		map.clear();
	}
	
	@Override
	public void handleLog(SearchLog logData) {
		String keyword = logData.keyword();
		logger.debug("keyword > {}", keyword);
		Counter c = map.get(keyword);
		if (c == null) {
			c = new Counter(1);
			map.put(keyword, c);
		} else {
			c.increment();
		}
	}

	@Override
	public Set<String> done() {
		return null;
	}

}

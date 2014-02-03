package org.fastcatgroup.analytics.analysis;

import java.util.HashMap;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.LogAggregator.Counter;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.LogHandler;

public class SimpleLogHandler extends LogHandler<SearchLog> {

	Map<String, Counter> map;

	public SimpleLogHandler(){
		map = new HashMap<String, Counter>();
	}
	
	@Override
	protected void reset() {
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
	public void process(Object[] parameters) {
		// do nothing.
	}

	@Override
	protected Object[] doDone() {
		return new Object[] { map };
	}

	

}

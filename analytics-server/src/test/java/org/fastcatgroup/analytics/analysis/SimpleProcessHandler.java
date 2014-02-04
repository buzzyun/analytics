package org.fastcatgroup.analytics.analysis;

import java.util.Map;

import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;

public class SimpleProcessHandler extends ProcessHandler {

	@Override
	public void reset() {
	}

	@Override
	public Object process(Object parameter) {
		Map map =  (Map) parameter;
		logger.debug("Result > {}", map);
		return null;
	}


}

package org.fastcatgroup.analytics.analysis;

import java.util.Map;

import org.fastcatgroup.analytics.analysis2.ProcessHandler;

public class SimpleProcessHandler extends ProcessHandler {

	@Override
	protected void reset() {
		
	}

	@Override
	public void process(Object[] parameters) {
		Map map =  (Map) parameters[0];
		logger.debug("Result > {}", map);
	}

	@Override
	protected Object[] doDone() {
		return null;
	}

}

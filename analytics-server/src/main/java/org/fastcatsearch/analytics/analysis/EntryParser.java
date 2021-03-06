package org.fastcatsearch.analytics.analysis;

import org.fastcatsearch.analytics.analysis.util.RunEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface EntryParser<E extends RunEntry> {
	public static Logger logger = LoggerFactory.getLogger(EntryParser.class);
	
	public E parse(String line);

}

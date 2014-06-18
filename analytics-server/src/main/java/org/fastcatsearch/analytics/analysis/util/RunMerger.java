package org.fastcatsearch.analytics.analysis.util;

import java.io.IOException;

public interface RunMerger {
	
	public void merge() throws IOException;
	
	public void close();
	
}

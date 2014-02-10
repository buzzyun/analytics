package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;

public class UpdateRelateKeywordHandler extends ProcessHandler {

	String siteId;
	String categoryId;
	File file;
	
	public UpdateRelateKeywordHandler(String siteId, String categoryId, File file) {
		this.siteId = siteId;
		this.categoryId = categoryId;
		this.file = file;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		if (file != null && file.exists()) {
			
			//TODO db입력.
		}
		return null;
	}

}

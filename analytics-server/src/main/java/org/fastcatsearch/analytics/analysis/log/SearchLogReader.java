package org.fastcatsearch.analytics.analysis.log;

import java.io.File;
import java.io.IOException;

public class SearchLogReader extends FileListLogReader<SearchLog> {

	public SearchLogReader(File[] file, String encoding) throws IOException {
		super(file, encoding);
	}

	@Override
	protected SearchLog makeLog(String[] el) {
		String[] el2 = null;
		//FIXME:임시코드. 로그호환성을 맞추기 위해 제작한 임시코드.
		if(el.length < 7) { 
			el2 = new String[7];
			logger.error(">>>>>>>>>>>>>>>>> Fixme!!");
			System.arraycopy(el, 0, el2, 0, el.length); 
		} else {
			el2 = el;
		}
		
		return new SearchLog(el2[0], el2[1], el2[2], "1", el2[4], el2[5], el2[6]);
	}
}

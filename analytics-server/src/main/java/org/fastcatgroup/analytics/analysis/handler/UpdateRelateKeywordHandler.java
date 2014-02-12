package org.fastcatgroup.analytics.analysis.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;

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

			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<RelateKeywordMapper> mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			MapperSession<RelateKeywordValueMapper> vmapperSession = dbService.getMapperSession(RelateKeywordValueMapper.class);
			BufferedReader reader = null;
			try {
				RelateKeywordMapper mapper = mapperSession.getMapper();
				RelateKeywordValueMapper vmapper = vmapperSession.getMapper();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());

				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line = null;

				// file 한줄씩 읽어서 입력.
				while ((line = reader.readLine()) != null) {
					// value 파싱.
					String[] el = line.split("\t");
					String keyword = el[0];
					String values = el[1];
					RelateKeywordVO vo = new RelateKeywordVO(categoryId, keyword, values, timestamp);
					mapper.putEntry(vo);
					int id = vo.getId();
					vmapper.putEntry(id, vo.getValue());
				}
			} finally {
				if (mapperSession != null) {
					mapperSession.closeSession();
				}
				if (vmapperSession != null) {
					vmapperSession.closeSession();
				}
				if (reader != null) {
					reader.close();
				}
			}

		}
		return null;
	}

}

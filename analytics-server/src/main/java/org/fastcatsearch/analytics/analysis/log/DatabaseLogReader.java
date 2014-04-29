package org.fastcatsearch.analytics.analysis.log;

import java.io.IOException;
import java.util.List;

import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.AnalyticsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DatabaseLogReader<LogType extends LogData, MapperType extends AnalyticsMapper, DataType> implements SourceLogReader<LogType> {

	protected static final Logger logger = LoggerFactory.getLogger(DatabaseLogReader.class);

	private MapperSession<MapperType> mapperSession;

	private List<DataType> list;

	private int currentInx;

	public DatabaseLogReader(MapperSession<MapperType> mapperSession) throws IOException {
		this.mapperSession = mapperSession;
	}

	@Override
	public LogType readLog() {
		try {

			if (list == null) {
				list = prepareLog(mapperSession.getMapper());
			}

			while (currentInx < list.size()) {

				DataType data = list.get(currentInx++);

				return makeLog(data);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	protected abstract List<DataType> prepareLog(MapperType mapper);

	protected abstract LogType makeLog(DataType data);

	@Override
	public void close() {
		if (mapperSession != null)
			try {
				mapperSession.closeSession();
			} catch (Exception ignore) {
			}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

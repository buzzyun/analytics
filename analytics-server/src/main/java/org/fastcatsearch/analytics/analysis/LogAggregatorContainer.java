package org.fastcatsearch.analytics.analysis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fastcatsearch.analytics.analysis.log.LogData;
import org.fastcatsearch.analytics.analysis.util.AggregationResultWriter;
import org.fastcatsearch.analytics.analysis.util.RunMerger;

public class LogAggregatorContainer<LogType extends LogData> extends AbstractLogAggregator<LogType> {
	
	public LogAggregatorContainer() {
		super(0, "", 0);
		this.aggregators = new ArrayList<AbstractLogAggregator<LogType>>();
	}

	private List<AbstractLogAggregator<LogType>> aggregators;
	
	public void addAggregator(AbstractLogAggregator<LogType> type) {
		aggregators.add(type);
	}

	public void handleLog(LogType log) throws IOException {
		for(AbstractLogAggregator<LogType> aggregator : aggregators) {
			aggregator.handleLog(log);
		}
	}
	
	public void flushRun() throws IOException {
		for(AbstractLogAggregator<LogType> aggregator : aggregators) {
			aggregator.flushRun();
		}
	}

	public void done() throws IOException {
		for(AbstractLogAggregator<LogType> aggregator : aggregators) {
			aggregator.done();
		}
	}
	@Override protected AggregationResultWriter newRunWriter(String encoding, int flushId) { return null; }
	@Override protected RunMerger newFinalMerger(String encoding, int flushCount) { return null; }
	@Override protected void doDone() { }
}
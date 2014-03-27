package org.fastcatgroup.analytics.analysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis.util.AggregationResultWriter;
import org.fastcatgroup.analytics.analysis.util.RunMerger;
import org.fastcatgroup.analytics.util.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LogReader를 이용해 handleLog(String line) 를 통해 들어온 로그 1줄을 파싱해 읽어들여, aggregateMap에
 * 카운트를 올려준다. (통계작업) 메모리가 커질수 있으므로, aggregateMap.size()가 미리정해놓은 runSize보다 커지면
 * 파일로 flush한다. flush한 run파일은 comparator 를 통해 정렬하여 기록된다. 마지막 done()호출시 run들을
 * 파일병합하여 하나의 정렬된 파일로 만든다.
 * 
 * 하위클래스에서는 source 로그파일을 읽어들이는 reader를 정의하고, output을 위한 comparator 를 정의한다.
 * 
 * */
public abstract class AbstractLogAggregator<LogType extends LogData> {
	protected static Logger logger = LoggerFactory.getLogger(AbstractLogAggregator.class);

	private int runKeySize;
	private Map<String, Counter> aggregateMap;
	private int flushCount;
	private String outputEncoding;
	protected Set<String> banWords;
	protected int minimumHitCount;
	
	
	public AbstractLogAggregator(int runKeySize, String outputEncoding, int minimumHitCount) {
		this.runKeySize = runKeySize;
		this.aggregateMap = new HashMap<String, Counter>(runKeySize);
		this.outputEncoding = outputEncoding;
		this.minimumHitCount = minimumHitCount;
	}

	// 중간 run 결과를 기록할 writer를 받는다.
	protected abstract AggregationResultWriter newRunWriter(String encoding, int flushId);

	// 최종 머징 결과를 기록할 merger를 받는다.
	protected abstract RunMerger newFinalMerger(String encoding, int flushCount);

	protected abstract void doDone();
	
	public void handleLog(LogType log) throws IOException {
//		logger.debug("{}: {}", getClass().getSimpleName(), log);
		if (log != null && log.getKey() != null && log.getKey().length() > 0) {
			Counter counter = aggregateMap.get(log.getKey());
//			logger.debug("##handle log {} > {}", log.getKey(), counter);
			if (counter != null) {
				counter.increment(log.getCount());
			} else {
				aggregateMap.put(log.getKey(), new Counter(1));
			}
		}

		// 메모리에 쌓인갯수 체크하여 run생성.
		if (aggregateMap.size() >= runKeySize) {
			flushRun();
		}
	}

	
	private void flushRun() throws IOException {
		TreeMap<String, Counter> sortedMap = new TreeMap<String, Counter>();
		sortedMap.putAll(aggregateMap);
		aggregateMap.clear();
		AggregationResultWriter logWriter = newRunWriter(outputEncoding, flushCount++);
		try {
			for (Map.Entry<String, Counter> entry : sortedMap.entrySet()) {
				logWriter.write(entry.getKey(), entry.getValue().value());
			}
		} finally {
			if (logWriter != null) {
				logWriter.close();
			}
		}
	}

	public void done() throws IOException {
		
		logger.debug("##aggregate count {}", flushCount);
		if (aggregateMap.size() > 0) {
			flushRun();
		}

		// 여기까지오면, targetDir에 0.log, 1.log 등의 run들이 쌓여있다.
		// 이제 정렬된 run들을 모아서 하나로 만든다.
		if(flushCount > 0) {
			RunMerger merger = newFinalMerger(outputEncoding, flushCount);
			if (merger != null) {
				merger.merge();
			}
		}

		doDone();
	}
	

}

package org.fastcatsearch.analytics.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.util.AggregationResultWriter;
import org.fastcatsearch.analytics.analysis.util.RunMerger;
import org.fastcatsearch.analytics.util.Counter;
import org.fastcatsearch.ir.io.CharVector;

public class ServiceCountLogAggregator<LogType extends SearchLog> extends AbstractLogAggregator<LogType> {
	
	//서비스 갯수가 한정적이기 때문에 파일베이스를 쓰지 않고 바로 메모리에서 처리한다.
	private Map<CharVector, Counter> serviceCounter;
	private File destFile;
	private String encoding;
	private List<ServiceSetting> serviceTypes;

	public ServiceCountLogAggregator(File targetDir, String targetFilename, String encoding, List<ServiceSetting> serviceTypes) {
		super(0, null, 0);
		this.destFile = new File(targetDir, targetFilename);
		this.encoding = encoding;
		this.serviceTypes = serviceTypes;
		serviceCounter = new HashMap<CharVector, Counter>();
		for(ServiceSetting serviceType : serviceTypes) {
			serviceCounter.put(new CharVector(serviceType.getId(), true), new Counter(0));
		}
		serviceCounter.put(new CharVector("_etc", true), new Counter(0));
	}

	@Override
	public void handleLog(LogType log) throws IOException {
		String serviceType = log.getServiceType();
		Counter counter = null;
		CharVector key = new CharVector(serviceType, true);
		if(serviceCounter.containsKey(key)) {
			counter = serviceCounter.get(key);
		} else {
			counter = serviceCounter.get(new CharVector("_etc", true));
		}
		counter.increment(log.getCount());
	}

	@Override
	public void done() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), encoding));
			
			List<CharVector> serviceList = new ArrayList<CharVector>();
			for(ServiceSetting serviceSetting : serviceTypes) {
				serviceList.add(new CharVector(serviceSetting.getId(), true));
			}
			serviceList.add(new CharVector("_etc", true));
			
			Collections.sort(serviceList);
			
			for(CharVector serviceType : serviceList) {
				writer.append(serviceType).append("\t")
					.append(String.valueOf(serviceCounter.get(serviceType).value()))
					.append("\n");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (writer != null) try {
				writer.close();
			} catch (IOException ignore) { }
		}
	}

	@Override protected AggregationResultWriter newRunWriter(String encoding, int flushId) { return null; }
	@Override protected RunMerger newFinalMerger(String encoding, int flushCount) { return null; }
	@Override protected void doDone() { }
}
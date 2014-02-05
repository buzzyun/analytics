package org.fastcatgroup.analytics.analysis2;

import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis2.schedule.Schedule;
import org.fastcatgroup.analytics.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisTask<LogType extends LogData> extends Job implements Comparable<AnalysisTask<LogType>> {
	private static final long serialVersionUID = -8028269282257112376L;

	protected static Logger logger = LoggerFactory.getLogger(AnalysisTask.class);

	private Schedule schedule;
	private int priority;

	private Runnable preProcess;
	private List<Calculator<LogType>> calculatorList;
	private SourceLogReaderFactory<LogType> readerFactory;
	private int executeCount;

	public AnalysisTask(Schedule schedule, int priority, SourceLogReaderFactory<LogType> readerFactory) {
		this.schedule = schedule;
		this.priority = priority;
		this.readerFactory = readerFactory;
		calculatorList = new ArrayList<Calculator<LogType>>();
	}


	public int priority() {
		return priority;
	}

	public void addCalculator(Calculator<LogType> calculator) {
		calculatorList.add(calculator);
	}

	public void reset() {
		for (Calculator<LogType> c : calculatorList) {
			c.reset();
		}
	}

	@Override
	public JobResult doRun() {
		try {
			
			if(preProcess != null){
				preProcess.run();
			}
			
			SourceLogReader<LogType> reader = readerFactory.createReader();
			try {
				LogType logData = null;
				while ((logData = reader.readLog()) != null) {
					logger.debug("logData > {}", logData);
					for (Calculator<LogType> c : calculatorList) {
						c.offerLog(logData);
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			for (Calculator<LogType> c : calculatorList) {
				c.calculate();
			}
			
			
		}catch(Exception e){
			logger.error("", e);
			return new JobResult(false);
		}
		
		return new JobResult(true);
	}

	@Override
	public int compareTo(AnalysisTask<LogType> o) {
		int c = (int) (schedule.scheduledTime() - o.schedule.scheduledTime());

		if (c == 0) {
			return priority - o.priority;
		}

		return c;
	}

	public void updateScheduleTimeByNow() {
		schedule.updateSchduleTime();
	}

	public long getScheduledTime() {
		return schedule.scheduledTime();
	}

	public long getDelayedScheduledTime() {
		return schedule.delayedScheduledTime();
	}

	public int getExecuteCount() {
		return executeCount;
	}

	public void incrementExecution() {
		executeCount++;
	}


	public void preProcess(Runnable runnable) {
		
	}

}

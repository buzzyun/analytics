package org.fastcatgroup.analytics.analysis.task;

import java.util.List;

import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis.log.SourceLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;
import org.fastcatgroup.analytics.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AnalysisTask<LogType extends LogData> extends Job implements Comparable<AnalysisTask<LogType>> {
	private static final long serialVersionUID = -8028269282257112376L;

	protected static Logger logger = LoggerFactory.getLogger(AnalysisTask.class);

	private Schedule schedule;
	private int priority;

	private Runnable preProcess;
	protected List<Calculator<LogType>> calculatorList;
	protected SourceLogReader<LogType> logReader;
	private int executeCount;

	public AnalysisTask(Schedule schedule, int priority) {
		this.schedule = schedule;
		this.priority = priority;
	}

	public abstract void prepare();

	public int priority() {
		return priority;
	}

	@Override
	public JobResult doRun() {
		try {

			if (preProcess != null) {
				preProcess.run();
			}

			if (logReader != null) {
				try {
					LogType logData = null;
					int n = 0;
					while ((logData = logReader.readLog()) != null) {
						logger.debug("{} : {}", n++, logData);
						for (Calculator<LogType> c : calculatorList) {
							c.offerLog(logData);
						}
					}
				} finally {
					if (logReader != null) {
						logReader.close();
					}
				}
			}
			
			for (Calculator<LogType> c : calculatorList) {
				c.calculate();
			}

		} catch (Exception e) {
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

	public void preProcess(Runnable preProcess) {
		this.preProcess = preProcess;
	}

}

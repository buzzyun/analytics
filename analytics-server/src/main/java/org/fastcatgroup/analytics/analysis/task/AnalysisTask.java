package org.fastcatgroup.analytics.analysis.task;

import java.util.ArrayList;
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
	
	protected String siteId;
	protected List<String> categoryIdList;
	private Schedule schedule;
	private int priority;

	private List<Calculator<LogType>> calculatorList;
	protected SourceLogReader<LogType> logReader;
	private int executeCount;

	public AnalysisTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		this.siteId = siteId;
		this.categoryIdList = categoryIdList;
		this.schedule = schedule;
		this.priority = priority;
		this.calculatorList = new ArrayList<Calculator<LogType>>();
	}

	protected abstract void prepare();
	
	protected void addCalculator(Calculator<LogType> calculator){
		calculator.init();
		calculatorList.add(calculator);
	}
	
	protected void preProcess(){ 
	}

	public int priority() {
		return priority;
	}

	@Override
	public JobResult doRun() {
		try {
			preProcess();
			
			prepare();
			
			logger.debug("AnalysisTask logReader > {}", logReader);
			
			if (logReader != null) {
				try {
					LogType logData = null;
					int n = 0;
					while ((logData = logReader.readLog()) != null) {
						logger.debug("logReader.readLog() {} : {}", n++, logData);
						logger.debug("Task calculatorList {}", calculatorList);
						for (Calculator<LogType> c : calculatorList) {
							logger.debug("offer log to  {} < {}", c, logData);
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

}

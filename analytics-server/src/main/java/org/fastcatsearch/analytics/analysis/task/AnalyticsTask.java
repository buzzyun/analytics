package org.fastcatsearch.analytics.analysis.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.log.LogData;
import org.fastcatsearch.analytics.analysis.log.SourceLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AnalyticsTask<LogType extends LogData> extends Job implements Comparable<AnalyticsTask<LogType>> {
	private static final long serialVersionUID = -8028269282257112376L;

	protected static Logger logger = LoggerFactory.getLogger(AnalyticsTask.class);
	protected String name;
	protected String siteId;
	protected List<String> categoryIdList;
	private Schedule schedule;
	private int priority;

	private List<Calculator<LogType>> calculatorList;
	protected SourceLogReader<LogType> logReader;
	private int executeCount;
	public AnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		this("Noname", siteId, categoryIdList, schedule, priority);
	}
	public AnalyticsTask(String name, String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		this.name = name;
		this.siteId = siteId;
		this.categoryIdList = categoryIdList;
		this.schedule = schedule;
		this.priority = priority;
		this.calculatorList = new ArrayList<Calculator<LogType>>();
	}

	protected abstract void prepare(Calendar calendar);
	
	protected void addCalculator(Calculator<LogType> calculator){
		calculator.init();
		calculatorList.add(calculator);
	}
	
	protected void preProcess(){ 
	}

	public String name(){
		return name;
	}
	public int priority() {
		return priority;
	}

	@Override
	public JobResult doRun() {
		try {
			
			calculatorList.clear();
			
			preProcess();
			Calendar calendar = StatisticsUtils.getCalendar();
			calendar.setTimeInMillis(getBaseTime());
			
			prepare(calendar);
			
			logger.info("### {}-{} Run > {}", getClass().getSimpleName(), name, schedule);
			
			if (logReader != null) {
				try {
					LogType logData = null;
					int n = 0;
					while ((logData = logReader.readLog()) != null) {
//						logger.debug("logReader.readLog() {} : {}", n++, logData);
//						logger.debug("Task calculatorList {}", calculatorList);
						for (Calculator<LogType> c : calculatorList) {
							//logger.debug("offer log to  {} < {}", c, logData);
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

			logger.info("### {}-{} Done!", getClass().getSimpleName(), name);
		} catch (Exception e) {
			logger.info("### {}-{} Error!", getClass().getSimpleName(), name);
			logger.error("", e);
			return new JobResult(false);
		}

		return new JobResult(true);
	}

	@Override
	public int compareTo(AnalyticsTask<LogType> o) {
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
	
	public long getBaseTime() {
		return schedule.baseTime();
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
	
	public String toString() {
		return getClass().getSimpleName() + "[" + name + "] priority[" + priority + "] siteId[" + siteId + "] executeCount[" + executeCount + "] schedule["+schedule+"]";
	}

}

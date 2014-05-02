package org.fastcatsearch.analytics.analysis.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.log.LogData;
import org.fastcatsearch.analytics.analysis.log.SourceLogReader;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SystemErrorMapper;
import org.fastcatsearch.analytics.db.mapper.TaskResultMapper;
import org.fastcatsearch.analytics.db.vo.SystemErrorVO;
import org.fastcatsearch.analytics.db.vo.TaskResultVO;
import org.fastcatsearch.analytics.job.Job;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AnalyticsTask<LogType extends LogData> extends Job implements Comparable<AnalyticsTask<LogType>> {
	private static final long serialVersionUID = -8028269282257112376L;

	protected static Logger logger = LoggerFactory.getLogger(AnalyticsTask.class);
	protected String id;
	protected String name;
	protected String siteId;
	protected List<String> categoryIdList;
	private Schedule schedule;
	private int priority;

	private List<Calculator<LogType>> calculatorList;
	protected SourceLogReader<LogType> logReader;
	private int executeCount;
	
	public AnalyticsTask(String taskId, String taskName, String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		this.id = taskId;
		this.name = taskName;
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
	public String taskId(){
		return id;
	}
	public String taskName(){
		return name;
	}
	public int priority() {
		return priority;
	}

	@Override
	public JobResult doRun() {
		
		boolean isSuccess = false;
		String errorMessage = null;
		long startTime = System.currentTimeMillis();
		long baseTime = schedule.baseTime();
		String targetTime = Formatter.formatYYYYMMDD(new Date(baseTime));
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
			isSuccess = true;
			logger.info("### {}-{} Done!", taskId(), taskName());
		} catch (Throwable e) {
			logger.info("### {}-{} Error!", taskId(), taskName());
			logger.error("", e);
			StringWriter stringWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(stringWriter);
			e.printStackTrace(writer);
			errorMessage = stringWriter.toString();
		} finally {
			long endTime = System.currentTimeMillis();
			int duration = (int) (endTime - startTime);
			String durationString = Formatter.getFormatTime(duration);
			
			//Update task result
			AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<TaskResultMapper> mapperSession = analyticsDBService.getMapperSession(TaskResultMapper.class);
			
			try{ 
				TaskResultVO taskResultVO = new TaskResultVO(siteId, targetTime, new Timestamp(startTime), new Timestamp(endTime)
				, durationString, schedule.isScheduled() ? "SCHEDULE" : "MANUAL" , isSuccess ? "SUCCESS" : "FAIL", taskId(), taskName()
				, errorMessage != null ? errorMessage : "");
				
				TaskResultMapper mapper = mapperSession.getMapper();
				TaskResultVO vo = mapper.getEntry(siteId, targetTime, taskId());
				if(vo != null) {
					//update
					mapper.updateEntry(taskResultVO);
				} else {
					//insert
					mapper.putEntry(taskResultVO);
				}
			} catch (Exception e) {
				logger.error("error while insert task result log.", e);
			} finally {
				if(mapperSession != null) {
					mapperSession.closeSession();
				}
			}
			
			if(!isSuccess) {
				MapperSession<SystemErrorMapper> systemErrorMapperSession = analyticsDBService.getMapperSession(SystemErrorMapper.class);
				try {
					SystemErrorMapper mapper = systemErrorMapperSession.getMapper();
					SystemErrorVO vo = new SystemErrorVO(new Timestamp(endTime), errorMessage);
					mapper.putEntry(vo);
				} catch (Exception e) {
					logger.error("error while insert error log.", e);
				}finally{
					if(systemErrorMapperSession != null) {
						systemErrorMapperSession.closeSession();
					}
				}
			}
			
		}
		return new JobResult(isSuccess);
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

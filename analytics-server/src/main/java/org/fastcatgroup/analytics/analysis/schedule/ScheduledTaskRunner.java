package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis.task.AnalysisTask;
import org.fastcatgroup.analytics.control.JobExecutor;
import org.fastcatgroup.analytics.control.ResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스케줄링된 여러 task를 가지고 작업을 수행한다. 다음 스케줄에 실행될 작업을 리턴해준다.
 * */
public class ScheduledTaskRunner<LogType extends LogData> extends Thread {
	protected static Logger logger = LoggerFactory.getLogger(ScheduledTaskRunner.class);

	private JobExecutor jobExecutor;
	private Queue<AnalysisTask<LogType>> priorityJobQueue;
	private boolean isCanceled;

	public ScheduledTaskRunner(String name, JobExecutor jobExecutor) {
		super(name);
		this.jobExecutor = jobExecutor;
		this.priorityJobQueue = new PriorityQueue<AnalysisTask<LogType>>(5);
	}

	public void addTask(AnalysisTask<LogType> task) {
		priorityJobQueue.add(task);
	}

	public void cancel(){
		logger.info("[{}] cancel requested! > {}",  getClass().getSimpleName(), priorityJobQueue);
		isCanceled = true;
	}
	
	@Override
	public void run() {
		Iterator<AnalysisTask<LogType>> iterator = priorityJobQueue.iterator();
		while (iterator.hasNext()) {
			AnalysisTask<LogType> e = iterator.next();
			e.updateScheduleTimeByNow();
		}

		while (!isCanceled) {
			try {
				AnalysisTask<LogType> task = priorityJobQueue.poll();
				long timeToWait = task.getDelayedScheduledTime() - System.currentTimeMillis();
				if (timeToWait < 0) {
					// 이미 지났을 경우 바로실행한다. 스케쥴링된 모든 작업은 실행이 보장되어야한다.
					timeToWait = 0;
				}

				logger.info("Next task {} will run after waiting {}ms", task, timeToWait);
				if (timeToWait > 0) {
					synchronized (this) {
						wait(timeToWait);
					}
				}

				if (isCanceled) {
					break;
				}

				try {
					task.prepare();
					task.incrementExecution();
					logger.debug("{} run!", task);
					
					ResultFuture resultFuture = jobExecutor.offer(task);
					Object result = null;
					if (resultFuture == null) {
						// ignore
						logger.debug("Scheduled job {} is ignored.", task);
					} else {
						result = resultFuture.take();
						logger.debug("Scheduled Job Finished. {} > {}, execution[{}]", task, result, task.getExecuteCount());
					}
				} finally {
					// 실행한 job에 대해서는 반드시 다음 시간에 실행되도록 update time후 offer되도록 한다.
					task.updateScheduleTimeByNow();
					priorityJobQueue.offer(task);
				}

			} catch (InterruptedException e) {
				// InterruptedException 은 thread를 끝내게 한다.
				logger.info("[{}] is interrupted!", getClass().getSimpleName());
				break;
			} catch (Throwable t) {
				// 죽지마.
				logger.error("", t);
			}
		}

		if (isCanceled) {
			logger.info("[{}] is canceled >> {}", getClass().getSimpleName(), priorityJobQueue);
		}
	}

}

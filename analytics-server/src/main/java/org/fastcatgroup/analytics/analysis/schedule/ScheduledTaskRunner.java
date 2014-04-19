package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Date;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis.task.AnalyticsTask;
import org.fastcatgroup.analytics.control.JobExecutor;
import org.fastcatgroup.analytics.control.ResultFuture;
import org.fastcatgroup.analytics.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스케줄링된 여러 task를 가지고 작업을 수행한다. 다음 스케줄에 실행될 작업을 리턴해준다.
 * */
public class ScheduledTaskRunner extends Thread {
	protected static Logger logger = LoggerFactory.getLogger(ScheduledTaskRunner.class);

	private JobExecutor jobExecutor;
	private Environment environment;
	private Queue<AnalyticsTask> priorityJobQueue;
	private boolean isCanceled;

	public ScheduledTaskRunner(String name, JobExecutor jobExecutor, Environment environment) {
		super(name);
		this.jobExecutor = jobExecutor;
		this.environment = environment;
		this.priorityJobQueue = new PriorityQueue<AnalyticsTask>(5);
	}

	public void addTask(AnalyticsTask task) {
		task.setEnvironment(environment);
		priorityJobQueue.add(task);
	}

	public void cancel() {
		logger.info("[{}] cancel requested! > {}", getClass().getSimpleName(), priorityJobQueue);
		this.interrupt();
		isCanceled = true;
	}

	@Override
	public void run() {
		int size = priorityJobQueue.size();
		Iterator<AnalyticsTask> iterator = priorityJobQueue.iterator();
		while (iterator.hasNext()) {
			AnalyticsTask task = iterator.next();
			task.updateScheduleTimeByNow();
		}

		while (!isCanceled) {
			try {
				logger.debug("priorityJobQueue > {}", priorityJobQueue);
				AnalyticsTask task = priorityJobQueue.poll();
				if (task == null) {
					// 작업이 없으면 끝난다.
					break;
				}
				long timeToWait = task.getDelayedScheduledTime() - System.currentTimeMillis();
				if (timeToWait < 0) {
					// 이미 지났을 경우 바로실행한다. 스케쥴링된 모든 작업은 실행이 보장되어야한다.
					timeToWait = 0;
				}

				logger.info("Next task {} will run after waiting {}ms at {}", task, timeToWait, new Date(task.getDelayedScheduledTime()));
				if (timeToWait > 0) {
					synchronized (this) {
						wait(timeToWait);
					}
				}

				if (isCanceled) {
					break;
				}

				long st = System.currentTimeMillis();
				try {
					logger.info("===================================");
					logger.info("= {} RUN.", task.getClass().getSimpleName());
					logger.info("= {} ", task);
					logger.info("===================================");
					task.incrementExecution();

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
					logger.info("===================================");
					logger.info("= {} Done. time = {}s", task.getClass().getSimpleName(), (System.currentTimeMillis() - st) / 1000);
					logger.info("===================================");
					
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

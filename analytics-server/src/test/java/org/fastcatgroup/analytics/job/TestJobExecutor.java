package org.fastcatgroup.analytics.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.fastcatgroup.analytics.control.JobExecutor;
import org.fastcatgroup.analytics.control.ResultFuture;

public class TestJobExecutor implements JobExecutor {

	AtomicLong jobIdIncrement = new AtomicLong();
	Map<Long, ResultFuture> resultFutureMap = new ConcurrentHashMap<Long, ResultFuture>();

	@Override
	public ResultFuture offer(Job job) {

		long myJobId = jobIdIncrement.getAndIncrement();
		ResultFuture resultFuture = new ResultFuture(myJobId, resultFutureMap);
		resultFutureMap.put(myJobId, resultFuture);
		job.setJobExecutor(this);
		job.setId(myJobId);
		new Thread(job).start();
		return resultFuture;
	}

	@Override
	public void result(Job job, Object result, boolean isSuccess) {
		long jobId = job.getId();
		ResultFuture resultFuture = resultFutureMap.remove(jobId);

		if (resultFuture != null) {
			resultFuture.put(result, isSuccess);
		}
	}

	@Override
	public int runningJobSize() {
		return resultFutureMap.size();
	}

	@Override
	public int inQueueJobSize() {
		return resultFutureMap.size();
	}

}
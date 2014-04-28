package org.fastcatsearch.analytics.control;

import org.fastcatsearch.analytics.job.Job;

public interface JobExecutor {
	
	public ResultFuture offer(Job job);
		
//	public JobHandler jobHandler();
	
	public void result(Job job, Object result, boolean isSuccess);
	
	public int runningJobSize();
	public int inQueueJobSize();
}

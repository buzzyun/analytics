/*
 * Copyright (c) 2013 Websquared, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     swsong - initial API and implementation
 */

package org.fastcatgroup.analytics.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fastcatgroup.analytics.job.StatisticsJob;


public class StatisticsMutex {

	private Map<Long, String> jobIdMap;
	private Map<String, String> jobMonitorMap;
	
	public StatisticsMutex(){
		jobIdMap = new HashMap<Long, String>();
		jobMonitorMap = new HashMap<String, String>();
	}
	
	public Collection<String> getStatisticsRunningList(){
		return jobMonitorMap.values();
	}
	public synchronized void release(long jobId) {
		String collectionId = jobIdMap.remove(jobId);
		if(collectionId != null){
			unlock(collectionId);
		}
	}
	
	protected void unlock(String collectionId){
		jobMonitorMap.remove(collectionId);
	}

	public synchronized void access(long jobId, StatisticsJob job) {
		String collectionId = job.getStringArgs();
		if(jobMonitorMap.get(collectionId) != null){
			return;
		}
		
		jobIdMap.put(jobId, collectionId);
		jobMonitorMap.put(collectionId, job.getClass().getName());
	}

	public synchronized boolean isLocked(StatisticsJob job) {
		String collectionId = job.getStringArgs();
		if(jobMonitorMap.get(collectionId) != null){
			return true;
		}
		return false;
	}

}

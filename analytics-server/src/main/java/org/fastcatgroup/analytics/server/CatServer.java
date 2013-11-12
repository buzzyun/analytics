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

package org.fastcatgroup.analytics.server;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.db.DBService;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.http.HttpRequestService;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CatServer {

	private ServiceManager serviceManager;

	public static long startTime;
	public static CatServer instance;
	private static Logger logger;
	private boolean isRunning;

	private Thread shutdownHook;
	protected boolean keepAlive;

	private String serverHome;
	private static volatile Thread keepAliveThread;
	private static volatile CountDownLatch keepAliveLatch;

	public static void main(String... args) throws AnalyticsException {
		if (args.length < 1) {
			usage();
			return;
		}
		
		CatServer server = new CatServer(args[0]);
		if (server.load(args)) {
			server.start();
		}
	}

	public void setKeepAlive(boolean b) {
		keepAlive = b;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public static CatServer getInstance() {
		return instance;
	}

	public CatServer() {
	}

	public CatServer(String serverHome) {
		this.serverHome = serverHome;
	}

	public boolean load(){
		return load(null);
	}
	public boolean load(String[] args) {

		boolean isConfig = false;

		//load 파라미터는 없을수도 있다.
		if(args != null){
			for (int i = 0; i < args.length; i++) {
				if (isConfig) {
					isConfig = false;
				} else if (args[i].equals("-config")) {
					isConfig = true;
				} else if (args[i].equals("-help")) {
					usage();
					return false;
				}
			}
		}
		
		setKeepAlive(true);
		return true;

	}

	protected static void usage() {

        System.out.println
            ("usage: java "+ CatServer.class.getName()
             + " [ -help -config ]"
             + " {HomePath}");

    }

	public void start() throws AnalyticsException {
		// 초기화 및 서비스시작을 start로 옮김.
		// 초기화로직이 init에 존재할 경우, 관리도구에서 검색엔진을 재시작할때, init을 호출하지 않으므로, 초기화를 건너뛰게
		// 됨.
		instance = this;

		if (serverHome == null) {
			System.err.println("Warning! No argument for \"server.home\".");
			usage();
			System.exit(1);
		}

		File f = new File(serverHome);
		if (!f.exists()) {
			System.err.println("Warning! Path \"" + serverHome + "\" is not exist!");
			usage();
			System.exit(1);
		}

		Environment environment = new Environment(serverHome).init();
		this.serviceManager = new ServiceManager(environment);
		serviceManager.asSingleton();

		
		DBService dbService = serviceManager.createService("db", DBService.class);
		dbService.asSingleton();
		JobService jobService = serviceManager.createService("job", JobService.class);
		jobService.asSingleton();

		HttpRequestService httpRequestService = serviceManager.createService("http", HttpRequestService.class);
		
		logger = LoggerFactory.getLogger(CatServer.class);
		logger.info("ServerHome = {}", serverHome);
		try {
			dbService.start();
			jobService.start();
			httpRequestService.start();
			
		} catch (AnalyticsException e) {
			logger.error("CatServer 시작에 실패했습니다.", e);
			stop();
		}

		if (shutdownHook == null) {
			shutdownHook = new ServerShutdownHook();
		}
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		startTime = System.currentTimeMillis();

		logger.info("CatServer started!");
		isRunning = true;

		if (keepAlive) {
			setKeepAlive();
		}
	}

	private void setKeepAlive() {
		keepAliveLatch = new CountDownLatch(1);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				keepAliveLatch.countDown();
			}
		});

		keepAliveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					keepAliveLatch.await();
				} catch (InterruptedException e) {
					// bail out
				}
			}
		}, "CatServer[keepAlive]");
		keepAliveThread.setDaemon(false);
		keepAliveThread.start();

	}

	public void stop() throws AnalyticsException {

		//뜨는 도중 에러 발생시 NullPointerException 발생가능성.
		serviceManager.stopService(HttpRequestService.class);
		serviceManager.stopService(JobService.class);
		serviceManager.stopService(DBService.class);

		logger.info("CatServer shutdown!");
		isRunning = false;

		// Runtime.getRuntime().removeShutdownHook(shutdownHook);
	}

	public void close() throws AnalyticsException {
		serviceManager.closeService(HttpRequestService.class);
		serviceManager.closeService(JobService.class);
		serviceManager.closeService(DBService.class);
	}

	protected class ServerShutdownHook extends Thread {

		@Override
		public void run() {
			try {
				logger.info("Server Shutdown Requested!");
				CatServer.this.stop();
				// TODO shutdown 시 할일들을 적는다.
			} catch (Throwable ex) {
				logger.error("CatServer.shutdownHookFail", ex);
			} finally {
				logger.info("Server Shutdown Complete!");
			}
		}
	}

}

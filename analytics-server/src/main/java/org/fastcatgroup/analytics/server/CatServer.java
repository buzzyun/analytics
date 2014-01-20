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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.CountDownLatch;

import org.fastcatgroup.analytics.analysis.SearchStatisticsService;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.db.DBService;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.http.HttpRequestService;
import org.fastcatgroup.analytics.keyword.KeywordService;
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
	private FileLock fileLock;
	private File lockFile;
	
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

		lockFile = new File(serverHome, ".lock");
		try {
			FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel();
			fileLock = channel.tryLock();
		} catch (IOException e) {
			System.err.println("Error! Cannot create lock file \"" + lockFile.getAbsolutePath() + "\".");
			System.exit(1);
		}
		
		if(fileLock == null){
			System.err.println("Error! Another instance of CatServer may have already booted at home path = " + serverHome);
			System.exit(1);
		}else{
			System.out.println("File lock > "+ fileLock + ", "+lockFile.getAbsolutePath());
		}
		
		Environment environment = new Environment(serverHome).init();
		this.serviceManager = new ServiceManager(environment);
		serviceManager.asSingleton();

		
		DBService dbService = serviceManager.createService("db", DBService.class);
		dbService.asSingleton();
		JobService jobService = serviceManager.createService("job", JobService.class);
		jobService.asSingleton();

		HttpRequestService httpRequestService = serviceManager.createService("http", HttpRequestService.class);
		
		SearchStatisticsService searchStatisticsService = serviceManager.createService("statistics", SearchStatisticsService.class);
		KeywordService keywordService = serviceManager.createService("keyword", KeywordService.class);
		
		
		logger = LoggerFactory.getLogger(CatServer.class);
		logger.info("ServerHome = {}", serverHome);
		try {
			dbService.start();
			jobService.start();
			httpRequestService.start();
			
			searchStatisticsService.start();
			keywordService.start();
			/*
			 * 서비스가 모두 뜬 상태에서 후속작업.
			 */
			
			
			
			
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

		serviceManager.stopService(SearchStatisticsService.class);
		serviceManager.stopService(KeywordService.class);
		
		logger.info("CatServer shutdown!");
		isRunning = false;

		// Runtime.getRuntime().removeShutdownHook(shutdownHook);
	}

	public void close() throws AnalyticsException {
		serviceManager.closeService(HttpRequestService.class);
		serviceManager.closeService(JobService.class);
		serviceManager.closeService(DBService.class);
		
		serviceManager.closeService(SearchStatisticsService.class);
		serviceManager.closeService(KeywordService.class);
		
		if(fileLock != null){
			try {
				fileLock.release();
				logger.info("CatServer Lock Release! {}", fileLock);
			} catch (IOException e) {
				logger.error("", e);
			}
			
			try {
				fileLock.channel().close();
			} catch (Exception e) {
				logger.error("", e);
			}
			
			try {
				lockFile.delete();
				logger.info("Remove .lock file >> {}", lockFile.getAbsolutePath());
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	protected class ServerShutdownHook extends Thread {

		@Override
		public void run() {
			try {
				logger.info("Server Shutdown Requested!");
				CatServer.this.stop();
				CatServer.this.close();
			} catch (Throwable ex) {
				logger.error("CatServer.shutdownHookFail", ex);
			} finally {
				logger.info("Server Shutdown Complete!");
			}
		}
	}

}

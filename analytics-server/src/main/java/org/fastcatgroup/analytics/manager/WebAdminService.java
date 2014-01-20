package org.fastcatgroup.analytics.manager;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.service.AbstractService;
import org.fastcatgroup.analytics.service.ServiceManager;

public class WebAdminService extends AbstractService {

	private int SERVER_PORT;
	private Server server;

	public WebAdminService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		
		SERVER_PORT = 8080;
		File webDir = environment.filePaths().file("web");//,"analytics-web.war"
		
		File[] files = webDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("analytics-web") && name.endsWith(".war");
			}
		});
		
		File dir = new File("/Users/swsong/git-stable2/analytics/analytics-web/src/main/webapp");
		File warFile = null;
		if(files != null && files.length > 0){
			warFile = files[0];
		}else{
			if(dir.exists()){
				warFile = dir;
			}else{
				return false;
			}
		}
		
		String warFilePath = warFile.getAbsolutePath();
		logger.debug("warFilePath >> {}", warFilePath);
		
		server = new Server(SERVER_PORT);
		File tempDir = environment.filePaths().file("web", "temp");
		tempDir.mkdir();
		
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/analytics");
		webapp.setWar(warFilePath);
		//webapp.setTempDirectory(tempDir);
		webapp.setParentLoaderPriority(true);
		server.setHandler(webapp);
		
		try {
			// stop을 명령하면 즉시 중지되도록.
			server.setStopAtShutdown(true);
			server.start();
			logger.info("WebAdmin jetty[{}] port[{}]", server.getVersion(), SERVER_PORT);
		} catch (Exception e) {
			throw new AnalyticsException(SERVER_PORT + " PORT로 웹서버 시작중 에러발생. ", e);

		}
		return true;
	}

	@Override
	protected boolean doStop() throws AnalyticsException {
		try {
			if(server != null && server.isStarted()){
				server.stop();
			}else{
				logger.warn("Web admin server is not started.");
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		server = null;
		return true;
	}

}

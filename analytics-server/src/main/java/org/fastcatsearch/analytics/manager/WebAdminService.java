package org.fastcatsearch.analytics.manager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.service.AbstractService;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.web.controller.AbstractController;

import java.io.File;
import java.io.FilenameFilter;

public class WebAdminService extends AbstractService {

	private int webPort;
	private Server server;

	public WebAdminService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		
		webPort = settings.getInt("admin.web.port", 8080);
		File webDir = environment.filePaths().file("web");//,"analytics-web.war"
		
		File[] files = webDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("analytics-web") && name.endsWith(".war");
			}
		});
		
		File warFile = null;
		if(files != null && files.length > 0){
			warFile = files[0];
            logger.info("Use WepApp war = {}", warFile.getAbsolutePath());
		}else{
			//war파일을 찾지못했다면 개발환경 경로를 확인한다.
			File devWebApp = new File("analytics-web/src/main/webapp");
            logger.info("Use WepApp dir = {}", devWebApp.getAbsolutePath());
			if(devWebApp.exists()){
				warFile = devWebApp;
			}else{
                logger.error("Cannot find webapp");
				return false;
			}
		}
		
		String warFilePath = warFile.getAbsolutePath();
		
		server = new Server(webPort);
		File tempDir = environment.filePaths().file("web", "temp");
		logger.info("Web application tempDir > {}", tempDir.getAbsolutePath());
		tempDir.delete();
//		tempDir.mkdir();
		
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/analytics");
		webapp.setWar(warFilePath);
		webapp.setTempDirectory(tempDir);
		webapp.setParentLoaderPriority(true);
		webapp.setAttribute(AbstractController.ENVIRONMENT, environment);
		server.setHandler(webapp);
		
		try {
			// stop을 명령하면 즉시 중지되도록.
			server.setStopAtShutdown(true);
			server.start();
			logger.info("WebAdmin jetty[{}] port[{}] webapp[{}]", Server.getVersion(), webPort, warFilePath);
		} catch (Exception e) {
			throw new AnalyticsException(webPort + " PORT로 웹서버 시작중 에러발생. ", e);

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

package org.fastcatsearch.analytics.http;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.fastcatsearch.analytics.common.ThreadPoolFactory;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.http.action.HttpAction;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.service.AbstractService;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ClassScanner;
import org.fastcatsearch.analytics.util.DynamicClassLoader;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpRequestService extends AbstractService implements HttpServerAdapter {

	private HttpTransportModule transportModule;
	private HttpServiceController serviceController;

	public HttpRequestService(Environment environment, Settings settings, ServiceManager serviceManager) throws AnalyticsException {
		super(environment, settings, serviceManager);
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		int servicePort = environment.settingManager().getSystemSettings().getInt("servicePort");
		transportModule = new HttpTransportModule(environment, settings, servicePort);
		transportModule.httpServerAdapter(this);
		ExecutorService executorService = ThreadPoolFactory.newCachedDaemonThreadPool("http-execute-pool", 300);
		serviceController = new HttpServiceController(executorService);
		if (!transportModule.load()) {
			throw new AnalyticsException("can not load transport module!");
		}

		Map<String, HttpAction> actionMap = new HashMap<String, HttpAction>();
		String[] actionBasePackageList = settings.getStringArray("action-base-package", ",");
		logger.debug("actionBasePackageList >> {}", actionBasePackageList);
		if (actionBasePackageList != null) {
			scanActions(actionMap, actionBasePackageList);
		}

		serviceController.setActionMap(actionMap);
		return true;
	}

//	private void scanActions(Map<String, HttpAction> actionMap, String[] actionBasePackageList) {
//		for (String actionBasePackage : actionBasePackageList) {
//			scanActions(actionMap, actionBasePackage);
//		}
//	}
//
//	private void addDirectory(Set<File> set, File d) {
//		File[] files = d.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isDirectory()) {
//				addDirectory(set, files[i]);
//			} else {
//				set.add(files[i]);
//			}
//		}
//	}
//
//	// 하위패키지까지 모두 포함되도록 한다.
//	private void scanActions(Map<String, HttpAction> actionMap, String actionBasePackage) {
//		String path = actionBasePackage.replace(".", "/");
//		if (!path.endsWith("/")) {
//			path = path + "/";
//		}
//		try {
//			String findPath = path;// +"**/*.class";
//			Enumeration<URL> em = DynamicClassLoader.getResources(findPath);
//			logger.debug("findPath >> {}, {}", findPath, em);
//			while (em.hasMoreElements()) {
//				String urlstr = em.nextElement().toString();
//				logger.debug("urlstr >> {}", urlstr);
//				if (urlstr.startsWith("jar:file:")) {
//					String jpath = urlstr.substring(9);
//					int st = jpath.indexOf("!/");
//					String jarPath = jpath.substring(0, st);
//					String entryPath = jpath.substring(st + 2);
//					JarFile jf = new JarFile(jarPath);
//					Enumeration<JarEntry> jee = jf.entries();
//					while (jee.hasMoreElements()) {
//						JarEntry je = jee.nextElement();
//						String ename = je.getName();
//						if (ename.startsWith(entryPath)) {
//							registerAction(actionMap, ename, true);
//						}
//					}
//				} else if (urlstr.startsWith("file:")) {
//					// logger.debug("urlstr >> {}", urlstr);
//					String rootPath = urlstr.substring(5);
//					int prefixLength = rootPath.indexOf(path);
//					File file = new File(rootPath);
//
//					Set<File> actionFileSet = new HashSet<File>();
//					if (file.isDirectory()) {
//						addDirectory(actionFileSet, file);
//					} else {
//						actionFileSet.add(file);
//					}
//					for (File f : actionFileSet) {
//						String classPath = f.toURI().toURL().toString().substring(5).substring(prefixLength);
//						// logger.debug("file >> {}", classPath);
//						registerAction(actionMap, classPath, true);
//					}
//				}
//			}
//		} catch (IOException e) {
//			logger.error("action load error!", e);
//		}
//	}
	
	private void scanActions(Map<String, HttpAction> actionMap, String[] actionBasePackageList) {
		for (String actionBasePackage : actionBasePackageList) {
			scanActions(actionMap, actionBasePackage);
		}
	}

	// 하위패키지까지 모두 포함되도록 한다.
	private void scanActions(final Map<String, HttpAction> actionMap, String actionBasePackage) {
		ClassScanner<HttpAction> scanner = new ClassScanner<HttpAction>() {
			@Override
			public HttpAction done(String ename, String pkg, Object param) {
				registerAction(actionMap, ename);
				return null;
			}
		};
		scanner.scanClass(actionBasePackage, null);
	}

	public void registerAction(String className, String pathPrefix) {
		registerAction(serviceController.getActionMap(), className, pathPrefix);
	}

//	private void registerAction(Map<String, HttpAction> actionMap, String className, boolean isFile) {
//		registerAction(actionMap, className, null, isFile);
//	}
//
//	private void registerAction(Map<String, HttpAction> actionMap, String className, String pathPrefix, boolean isFile) {
//		if (className == null) {
//			logger.warn("Cannot register action class name >> {} : {}", className, pathPrefix);
//			return;
//		}
//		if (isFile) {
//			if (className.endsWith(".class")) {
//				className = className.substring(0, className.length() - 6);
//				className = className.replaceAll("/", ".");
//			} else {
//				// file인데 class로 끝나지 않으면 무시.
//				return;
//			}
//		}
//
//		try {
//			Class<?> actionClass = DynamicClassLoader.loadClass(className);
//			logger.debug("className > {} => {}", className, actionClass);
//			// actionClass 가 serviceAction을 상속받은 경우만 등록.
//			if (actionClass != null) {
//				if (ServiceAction.class.isAssignableFrom(actionClass)) {
//					HttpAction actionObj = null;
//					ActionMapping actionMapping = actionClass.getAnnotation(ActionMapping.class);
//					// annotation이 존재할 경우만 사용.
//					if (actionMapping != null) {
//						String path = actionMapping.value();
//						if (pathPrefix != null) {
//							path = pathPrefix + path;
//						}
//						ActionMethod[] method = actionMapping.method();
//						actionObj = (HttpAction) actionClass.newInstance();
//						if (actionObj != null) {
//							actionObj.setMethod(method);
//							actionObj.setEnvironement(environment);
//
//							logger.debug("ACTION path={}, action={}, method={}", path, actionObj, method);
//
//							actionMap.put(path, actionObj);
//
//						}
//
//					}
//				}
//			}
//		} catch (InstantiationException e) {
//			logger.error("action load error!", e);
//		} catch (IllegalAccessException e) {
//			logger.error("action load error!", e);
//		}
//	}
	
	private void registerAction(Map<String, HttpAction> actionMap, String className) {
		registerAction(actionMap, className, null);
	}
	private void registerAction(Map<String, HttpAction> actionMap, String className, String pathPrefix) {
		if(className == null){
			logger.warn("Cannot register action class name >> {} : {}", className, pathPrefix);
			return;
		}

		try {
			Class<?> actionClass = DynamicClassLoader.loadClass(className);
			// logger.debug("className > {} => {}",className , actionClass);
			// actionClass 가 serviceAction을 상속받은 경우만 등록.
			if (actionClass != null) {
				if (ServiceAction.class.isAssignableFrom(actionClass)) {
					HttpAction actionObj = null;
					ActionMapping actionMapping = actionClass.getAnnotation(ActionMapping.class);
					// annotation이 존재할 경우만 사용.
					if (actionMapping != null) {
						String path = actionMapping.value();
						if(pathPrefix != null){
							path = pathPrefix + path;
						}
						ActionMethod[] method = actionMapping.method();
						actionObj = (HttpAction) actionClass.newInstance();
						if (actionObj != null) {
							actionObj.setMethod(method);
							actionObj.setEnvironement(environment);
							
							logger.debug("ACTION path={}, action={}, method={}", path, actionObj, method);	

							actionMap.put(path, actionObj);

						}

					}
				}
			}
		} catch (InstantiationException e) {
			logger.error("action load error!", e);
		} catch (IllegalAccessException e) {
			logger.error("action load error!", e);
		}
	}

	@Override
	protected boolean doStop() throws AnalyticsException {
		transportModule.doUnload();
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		serviceController = null;
		transportModule = null;
		return true;
	}

	@Override
	public void dispatchRequest(HttpRequest request, HttpChannel channel) {
		serviceController.dispatchRequest(request, channel);
	}

}

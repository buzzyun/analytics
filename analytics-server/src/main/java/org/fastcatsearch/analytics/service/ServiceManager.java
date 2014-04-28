package org.fastcatsearch.analytics.service;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {
	private final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	
	private static ServiceManager instance;
	private Environment environment;
	private Map<Class<?>, AbstractService> serviceMap;
	
	public static ServiceManager getInstance(){
		return instance;
	}
	
	public void asSingleton(){
		instance = this;
	}
	
	public ServiceManager(Environment environment){
		this.environment = environment;
		serviceMap = new ConcurrentHashMap<Class<?>, AbstractService>();
	}
	
	public <T extends AbstractService> T createService(String settingName, Class<T> serviceClass){
		try {
			Constructor<T> construct = serviceClass.getConstructor(Environment.class, Settings.class, ServiceManager.class);
			T t = construct.newInstance(environment, environment.settingManager().getSystemSettings().getSubSettings(settingName), this);
			serviceMap.put(serviceClass, t);
			return t;
		} catch (Exception e) {
			logger.error("can not make instance of class <"+serviceClass.getName()+">, {}", e);
		}
		return null;
	}
	
	
	public <T extends AbstractService> T getService(Class<T> serviceClass) {
		return (T) serviceMap.get(serviceClass);
	}
	
	public <T extends AbstractService> boolean stopService(Class<T> serviceClass) throws AnalyticsException {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.stop();
		}else{
			return false;
		}
	}
	
	public <T extends AbstractService> boolean closeService(Class<T> serviceClass) throws AnalyticsException {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.close();
		}else{
			return false;
		}
	}

}

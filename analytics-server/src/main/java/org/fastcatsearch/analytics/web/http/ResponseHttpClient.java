package org.fastcatsearch.analytics.web.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHttpClient {
	private static Logger logger = LoggerFactory.getLogger(ResponseHttpClient.class);

	private CloseableHttpClient httpclient;
	private String urlPrefix;
	private boolean isActive;

	private static final ResponseHandler<JSONObject> jsonResponseHandler = new JSONResponseHandler();
	private static final ResponseHandler<Document> xmlResponseHandler = new XMLResponseHandler();
	private static final ResponseHandler<String> textResponseHandler = new TextResponseHandler();
	
	public ResponseHttpClient(String host) {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(100);
		BasicCookieStore cookieStore = new BasicCookieStore();
		httpclient = HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore).build();

		urlPrefix = "http://" + host;
		isActive = true;

	}

	public boolean isActive() {
		return isActive;
	}

	private String getURL(String uri) {
		return urlPrefix + uri;
	}

	public GetMethod httpGet(String uri) {
		return new GetMethod(this, getURL(uri));
	}

	public PostMethod httpPost(String uri) {
		return new PostMethod(this, getURL(uri));
	}

	public void close() {
		if (httpclient != null) {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("error close httpclient", e);
			}
			httpclient = null;
		}
		isActive = false;
	}

	public static abstract class AbstractMethod {
		protected ResponseHttpClient responseHttpClient;
		protected String url;
		
		public AbstractMethod(ResponseHttpClient responseHttpClient, String url) {
			this.responseHttpClient = responseHttpClient;
			this.url = url;
		}
//		public abstract JSONObject requestJSON() throws ClientProtocolException, IOException;
//		public abstract Document requestXML() throws ClientProtocolException, IOException;
		public abstract AbstractMethod addParameter(String key, String value);
		protected abstract HttpUriRequest getHttpRequest();
		
		public AbstractMethod addParameterString(String parameterString){
			String[] keyValues = parameterString.split("&");
			for(String keyValue : keyValues){
				keyValue = keyValue.trim();
				if(keyValue.length() > 0){
					String[] list = keyValue.split("=");
					if(list.length == 2){
						addParameter(list[0], list[1]);
					}
				}
			}
			
			return this;
		}
		
		public JSONObject requestJSON() throws ClientProtocolException, IOException, Http404Error {
			try {
				return responseHttpClient.httpclient.execute(getHttpRequest(), jsonResponseHandler);
			}catch(SocketException e){
				logger.debug("httpclient socket error! >> {}", e.getMessage());
				responseHttpClient.close();
			}catch(ClientProtocolException e){
				if(e.getCause() instanceof Http404Error){
					throw (Http404Error) e.getCause();
				}
				logger.debug("httpclient error! >> {}, {}", e.getMessage(), e.getCause());
				throw e;
			}
			return null;
		}
		
		public Document requestXML() throws ClientProtocolException, IOException, Http404Error {
			try {
				return responseHttpClient.httpclient.execute(getHttpRequest(), xmlResponseHandler);
			}catch(SocketException e){
				logger.debug("httpclient socket error! >> {}", e.getMessage());
				responseHttpClient.close();
			}catch(ClientProtocolException e){
				if(e.getCause() instanceof Http404Error){
					throw (Http404Error) e.getCause();
				}
				logger.debug("httpclient error! >> {}, {}", e.getMessage(), e.getCause());
				throw e;
			}
			return null;
		}
		
		public String requestText() throws ClientProtocolException, IOException, Http404Error {
			try {
				return responseHttpClient.httpclient.execute(getHttpRequest(), textResponseHandler);
			}catch(SocketException e){
				logger.debug("httpclient socket error! >> {}", e.getMessage());
				responseHttpClient.close();
			}catch(ClientProtocolException e){
				if(e.getCause() instanceof Http404Error){
					throw (Http404Error) e.getCause();
				}
				logger.debug("httpclient error! >> {}", e.getMessage());
				throw e;
			}
			return null;
		}
	}
	
	public static class GetMethod extends AbstractMethod {
		
		private String queryString;
		
		public GetMethod(ResponseHttpClient responseHttpClient, String url) {
			super(responseHttpClient, url);
		}

		protected HttpGet getHttpRequest(){
			if(queryString != null){
				return new HttpGet(url + "?" + queryString);
			}else{
				return new HttpGet(url);
			}
		}
		
		@Override
		public GetMethod addParameter(String key, String value) {
			try {
				if(value == null){
					value = "";
				}
				
				if(queryString == null){
					queryString = "";
				}else{
					queryString += "&";
				}
				queryString += (key + "=" + URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}

			return this;
		}

	}

	public static class PostMethod extends AbstractMethod {
		private List<NameValuePair> nvps;

		public PostMethod(ResponseHttpClient responseHttpClient, String url) {
			super(responseHttpClient, url);
		}

		protected HttpPost getHttpRequest(){
			HttpPost httpost = new HttpPost(url);
			if (nvps != null) {
				httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			}
			return httpost;
		}

		@Override
		public PostMethod addParameter(String key, String value) {
			if (nvps == null) {
				nvps = new ArrayList<NameValuePair>();
			}

			nvps.add(new BasicNameValuePair(key, value));

			return this;
		}
		
		public String getParameter(String key){
			if(nvps != null){
				for(NameValuePair pair : nvps){
					if(pair.getName().equalsIgnoreCase(key)){
						return pair.getValue();
					}
				}
			}
			
			return null;
		}

	}
}

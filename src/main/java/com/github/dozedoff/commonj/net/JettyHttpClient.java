package com.github.dozedoff.commonj.net;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyHttpClient implements IHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(JettyHttpClient.class);
	
	private HttpClient httpClient;
	
	private long timeoutInMilliseconds = 10000;
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
	
	public JettyHttpClient() throws Exception {
		httpClient = new HttpClient();
		httpClient.setIdleTimeout(timeoutInMilliseconds);
		httpClient.setConnectTimeout(timeoutInMilliseconds);
		httpClient.start();
	}
	
	private ContentResponse getHeaderResponse(URL url) throws InterruptedException, TimeoutException, ExecutionException {
		return getDefaultRequest(url).method(HttpMethod.HEAD).send();
	}
	
	private Request getDefaultRequest(URL url) {
		return httpClient.newRequest(url.toString()).agent(userAgent)
				.timeout(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
	}

	@Override
	public long getLenght(URL url) throws InterruptedException, TimeoutException, ExecutionException {
			ContentResponse response = getHeaderResponse(url);
			return response.getHeaders().getLongField("content-length");
	}

	@Override
	public Map<String, List<String>> getHeader(URL url) throws InterruptedException, TimeoutException, ExecutionException {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();

		ContentResponse response = getHeaderResponse(url);
		HttpFields fields = response.getHeaders();
		Set<String> fieldKeys = fields.getFieldNamesCollection();

		for (String key : fieldKeys) {
			headers.put(key, fields.getValuesList(key));
		}

		return headers;
	}

	@Override
	public byte[] getDataRange(URL url, int start, long l) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException {
		ContentResponse response = getDefaultRequest(url).method(HttpMethod.GET).header("Range", "bytes=" + start + "-" + l).send();
		
		if (response.getStatus() != Response.SC_PARTIAL_CONTENT) {
			throw new PageLoadException(response.getReason(), response.getStatus());
		}
		
		return response.getContent();
	}

	@Override
	public byte[] getData(URL url) throws PageLoadException, InterruptedException, TimeoutException, ExecutionException {
		Request request = getDefaultRequest(url).method(HttpMethod.GET);
		ContentResponse response;
		
		response = request.send();
		
		if(response.getStatus() != Response.SC_OK) {
			throw new PageLoadException(response.getReason(), response.getStatus());
		}
		
		return response.getContent();
	}

	@Override
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String getUserAgent() {
		return this.userAgent;
	}

	@Override
	public void setTimeout(long value, TimeUnit timeUnit) {
		this.timeoutInMilliseconds = TimeUnit.MILLISECONDS.convert(value, timeUnit);
		httpClient.setIdleTimeout(timeoutInMilliseconds);
		httpClient.setConnectTimeout(timeoutInMilliseconds);
	}

	@Override
	public long getTimeoutInMilliseconds() {
		return this.timeoutInMilliseconds;
	}
}

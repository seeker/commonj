/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IHttpClient} implentation based on the Jetty Http-Client.
 * 
 * @author Nicholas Wright
 *
 */
public class JettyHttpClient implements IHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(JettyHttpClient.class);

	private static final int RESPONSE_OK = 200;
	private static final int RESPONSE_PARTIAL_CONTENT = 206;
	
	private HttpClient httpClient;
	
	private long timeoutInMilliseconds = 10000;
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";
	
	/**
	 * Create a new client with 10 second timeout and Firefox user agent.
	 * 
	 * @throws Exception
	 *             if there is a error starting the client
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLenght(URL url) throws InterruptedException, TimeoutException, ExecutionException {
			ContentResponse response = getHeaderResponse(url);
			return response.getHeaders().getLongField("content-length");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, List<String>> getHeader(URL url) throws InterruptedException, TimeoutException, ExecutionException {
		Map<String, List<String>> headers = new HashMap<String, List<String>>();

		ContentResponse response = getHeaderResponse(url);
		HttpFields fields = response.getHeaders();
		Set<String> fieldKeys = (Set<String>) fields.getFieldNamesCollection();

		for (String key : fieldKeys) {
			headers.put(key, fields.getValuesList(key));
		}

		return headers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getDataRange(URL url, int start, long l) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException {
		ContentResponse response = getDefaultRequest(url).method(HttpMethod.GET).header("Range", "bytes=" + start + "-" + l).send();
		
		if (response.getStatus() != RESPONSE_PARTIAL_CONTENT) {
			throw new PageLoadException(response.getReason(), response.getStatus());
		}
		
		return response.getContent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getData(URL url) throws PageLoadException, InterruptedException, TimeoutException, ExecutionException {
		Request request = getDefaultRequest(url).method(HttpMethod.GET);
		ContentResponse response;
		
		response = request.send();
		
		if(response.getStatus() != RESPONSE_OK) {
			throw new PageLoadException(response.getReason(), response.getStatus());
		}
		
		return response.getContent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserAgent(String userAgent) throws IllegalArgumentException {
		if (userAgent == null) {
			throw new IllegalArgumentException("User Agent cannot be null");
		}

		this.userAgent = userAgent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTimeout(long value, TimeUnit timeUnit) throws IllegalArgumentException{
		if(value < 0){
			throw new IllegalArgumentException("Timeout value must be 0 or greater");
		}
		
		this.timeoutInMilliseconds = TimeUnit.MILLISECONDS.convert(value, timeUnit);
		httpClient.setIdleTimeout(timeoutInMilliseconds);
		httpClient.setConnectTimeout(timeoutInMilliseconds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeoutInMilliseconds() {
		return this.timeoutInMilliseconds;
	}
}

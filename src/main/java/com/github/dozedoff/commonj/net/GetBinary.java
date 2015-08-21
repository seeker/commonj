/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
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

/**
 * Class for downloading binary data from the Internet.
 */
public class GetBinary implements DataDownloader {
	private int maxRetry = 3;
	private int readTimeoutInMilli = 10000;
	private final static Logger logger = LoggerFactory.getLogger(GetBinary.class);
	private final static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0";

	private HttpClient httpClient;

	public GetBinary() {
		httpClient = new HttpClient();
		httpClient.setConnectTimeout(readTimeoutInMilli);
		
		try {
			httpClient.start();
		} catch (Exception e) {
			throw new RuntimeException("Failed to start HttpClient");
		}
	}

	public Long getLenght(URL url) throws IOException, PageLoadException {
		try {
			ContentResponse response = getHeaderResponse(url);
			return response.getHeaders().getLongField("content-length");
		} catch (TimeoutException toe) {
			throw new SocketTimeoutException(toe.getMessage()); // re-throw exception for API compatibility
		} catch (InterruptedException | ExecutionException e) {
			return -1L;
		}
	}

	public Map<String, List<String>> getHeader(URL url) throws IOException {
			Map<String, List<String>> headers = new HashMap<String, List<String>>();
		
			try {
				ContentResponse response = getHeaderResponse(url);
				HttpFields fields = response.getHeaders();
				Set<String> fieldKeys = fields.getFieldNamesCollection();
				
				for (String key : fieldKeys) {
					headers.put(key, fields.getValuesList(key));
				}
			} catch (InterruptedException | TimeoutException| ExecutionException e) {
				logger.error("Failed to get headers due to {}", e);
			}
			
			return headers;
	}

	public byte[] getRange(URL url, int start, long l) throws IOException, PageLoadException {
		try {
			ContentResponse response = getDefaultRequest(url).method(HttpMethod.GET).header("Range", "bytes=" + start + "-" + l).send();
			
			if (response.getStatus() != Response.SC_PARTIAL_CONTENT) {
				throw new PageLoadException(response.getReason(), response.getStatus());
			}
			
			return response.getContent();
			
		} catch (TimeoutException toe) {
			throw new SocketTimeoutException(toe.getMessage());
		} catch (InterruptedException | ExecutionException e1) {
			throw new PageLoadException(e1.getMessage());
		}
	}

	public byte[] getViaHttp(String url) throws PageLoadException, IOException {
		return getViaHttp(new URL(url));
	}

	public byte[] getViaHttp(URL url) throws IOException, PageLoadException {
		Request request = getDefaultRequest(url).method(HttpMethod.GET);
		ContentResponse response;
		
		try {
			response = request.send();
			
			if(response.getStatus() != Response.SC_OK) {
				throw new PageLoadException(response.getReason(), response.getStatus());
			}
			
			return response.getContent();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			// TODO replace this legacy code with a better solution
			long contentLenght = getLenght(url);
			ByteBuffer dataBuffer = ByteBuffer.allocate((int)contentLenght);
			
			retry(url, dataBuffer, contentLenght);
			
			dataBuffer.flip();
			byte[] varBuffer = new byte[dataBuffer.limit()];
			dataBuffer.get(varBuffer);
			dataBuffer.clear();
			return varBuffer;
		}
	}
	
	private ContentResponse getHeaderResponse(URL url) throws InterruptedException, TimeoutException, ExecutionException {
		return getDefaultRequest(url).method(HttpMethod.HEAD).send();
	}
	
	private Request getDefaultRequest(URL url) {
		return httpClient.newRequest(url.toString()).agent(userAgent)
				.timeout(readTimeoutInMilli, TimeUnit.MILLISECONDS);
	}

	//TODO change this back to private once refactoring is done
	protected boolean retry(URL url, ByteBuffer buffer, long contentLength) throws PageLoadException, IOException {
		int failCount = 0;

		while (failCount < maxRetry) {
			Object[] logData = { url, maxRetry - failCount, buffer.position(), buffer.limit() };
			logger.info("Retrying {}, {} tries left, got {} of {} bytes", logData);

			try {
				failCount++;
				byte[] data = getRange(url, buffer.position(), contentLength);
				buffer.put(data);
				failCount--;
			} catch (PageLoadException ple) {
				logger.warn("Failed to load page with {} during retry for {}", ple.getMessage(), url);
			} catch (IOException ioe) {
				logger.warn("Connection issue ({}) during retry for {}", ioe.getMessage(), url);
			}

			if (!buffer.hasRemaining()) {
				// all data received
				break;
			}

			if (failCount >= maxRetry) {
				logger.warn("Out of retries for {}, giving up...", url);
				return false;
			}
		}

		return true;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public boolean setReadTimeout(int milliSeconds) {
		if (milliSeconds >= 0) {
			this.readTimeoutInMilli = milliSeconds;
			httpClient.setConnectTimeout(milliSeconds);
			httpClient.setIdleTimeout(milliSeconds);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public byte[] download(URL url) throws PageLoadException, IOException {
		return getViaHttp(url);
	}
}

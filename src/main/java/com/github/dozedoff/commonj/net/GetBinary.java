/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;

/**
 * Class for downloading binary data from the Internet.
 */
public class GetBinary implements DataDownloader {
	private int maxRetry = 3;
	private int readTimeoutInMilli = 10000;

	private IHttpClient httpClient;
	/**
	 * Use {@link GetBinary#GetBinary(HttpClient)} instead.
	 */
	@Deprecated
	public GetBinary() {
		try {
			this.httpClient = new JettyHttpClient();
			httpClient.setTimeout(readTimeoutInMilli, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new RuntimeException("Failed to start HttpClient");
		}
	}
	
	public GetBinary(IHttpClient httpClient) throws Exception {
		this.httpClient = httpClient;
		httpClient.setTimeout(readTimeoutInMilli, TimeUnit.MILLISECONDS);
	}

	public Long getLenght(URL url) throws IOException, PageLoadException {
		try {
			return httpClient.getLenght(url);
		} catch (TimeoutException toe) {
			throw new SocketTimeoutException(toe.getMessage()); // re-throw exception for API compatibility
		} catch (InterruptedException | ExecutionException e) {
			return -1L;
		}
	}

	public Map<String, List<String>> getHeader(URL url) throws IOException {
			try {
				return httpClient.getHeader(url);
			} catch (InterruptedException | TimeoutException | ExecutionException e) {
				return new HashMap<String, List<String>>();
			}
	}

	public byte[] getRange(URL url, int start, long l) throws IOException, PageLoadException {
		try {
			return httpClient.getDataRange(url, start, l);
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
		try {
			return httpClient.getData(url);
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			DownloadWithRetry downloadRetry = new DownloadWithRetry(this);
			return downloadRetry.download(url, maxRetry);
		}
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
			httpClient.setTimeout(milliSeconds, TimeUnit.MILLISECONDS);
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

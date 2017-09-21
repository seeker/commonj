/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface IHttpClient {

	public abstract long getLenght(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	public abstract Map<String, List<String>> getHeader(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	public abstract byte[] getDataRange(URL url, int start, long l) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	public abstract byte[] getData(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;
	
	public abstract void setUserAgent(String userAgent) throws IllegalArgumentException;
	
	public abstract String getUserAgent();
	
	public abstract void setTimeout(long value, TimeUnit timeUnit) throws IllegalArgumentException;
	
	public abstract long getTimeoutInMilliseconds();
}
/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Interface for http clients.
 * 
 * @author Nicholas Wright
 *
 */
public interface IHttpClient {

	/**
	 * Get the size of the resource.
	 * 
	 * @param url
	 *            to check
	 * @return the size of the resource
	 * @throws InterruptedException
	 *             if the request was interrupted
	 * @throws TimeoutException
	 *             if the request timed out
	 * @throws ExecutionException
	 *             usually a wrapped timeout exception under windows 10
	 * @throws PageLoadException
	 *             if there was an error accessing the resource
	 */
	public abstract long getLenght(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	/**
	 * Get the headers for the resource.
	 * 
	 * @param url
	 *            to fetch headers for
	 * @return a map containing the header fields and associated values
	 * @throws InterruptedException
	 *             if the request was interrupted
	 * @throws TimeoutException
	 *             if the request timed out
	 * @throws ExecutionException
	 *             usually a wrapped timeout exception under windows 10
	 * @throws PageLoadException
	 *             if there was an error accessing the resource
	 */
	public abstract Map<String, List<String>> getHeader(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	/**
	 * Get a range of bytes from the resource.
	 * 
	 * @param url
	 *            to load
	 * @param start
	 *            offset to start loading from
	 * @param l
	 *            number of bytes to read from the offset
	 * @return bytes read from the specified range
	 * @throws InterruptedException
	 *             if the request was interrupted
	 * @throws TimeoutException
	 *             if the request timed out
	 * @throws ExecutionException
	 *             usually a wrapped timeout exception under windows 10
	 * @throws PageLoadException
	 *             if there was an error accessing the resource
	 */
	public abstract byte[] getDataRange(URL url, int start, long l) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	/**
	 * Get the entire resource.
	 * 
	 * @param url
	 *            to load
	 * @return the resource as a byte array
	 * @throws InterruptedException
	 *             if the request was interrupted
	 * @throws TimeoutException
	 *             if the request timed out
	 * @throws ExecutionException
	 *             usually a wrapped timeout exception under windows 10
	 * @throws PageLoadException
	 *             if there was an error accessing the resource
	 */
	public abstract byte[] getData(URL url) throws InterruptedException, TimeoutException, ExecutionException, PageLoadException;

	/**
	 * Set the user agent for the client.
	 * 
	 * @throws IllegalArgumentException
	 *             if the user agent is invalid
	 */
	public abstract void setUserAgent(String userAgent) throws IllegalArgumentException;
	
	/**
	 * Get the clients user agent
	 * 
	 * @return the user agent
	 */
	public abstract String getUserAgent();
	
	/**
	 * Set the timeout for requests.
	 * 
	 * @param value
	 *            the duration of the time
	 * @param timeUnit
	 *            of the specified duration
	 * @throws IllegalArgumentException
	 *             if the specified time is invalid
	 */
	public abstract void setTimeout(long value, TimeUnit timeUnit) throws IllegalArgumentException;
	
	/**
	 * Get the timeout for requests.
	 * 
	 * @return the timeout in milli seconds
	 */
	public abstract long getTimeoutInMilliseconds();
}
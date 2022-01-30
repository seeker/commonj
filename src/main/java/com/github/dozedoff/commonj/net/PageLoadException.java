/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.IOException;

/**
 * This exception is thrown if the page could not be loaded.
 */

public class PageLoadException extends IOException {
	private int responseCode = -1;
	private String url = null;

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new {@link PageLoadException} with the provided message. The
	 * response code is set to -1 and the url is null. d
	 * 
	 * @param message
	 *            that describes this exception
	 */
	public PageLoadException(String message) {
		super(message);
	}

	/**
	 * Create a new {@link PageLoadException}. The message and url are null, the
	 * response code is set to -1.
	 */
	public PageLoadException() {
		super();
	}

	/**
	 * Create a new {@link PageLoadException} with the provided response code.
	 * Message and url are null.
	 * 
	 * @param responseCode
	 *            the error code from loading the page
	 */
	public PageLoadException(int responseCode) {
		super();
		this.responseCode = responseCode;
	}

	/**
	 * Create a new {@link PageLoadException} with the provided message and
	 * response code. The url is null.
	 * 
	 * @param message
	 *            that describes this exception
	 * @param responseCode
	 *            the error code from loading the page
	 */
	public PageLoadException(String message, int responseCode) {
		super(message);
		this.responseCode = responseCode;
	}

	/**
	 * Create a new {@link PageLoadException} with the provided message,
	 * response code and url.
	 * 
	 * @param message
	 *            that describes this exception
	 * @param responseCode
	 *            the error code from loading the page
	 * @param url
	 *            that caused the exception
	 */
	public PageLoadException(String message, int responseCode, String url) {
		super(message);
		this.responseCode = responseCode;
		this.url = url;
	}

	/**
	 * Create a new {@link PageLoadException} with the provided response code
	 * and url. The message is null.
	 * 
	 * @param responseCode
	 *            the error code from loading the page
	 * @param url
	 *            that caused the exception
	 */
	public PageLoadException(int responseCode, String url) {
		super();
		this.responseCode = responseCode;
		this.url = url;
	}

	/**
	 * Get the response code for the page load. This represents the reason for
	 * the failure.
	 * 
	 * @return the response code, or -1 if not set
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Get the url that failed to load.
	 * 
	 * @return the url as a string, or null if not set
	 */
	public String getUrl() {
		return url;
	}
}

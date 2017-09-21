/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
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

	public PageLoadException(String message) {
		super(message);
	}

	public PageLoadException() {
		super();
	}

	public PageLoadException(int responseCode) {
		super();
		this.responseCode = responseCode;
	}

	public PageLoadException(String message, int responseCode) {
		super(message);
		this.responseCode = responseCode;
	}

	public PageLoadException(String message, int responseCode, String url) {
		super(message);
		this.responseCode = responseCode;
		this.url = url;
	}

	public PageLoadException(int responseCode, String url) {
		super();
		this.responseCode = responseCode;
		this.url = url;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getUrl() {
		return url;
	}
}

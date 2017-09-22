/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for loading HTML data from the Internet.
 */
public class GetHtml {
	private int failCount;
	private static Logger logger = LoggerFactory.getLogger(GetHtml.class);
	private int maxRetry = 3;
	private int readTimeoutInMilli = 10000;

	public int getResponse(String url) throws Exception {
		return getResponse(new URL(url));
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public boolean hasBeenModifiedSince(URL url, long timestamp) {
		HttpURLConnection httpCon = null;
		try {
			httpCon = prepareHttpConnection(url, null);
			httpCon.connect();

			if (httpCon.getLastModified() >= timestamp) {
				return true;
			}
		} catch (IOException io) {
			logger.warn("Error while getting HTML response code: " + io.getMessage());
		} finally {
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}

		return false;
	}

	public long getLastModified(URL url) {
		HttpURLConnection httpCon = null;
		long lastMod = 0;

		try {
			httpCon = prepareHttpConnection(url, null);
			httpCon.connect();
			lastMod = httpCon.getLastModified();
		} catch (IOException io) {
			logger.warn("Error while getting last modified timestamp", io);
		} finally {
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}

		return lastMod;
	}

	public int getResponse(URL url) {
		/*
		 * URLConnection thread = url.openConnection(); return thread.getHeaderField(0);
		 */
		int response = 0;
		HttpURLConnection httpCon = null;
		try {
			httpCon = connect(url);
			response = httpCon.getResponseCode();
		} catch (IOException io) {
			logger.warn("Error while getting HTML response code: " + io.getMessage());
		} finally {
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}

		return response;
	}

	public String get(String url) throws Exception {
		return get(new URL(url));
	}

	/**
	 * Diese Funktion liefert den HTML code der angegebenen Adresse als String.
	 * 
	 * @param url
	 *            Adresse der WebPage
	 * @return WebPage als Text String
	 * @throws IOException
	 * @throws PageLoadException
	 */
	public String get(URL url) throws IOException {
		return loadHtml(connect(url));
	}

	public String get(String url, long lastModificationTimestamp) throws IOException {
		return get(new URL(url), lastModificationTimestamp);
	}

	/**
	 * Get the HTML for the given URL if the page has been modified at or after the timestamp.
	 * 
	 * @param url
	 *            URL to load the HTML from
	 * @param lastModificationTimestamp
	 *            the timestamp of the last modification
	 * @return HTML if the page has been modified, otherwise null
	 * @throws PageLoadException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public String get(URL url, long lastModificationTimestamp) throws IOException {
		HttpURLConnection connection = prepareHttpConnection(url, null);

		connection.setIfModifiedSince(lastModificationTimestamp);
		connection.connect();

		if (connection.getLastModified() >= lastModificationTimestamp) {
			return null;
		}

		return loadHtml(connection);
	}

	public boolean setReadTimeout(int milliSeconds) {
		if (milliSeconds >= 0) {
			this.readTimeoutInMilli = milliSeconds;
			return true;
		} else {
			return false;
		}
	}

	private String loadHtml(HttpURLConnection connection) throws IOException {
		StringBuilder classString = new StringBuilder();
		BufferedReader in = null;
		String inputLine = "";

		try {
			if (connection.getResponseCode() != 200) {
				connection.disconnect();
				throw new PageLoadException(String.valueOf(connection.getResponseCode()), connection.getResponseCode());
			}
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			while ((inputLine = in.readLine()) != null)
				classString.append(inputLine);

		} catch (SocketException se) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			return reTry(connection.getURL(), connection, new SocketException());
		} catch (SocketTimeoutException te) {
			return reTry(connection.getURL(), connection, new SocketTimeoutException());
		} finally {
			if (in != null)
				in.close();
			if (connection != null) {
				connection.disconnect();
			}
		}

		reset();
		return classString.toString();
	}

	private HttpURLConnection connect(URL url) throws IOException {
		return connect(url, null);
	}

	private HttpURLConnection connect(URL url, HashMap<String, String> requestProperties) throws IOException {
		HttpURLConnection httpCon = prepareHttpConnection(url, requestProperties);

		httpCon.connect();
		return httpCon;
	}

	private HttpURLConnection prepareHttpConnection(URL url, HashMap<String, String> requestProperties) throws IOException {
		HttpURLConnection httpCon;
		httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be
																															// a firefox
																															// browser
		if (requestProperties != null) {
			for (String key : requestProperties.keySet()) {
				httpCon.setRequestProperty(key, requestProperties.get(key));
			}
		}
		httpCon.setRequestMethod("GET");
		httpCon.setDoOutput(true);
		httpCon.setReadTimeout(readTimeoutInMilli);

		return httpCon;
	}

	private String reTry(URL url, HttpURLConnection httpCon, IOException ex) throws IOException {
		if (failCount < maxRetry) {
			failCount++;
			httpCon.disconnect();
			return get(url);
		} else {
			if (httpCon != null) {
				httpCon.disconnect();
			}
			throw ex;
		}
	}

	private void reset() {
		failCount = 0;
	}
}

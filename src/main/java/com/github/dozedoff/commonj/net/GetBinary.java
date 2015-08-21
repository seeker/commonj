/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for downloading binary data from the Internet.
 */
public class GetBinary implements DataDownloader {
	private int maxRetry = 3;
	private int readTimeoutInMilli = 10000;
	private final static Logger logger = LoggerFactory.getLogger(GetBinary.class);

	private final static String GET_METHOD = "GET", HEAD_METHOD = "HEAD";

	public GetBinary() {

	}

	public Long getLenght(URL url) throws IOException, PageLoadException {
		HttpURLConnection thread = null;

		try {
			thread = connect(url, HEAD_METHOD, true);
			thread.connect();

			long contentLength = thread.getContentLengthLong();
			thread.getResponseCode(); // this line is REQUIRED, otherwise read timeouts won't throw an exception

			return contentLength;
		} finally {
			closeHttpConnection(thread);
		}
	}

	public Map<String, List<String>> getHeader(URL url) throws IOException {
		HttpURLConnection thread = null;
		try {
			thread = connect(url, HEAD_METHOD, true);
			thread.connect();
			return thread.getHeaderFields();
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		} finally {
			closeHttpConnection(thread);
		}
	}

	public byte[] getRange(URL url, int start, long l) throws IOException, PageLoadException {
		BufferedInputStream binary = null;
		HttpURLConnection httpCon = null;
		ByteBuffer dataBuffer;

		try {
			httpCon = connect(url, GET_METHOD, true);
			httpCon.setRequestProperty("Range", "bytes=" + start + "-" + l);

			httpCon.connect();

			if (httpCon.getResponseCode() != 206) {
				throw new PageLoadException(httpCon.getResponseMessage(), httpCon.getResponseCode());
			}

			binary = new BufferedInputStream(httpCon.getInputStream());
		} catch (SocketTimeoutException ste) {
			closeHttpConnection(httpCon);
			throw new SocketTimeoutException(ste.getMessage());
		} catch (IOException e) {
			closeHttpConnection(httpCon);
			if (httpCon != null) {
				throw new PageLoadException(httpCon.getResponseMessage(), httpCon.getResponseCode());
			} else {
				throw new PageLoadException("null", 0);
			}
		}

		int contentLength = httpCon.getContentLength();
		dataBuffer = ByteBuffer.allocate(contentLength);

		int count = 0;
		byte[] c = new byte[8192]; // transfer data from input (URL) to output (file) one byte at a time

		try {
			while ((count = binary.read(c)) != -1) {
				dataBuffer.put(c, 0, count);
			}
		} catch (SocketException se) {
			logger.warn("SocketException, http response: " + httpCon.getResponseCode());
		} finally {
			if (binary != null)
				binary.close();
			closeHttpConnection(httpCon);
		}

		dataBuffer.flip();
		byte[] varBuffer = new byte[dataBuffer.limit()];
		dataBuffer.get(varBuffer);
		dataBuffer.clear();
		return varBuffer;
	}

	public byte[] getViaHttp(String url) throws PageLoadException, IOException {
		return getViaHttp(new URL(url));
	}

	public byte[] getViaHttp(URL url) throws IOException, PageLoadException {
		int contentLenght;
		ByteBuffer dataBuffer;

		BufferedInputStream binary = null;
		HttpURLConnection httpCon = null;

		httpCon = connect(url, GET_METHOD, true);
		httpCon.connect();

		if (httpCon.getResponseCode() != 200) {
			httpCon.disconnect();
			throw new PageLoadException(String.valueOf(httpCon.getResponseCode()), httpCon.getResponseCode());
		}

		contentLenght = httpCon.getContentLength();
		dataBuffer = ByteBuffer.allocate(contentLenght);
		binary = new BufferedInputStream(httpCon.getInputStream());

		int count = 0;
		byte[] c = new byte[8192]; // transfer data from input (URL) to output (file) one byte at a time
		try {
			while ((count = binary.read(c)) != -1) {
				dataBuffer.put(c, 0, count);
			}
		} catch (IOException ioe) {
			retry(url, dataBuffer, contentLenght);
		} catch (NullPointerException npe) {
			logger.error("NullPointerException in GetBinary.getViaHttp");
			return null;
		} finally {
			if (binary != null)
				binary.close();
			closeHttpConnection(httpCon);
		}

		dataBuffer.flip();
		byte[] varBuffer = new byte[dataBuffer.limit()];
		dataBuffer.get(varBuffer);
		dataBuffer.clear();
		return varBuffer;
	}

	private HttpURLConnection connect(URL url, String method, boolean isOutput) throws IOException, ProtocolException {
		HttpURLConnection httpCon;
		httpCon = (HttpURLConnection) url.openConnection();

		// pretend to be a firefox browser
		httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");

		httpCon.setRequestMethod(method);
		httpCon.setDoOutput(isOutput);
		httpCon.setReadTimeout(readTimeoutInMilli);

		return httpCon;
	}

	private void closeHttpConnection(HttpURLConnection httpCon) {
		if (httpCon != null) {
			httpCon.disconnect();
		}
	}

	//TODO change this back to private once refactoring is done
	protected boolean retry(URL url, ByteBuffer buffer, long contentLength) throws PageLoadException, IOException {

		int failCount = 0;

		while (failCount < maxRetry) {
			Object[] logData = { url, maxRetry - failCount, buffer.position(), buffer.limit() };
			logger.info("Retrying {}, {} tries left, got {} of {} bytes", logData);

			try {
				failCount++;
				byte[] data = getRange(url, buffer.position(), contentLength - 1);
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

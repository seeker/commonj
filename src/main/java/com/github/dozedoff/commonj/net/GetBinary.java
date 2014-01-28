/*  Copyright (C) 2012  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class GetBinary {
	private long contentLenght = 0;
	private int offset = 0;
	private int failCount = 0;
	private int maxRetry = 3;
	private int readTimeoutInMilli = 10000;
	private final static Logger logger = LoggerFactory.getLogger(GetBinary.class);

	private final static String GET_METHOD = "GET", HEAD_METHOD = "HEAD";

	public GetBinary() {

	}

	/**
	 * No longer used, as buffers are now created on demand
	 * 
	 * @param size
	 */
	@Deprecated
	public GetBinary(int size) {

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
			if (thread != null)
				thread.disconnect();
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
			if (thread != null) {
				thread.disconnect();
			}
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
			binary = new BufferedInputStream(httpCon.getInputStream());
		} catch (SocketTimeoutException ste) {
			if (httpCon != null) {
				httpCon.disconnect();
			}
			throw new SocketTimeoutException(ste.getMessage());
		} catch (IOException e) {
			if (httpCon != null) {
				httpCon.disconnect();
			}
			throw new PageLoadException(Integer.toString(httpCon.getResponseCode()), httpCon.getResponseCode());
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
			if (failCount < maxRetry) {
				try {
					Thread.sleep(5000);
				} catch (Exception ie) {
				}
				this.offset = dataBuffer.position();
				httpCon.disconnect();
				failCount++;
				return getRange(url, offset, contentLenght - 1);
			} else {
				logger.warn("Buffer position at failure: " + dataBuffer.position() + "  URL: " + url.toString());
				httpCon.disconnect();
				throw new SocketException();
			}
		} finally {
			if (binary != null)
				binary.close();
			if (httpCon != null) {
				httpCon.disconnect();
			}
		}
		if (failCount != 0)
			logger.info("GetBinary Successful -> " + dataBuffer.position() + "/" + contentLenght + ", " + failCount + " tries, "
					+ url.toString());

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
		} catch (NullPointerException npe) {
			logger.error("NullPointerException in GetBinary.getViaHttp");
			return null;
		} finally {
			if (binary != null)
				binary.close();
			if (httpCon != null) {
				httpCon.disconnect();
			}
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

	private void retry(URL url, ByteBuffer buffer, long contentLength, int triesLeft) throws PageLoadException, IOException {
		getRange(url, buffer.position(), contentLenght - 1);
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
}

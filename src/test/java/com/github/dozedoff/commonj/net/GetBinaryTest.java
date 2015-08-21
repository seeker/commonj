/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetBinaryTest {
	GetBinary getBinary;
	static Server server;
	static byte[] testData = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	static byte[] testData2 = { 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	static final int SERVER_PORT = 5400;

	private static enum Pages {
		data, data2, range, wait, notok, error
	};

	HashMap<Pages, URL> pageURLs = new HashMap<>();

	static final int READ_TIMEOUT_CLASS = 100;
	static final int READ_TIMEOUT_TEST = 1500;

	@BeforeClass
	public static void startServer() throws Exception {
		server = new Server(SERVER_PORT);
		server.setHandler(new TestHandler());
		server.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		server.stop();
	}

	@Before
	public void setUp() throws Exception {
		getBinary = new GetBinary();
		server.start();
	}

	@Test
	public void testInvalidTimeoutSetting() {
		assertThat(getBinary.setReadTimeout(-100), is(false));
	}

	@Test
	public void testGetViaHttp() throws IOException {
		testData = generateRandomData(25);
		byte[] data = getBinary.getViaHttp(getURL(Pages.data));
		assertThat(data, is(testData));
	}
	
	@Test
	public void testGetViaHttpString() throws IOException {
		testData = generateRandomData(25);
		byte[] data = getBinary.getViaHttp(getURL(Pages.data).toString());
		assertThat(data, is(testData));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetViaHttpTimeout() throws IOException {
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getViaHttp(getURL(Pages.wait));
	}
	
	@Test(expected=PageLoadException.class)
	public void testGetViaHttpBadRequest() throws IOException {
		getBinary.getViaHttp(getURL(Pages.notok));
	}
	
	@Test(expected=IOException.class)
	public void testGetViaHttpIOExceptiom() throws IOException {
		getBinary.getViaHttp(getURL(Pages.error));
	}

	@Test
	public void testGetLenght() throws Exception {
		testData = generateRandomData(25);
		Long size = getBinary.getLenght(getURL(Pages.data));
		assertThat(size, is((long) testData.length));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetLenghtTimeOut() throws Exception {
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getLenght(getURL(Pages.wait));
	}

	@Test
	public void testGetHeader() throws Exception {
		testData = generateRandomData(5000);
		Map<String, List<String>> header = getBinary.getHeader(getURL(Pages.data));

		assertThat(header.containsKey("Content-Length"), is(true));
		assertThat(header.get("Content-Length").get(0), is("5000"));
	}

	@Test
	public void testGetRange() throws Exception {
		testData = generateRandomData(25);
		byte[] subSet = Arrays.copyOfRange(testData, 10, 25);

		byte[] data = getBinary.getRange(getURL(Pages.data), 10, 15);
		assertThat(data, is(subSet));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetRangeTimeout() throws Exception {
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getRange(getURL(Pages.wait), 10, 15);
	}

	@Test(timeout = 5000)
	public void testReUse() throws Exception {
		assertThat(getBinary.getViaHttp(getURL(Pages.data)), is(testData));
		assertThat(getBinary.getViaHttp(getURL(Pages.data2)), is(testData2));
		assertThat(getBinary.getViaHttp(getURL(Pages.data)), is(testData));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testConnectionTimeout() throws Exception {
		getBinary.setMaxRetry(0);
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getViaHttp(getURL(Pages.wait));
	}

	@Test
	public void testRetry() throws Exception {
		int dataSize = 25;
		testData = generateRandomData(dataSize);
		URL url = getURL(Pages.data);
		
		ByteBuffer buffer = ByteBuffer.allocate(dataSize);
		
		boolean response = getBinary.retry(url, buffer, dataSize);
		byte[] data = bufferToArray(buffer, dataSize);
		
		assertThat(response, is(true));
		assertThat(data, is(equalTo(testData)));
	}
	
	@Test
	public void testRetryOutOfRetries() throws Exception {
		int dataSize = 25;
		testData = generateRandomData(dataSize);
		URL url = getURL(Pages.notok);
		
		ByteBuffer buffer = ByteBuffer.allocate(dataSize);
		
		boolean response = getBinary.retry(url, buffer, dataSize);
		
		assertThat(response, is(false));
	}
	
	private byte[] bufferToArray(ByteBuffer buffer, int dataSize) {
		buffer.flip();
		byte[] data = new byte[dataSize];
		buffer.get(data);
		return data;
	}

	static class TestHandler extends AbstractHandler {
		@Override
		public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
				ServletException {

			response.setContentType("application/octet-stream");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			String pageName = extractPage(request);

			Pages selectedPage = null;

			try {
				selectedPage = Pages.valueOf(Pages.class, pageName);
			} catch (IllegalArgumentException iae) {
				fail(pageName + " is not a valid page Enum");
			} catch (NullPointerException npe) {
				fail("Page name was null");
			}

			switch (selectedPage) {
			case data:
				processDataRequest(request, response, testData);
				break;

			case data2:
				processDataRequest(request, response, testData2);
				break;

			case wait:
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
				}
				break;
				
			case notok:
				response.setStatus(Response.SC_BAD_REQUEST);
				break;
				
			case error:
				response.sendError(Response.SC_BAD_REQUEST);
				break;
				
			default:
				throw new IllegalArgumentException("Unknown page");
			}
		}
	}

	private static boolean isRangeRequest(HttpServletRequest request) {
		return (request.getHeader("Range") != null && request.getHeader("Range").contains("bytes"));
	}

	private static void processRangeRequest(HttpServletRequest request, HttpServletResponse response, byte[] data) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getHeader("Range"));

		sb.replace(0, 6, "");
		String[] marker = sb.toString().split("-");

		int start = Integer.parseInt(marker[0]);
		int offset = start + Integer.parseInt(marker[1]);

		byte[] selection = Arrays.copyOfRange(data, start, offset);
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.getOutputStream().write(selection);
		response.getOutputStream().close();
	}

	private static void processDataRequest(HttpServletRequest request, HttpServletResponse response, byte[] data) throws IOException {
		if (isRangeRequest(request)) {
			processRangeRequest(request, response, data);
		} else {
			response.getOutputStream().write(data);
			response.getOutputStream().close();
		}
	}

	private byte[] generateRandomData(int numOfBytes) {
		byte[] randomData = new byte[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			randomData[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}

		return randomData;
	}

	private static String extractPage(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		int pageIndexStart = requestUri.lastIndexOf("/");

		String pageName = requestUri.substring(pageIndexStart + 1);

		return pageName;
	}

	private URL getURL(Pages page) throws MalformedURLException {
		if (pageURLs.containsKey(page)) {
			return pageURLs.get(page);
		} else {
			URL pageUrl = createURL(page);
			pageURLs.put(page, pageUrl);
			return pageUrl;
		}
	}

	private URL createURL(Pages page) throws MalformedURLException {
		String pageName = page.toString();
		String constructedUrl = "http://localhost:" + SERVER_PORT + "/" + pageName;

		URL pageUrl = new URL(constructedUrl);

		return pageUrl;
	}
}

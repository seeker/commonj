/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
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
	static final int SERVER_PORT = 5400;
	static final int TEST_DATA_SIZE = 25;
	static final int READ_TIMEOUT_CLASS = 100;
	static final int READ_TIMEOUT_TEST = 1500;
	
	private static enum Pages {
		data, range, wait, notok, error
	};
	
	GetBinary getBinary;
	static Server server;
	static TestHandler testHandler;
	HashMap<Pages, URL> pageURLs = new HashMap<>();

	@BeforeClass
	public static void startServer() throws Exception {
		server = new Server(SERVER_PORT);
		testHandler = new TestHandler();
		server.setHandler(testHandler);
		server.start();
	}

	@AfterClass
	public static void stopServer() throws Exception {
		server.stop();
	}

	@Before
	public void setUp() throws Exception {
		getBinary = new GetBinary();
		testHandler.setTestData(new byte[0]);
		server.start();
	}

	@Test
	public void testInvalidTimeoutSetting() {
		assertThat(getBinary.setReadTimeout(-100), is(false));
	}

	@Test
	public void testGetViaHttp() throws IOException {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		byte[] data = getBinary.getViaHttp(getURL(Pages.data));
		assertThat(data, is(testHandler.getTestData()));
	}
	
	@Test
	public void testGetViaHttpString() throws IOException {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		byte[] data = getBinary.getViaHttp(getURL(Pages.data).toString());
		assertThat(data, is(testHandler.getTestData()));
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
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		Long size = getBinary.getLenght(getURL(Pages.data));
		assertThat(size, is((long) testHandler.getTestData().length));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetLenghtTimeOut() throws Exception {
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getLenght(getURL(Pages.wait));
	}

	@Test
	public void testGetHeader() throws Exception {
		int dataLength = 5000;
		testHandler.setTestData(generateRandomData(dataLength));
		Map<String, List<String>> header = getBinary.getHeader(getURL(Pages.data));

		assertThat(header.containsKey("Content-Length"), is(true));
		assertThat(header.get("Content-Length").get(0), is(Integer.toString(dataLength)));
	}

	@Test
	public void testGetRange() throws Exception {
		int dataOffset = 10;
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		byte[] subSet = Arrays.copyOfRange(testHandler.getTestData(), dataOffset, TEST_DATA_SIZE);

		byte[] data = getBinary.getRange(getURL(Pages.data), dataOffset, TEST_DATA_SIZE-dataOffset);
		assertThat(data, is(subSet));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetRangeTimeout() throws Exception {
		int dataOffset = 10;
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getRange(getURL(Pages.wait), dataOffset, TEST_DATA_SIZE-dataOffset);
	}

	@Test(timeout = 5000)
	public void testReUse() throws Exception {
		int iterations = 3;
		
		for (int i=0; i<iterations; i++){
			testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
			assertThat(getBinary.getViaHttp(getURL(Pages.data)), is(testHandler.getTestData()));
		}
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testConnectionTimeout() throws Exception {
		getBinary.setMaxRetry(0);
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getViaHttp(getURL(Pages.wait));
	}

	@Test
	public void testRetry() throws Exception {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		URL url = getURL(Pages.data);
		
		ByteBuffer buffer = ByteBuffer.allocate(TEST_DATA_SIZE);
		
		boolean response = getBinary.retry(url, buffer, TEST_DATA_SIZE);
		byte[] data = bufferToArray(buffer, TEST_DATA_SIZE);
		
		assertThat(response, is(true));
		assertThat(data, is(equalTo(testHandler.getTestData())));
	}
	
	@Test
	public void testRetryOutOfRetries() throws Exception {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		URL url = getURL(Pages.notok);
		
		ByteBuffer buffer = ByteBuffer.allocate(TEST_DATA_SIZE);
		
		boolean response = getBinary.retry(url, buffer, TEST_DATA_SIZE);
		
		assertThat(response, is(false));
	}
	
	private byte[] bufferToArray(ByteBuffer buffer, int dataSize) {
		buffer.flip();
		byte[] data = new byte[dataSize];
		buffer.get(data);
		return data;
	}

	static class TestHandler extends AbstractHandler {
		private byte[] testData;
		
		public byte[] getTestData() {
			return testData;
		}

		public void setTestData(byte[] testData) {
			this.testData = testData;
		}

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
				processDataRequest(request, response, this.testData);
				break;

			case wait:
				try {
					Thread.sleep(2000);
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

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class JettyHttpClientTestNTS {
	static final int SERVER_PORT = 5400;
	static final int TEST_DATA_SIZE = 25;
	static final int READ_TIMEOUT_CLASS = 100;
	static final int READ_TIMEOUT_TEST = 1500;

	private static final AtomicInteger PORT_OFFSET = new AtomicInteger();
	private int testPort = SERVER_PORT + PORT_OFFSET.incrementAndGet();

	private static enum Pages {
		data, range, wait, notok, error, agent
	};

	@Rule
	public ExpectedException expected = ExpectedException.none();

	JettyHttpClient cut;
	static Server server;
	static TestHandler testHandler;
	HashMap<Pages, URL> pageURLs = new HashMap<>();

	/**
	 * Workaround for testing on Windows 10. On Linux and Windows 7, a
	 * {@link TimeoutException} is thrown, however Windows 10 throws the
	 * exception wrapped in a {@link ExecutionException}.
	 */
	private void windowsTenWorkaraound() {
		expected.expect(anyOf(instanceOf(TimeoutException.class), instanceOf(ExecutionException.class)));
	}

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
		cut = new JettyHttpClient();
		testHandler.setTestData(new byte[0]);
		server.start();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetTimeoutInvalid() {
		cut.setTimeout(-100, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testSetTimeoutZero() {
		cut.setTimeout(0, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testSetTimeoutPositive() {
		cut.setTimeout(10, TimeUnit.SECONDS);
	}

	@Test
	public void testGetData() throws Exception {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		byte[] data = cut.getData(getURL(Pages.data));
		assertThat(data, is(testHandler.getTestData()));
	}

	@Test(timeout = READ_TIMEOUT_TEST)
	public void testGetDataTimeout() throws Exception {
		windowsTenWorkaraound();
		cut.setTimeout(READ_TIMEOUT_CLASS, TimeUnit.MILLISECONDS);
		cut.getData(getURL(Pages.wait));
	}

	@Test(expected = PageLoadException.class)
	public void testGetDataBadRequest() throws Exception {
		cut.getData(getURL(Pages.notok));
	}

	@Test(expected = IOException.class)
	public void testGetDataIOExceptiom() throws Exception {
		cut.getData(getURL(Pages.error));
	}

	@Test
	public void testGetLenght() throws Exception {
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		Long size = cut.getLenght(getURL(Pages.data));
		assertThat(size, is((long) testHandler.getTestData().length));
	}

	@Test(timeout = READ_TIMEOUT_TEST)
	public void testGetLenghtTimeOut() throws Exception {
		windowsTenWorkaraound();

		cut.setTimeout(READ_TIMEOUT_CLASS, TimeUnit.MILLISECONDS);
		cut.getLenght(getURL(Pages.wait));
	}

	@Test
	public void testGetHeader() throws Exception {
		int dataLength = 5000;
		testHandler.setTestData(generateRandomData(dataLength));
		Map<String, List<String>> header = cut.getHeader(getURL(Pages.data));

		assertThat(header.containsKey("Content-Length"), is(true));
		assertThat(header.get("Content-Length").get(0), is(Integer.toString(dataLength)));
	}

	@Test
	public void testGetDataRange() throws Exception {
		int dataOffset = 10;
		testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
		byte[] subSet = Arrays.copyOfRange(testHandler.getTestData(), dataOffset, TEST_DATA_SIZE);

		byte[] data = cut.getDataRange(getURL(Pages.data), dataOffset, TEST_DATA_SIZE - dataOffset);
		assertThat(data, is(subSet));
	}

	@Test(timeout = READ_TIMEOUT_TEST)
	public void testGetDataRangeTimeout() throws Exception {
		windowsTenWorkaraound();
		int dataOffset = 10;
		cut.setTimeout(READ_TIMEOUT_CLASS, TimeUnit.MILLISECONDS);

		cut.getDataRange(getURL(Pages.wait), dataOffset, TEST_DATA_SIZE - dataOffset);
	}

	@Test(timeout = 5000)
	public void testReUse() throws Exception {
		int iterations = 3;

		for (int i = 0; i < iterations; i++) {
			testHandler.setTestData(generateRandomData(TEST_DATA_SIZE));
			assertThat(cut.getData(getURL(Pages.data)), is(testHandler.getTestData()));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetUserAgentNull() throws Exception {
		cut.setUserAgent(null);
	}

	@Test
	public void testSetUserAgent() throws Exception {
		String newAgent = "Foobar";
		cut.setUserAgent(newAgent);

		byte[] data = cut.getData(getURL(Pages.agent));
		String decoded = new String(data, StandardCharsets.UTF_8);

		assertThat(decoded, is(newAgent));
	}

	@Test
	public void testDefaultUserAgent() throws Exception {
		assertThat(cut.getUserAgent(), is("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0"));
	}

	@Test
	public void testGetTimeoutInMilliseconds() throws Exception {
		cut.setTimeout(2, TimeUnit.SECONDS);

		assertThat(cut.getTimeoutInMilliseconds(), is(2000L));
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
		public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

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

			case agent:
				processDataRequest(request, response, request.getHeader("user-agent").getBytes(StandardCharsets.UTF_8));
				break;

			default:
				throw new IllegalArgumentException("Unknown page");
			}
		}
	}

	private static boolean isRangeRequest(HttpServletRequest request) {
		return (request.getHeader("Range") != null && request.getHeader("Range").contains("bytes"));
	}

	private static void processRangeRequest(HttpServletRequest request, HttpServletResponse response, byte[] data)
			throws IOException {
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

	private static void processDataRequest(HttpServletRequest request, HttpServletResponse response, byte[] data)
			throws IOException {
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

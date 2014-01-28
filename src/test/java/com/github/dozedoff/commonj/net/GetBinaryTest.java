package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GetBinaryTest {
	GetBinary getBinary;
	static Server server;
	static byte[] testData = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	static byte[] testData2 = { 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	static final int SERVER_PORT = 5400;

	private static enum Pages {
		data, data2, range, wait
	};

	HashMap<Pages, URL> pageURLs = new HashMap<>();

	static final int READ_TIMEOUT_CLASS = 100;
	static final int READ_TIMEOUT_TEST = 1000;

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
	public void testGetViaHttp() throws IOException {
		testData = generateRandomData(25);
		byte[] data = getBinary.getViaHttp(getURL(Pages.data));
		assertThat(data, is(testData));
	}

	@Test(timeout = READ_TIMEOUT_TEST, expected = SocketTimeoutException.class)
	public void testGetViaHttpTimeout() throws IOException {
		getBinary.setReadTimeout(READ_TIMEOUT_CLASS);
		getBinary.getViaHttp(getURL(Pages.wait));
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
	@Ignore
	public void testGetHeader() throws Exception {
		fail("Not yet implemented");
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

	@Test(timeout = 10000, expected = SocketException.class)
	public void testConnectionFail() throws Exception {
		getBinary.setMaxRetry(0);
		server.stop();
		getBinary.getViaHttp(getURL(Pages.data));
	}

	static class TestHandler extends AbstractHandler {
		@Override
		public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
				ServletException {

			response.setContentType("application/octet-stream");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			if (matchesPage(request, Pages.data2)) {
				response.getOutputStream().write(testData2);
				response.getOutputStream().close();
			} else if (matchesPage(request, Pages.wait)) {
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
				}
			} else if (request.getHeader("Range") != null && request.getHeader("Range").contains("bytes")) {
				StringBuilder sb = new StringBuilder();
				sb.append(request.getHeader("Range"));

				sb.replace(0, 6, "");
				String[] marker = sb.toString().split("-");

				int start = Integer.parseInt(marker[0]);
				int offset = start + Integer.parseInt(marker[1]);

				byte[] selection = Arrays.copyOfRange(testData, start, offset);
				response.getOutputStream().write(selection);
				response.getOutputStream().close();
			} else if (matchesPage(request, Pages.data)) {
				response.getOutputStream().write(testData);
				response.getOutputStream().close();
			} else {
				throw new IllegalArgumentException("Unknown page");
			}
		}
	}

	private byte[] generateRandomData(int numOfBytes) {
		byte[] randomData = new byte[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			randomData[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}

		return randomData;
	}

	private static boolean matchesPage(HttpServletRequest request, Pages page) {
		return (request.getRequestURI().equals("/" + page.toString()));
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

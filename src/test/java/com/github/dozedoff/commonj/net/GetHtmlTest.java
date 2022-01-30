/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetHtmlTest {
	private GetHtml getHtml;
	private static String testData = "<html><head><title>Test Page</title></head><body><p>Test Page</p></body></html>";
	private static String testData2 = "<!DOCTYPE html><html><head><title>Bestellformular</title></head><body><h1>Bestellung</h1><fieldset><legend>Kundendaten</legend></fieldset><fieldset><legend>Artikel</legend></fieldset></body></html>";
	private String testString;
	private static Server server;

	private static final int SERVER_PORT = 5400;
	private String url = "http://localhost:" + SERVER_PORT + "/", url2 = "http://localhost:" + SERVER_PORT + "/2",
			urlWait = "http://localhost:"
			+ SERVER_PORT + "/wait";

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
		getHtml = new GetHtml();
		server.start();
	}

	@Test(timeout = 1000)
	public void testGetString() throws Exception {
		testString = getHtml.get(url);
		assertThat(testString, is(testData));
	}

	@Test(timeout = 5000)
	public void testReUse() throws Exception {
		assertThat(getHtml.get(url), is(testData));
		assertThat(getHtml.get(url2), is(testData2));
		assertThat(getHtml.get(url), is(testData));
	}

	@Test(timeout = 1200, expected = SocketTimeoutException.class)
	public void testConnectionTimeout() throws Exception {
		getHtml.setMaxRetry(0);
		getHtml.setReadTimeout(200);
		getHtml.get(urlWait);
	}

	@Test(timeout = 10000, expected = SocketException.class)
	public void testConnectionFail() throws Exception {
		getHtml.setMaxRetry(0);
		server.stop();
		getHtml.get(url);
	}

	@Test
	public void testGetLastModifiedNotSet() throws Exception {
		assertThat(getHtml.getLastModified(new URL(url)), is(0L));
	}

	@Test
	public void testGetLastModified() throws Exception {
		assertThat(getHtml.getLastModified(makeURL("mod")), greaterThanOrEqualTo(Calendar.getInstance().getTimeInMillis() - 5000L));
	}

	@Test
	public void testGetResponseURL() throws Exception {
		assertThat(getHtml.getResponse(makeURL(null)), is(200));
	}

	@Test
	public void testSetReadTimeout() throws Exception {
		assertThat(getHtml.setReadTimeout(1000), is(true));
	}

	@Test
	public void testSetReadTimeoutNegative() throws Exception {
		assertThat(getHtml.setReadTimeout(-1), is(false));
	}

	@Test
	public void testSetReadTimeoutInfinity() throws Exception {
		assertThat(getHtml.setReadTimeout(0), is(true));
	}

	private URL makeURL(String path) throws MalformedURLException {
		StringBuilder sb = new StringBuilder(url);
	
		if (path != null) {
			sb.append(path);
		}
	
		return new URL(sb.toString());
	}

	private static long getCurrentTime() {
		return Calendar.getInstance().getTimeInMillis();
	}

	static class TestHandler extends AbstractHandler {
		@Override
		public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
				ServletException {

			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			if (request.getRequestURI().equals("/2")) {
				response.getWriter().println(testData2);
			} else if (request.getRequestURI().equals("/wait")) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			} else if (request.getRequestURI().equals("/mod")) {
				response.setDateHeader("Last-Modified", getCurrentTime());
			} else {
				response.getWriter().println(testData);
			}
		}
	}
}

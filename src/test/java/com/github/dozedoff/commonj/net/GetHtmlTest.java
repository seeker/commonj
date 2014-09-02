/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetHtmlTest {
	GetHtml getHtml;
	static String testData = "<html><head><title>Test Page</title></head><body><p>Test Page</p></body></html>";
	static String testData2 = "<!DOCTYPE html><html><head><title>Bestellformular</title></head><body><h1>Bestellung</h1><fieldset><legend>Kundendaten</legend></fieldset><fieldset><legend>Artikel</legend></fieldset></body></html>";
	String testString = null;
	static Server server;

	static final int SERVER_PORT = 5400;
	String url = "http://localhost:" + SERVER_PORT + "/", url2 = "http://localhost:" + SERVER_PORT + "/2", urlWait = "http://localhost:"
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
		getHtml.setReadTimeout(1000);
		getHtml.get(urlWait);
	}

	@Test(timeout = 10000, expected = SocketException.class)
	public void testConnectionFail() throws Exception {
		getHtml.setMaxRetry(0);
		server.stop();
		getHtml.get(url);
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
			} else {
				response.getWriter().println(testData);
			}
		}
	}
}

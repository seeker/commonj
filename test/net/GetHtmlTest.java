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
package net;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
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
	String url = "http://localhost/", url2 = "http://localhost/2", urlWait = "http://localhost/wait";

	@BeforeClass
	public static void startServer() throws Exception{
		server  = new Server(80);
		server.setHandler(new TestHandler());
		server.start();
	}

	@AfterClass
	public static void stopServer() throws Exception{
		server.stop();
	}

	@Before
	public void setUp() throws Exception{
		getHtml = new GetHtml();
		server.start();
	}

	@Test(timeout=1000)
	public void testGetString() throws Exception {
		testString = getHtml.get(url);
		assertThat(testString,is(testData));
	}

	@Test(timeout=5000)
	public void testReUse() throws Exception{
		assertThat(getHtml.get(url), is(testData));
		assertThat(getHtml.get(url2), is(testData2));
		assertThat(getHtml.get(url), is(testData));
	}
	
	@Test(timeout=12000, expected=SocketTimeoutException.class)
	public void testConnectionTimeout() throws Exception{
		getHtml.setMaxRetry(0);
		getHtml.get(urlWait);
	}
	
	@Test(timeout=10000, expected=SocketException.class)
	public void testConnectionFail() throws Exception{
		getHtml.setMaxRetry(0);
		server.stop();
		getHtml.get(url);
	}

	static class TestHandler extends AbstractHandler{
		@Override
		public void handle(String arg0, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) throws IOException, ServletException {

			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			if(request.getRequestURI().equals("/2")){
				response.getWriter().println(testData2);
			}else if(request.getRequestURI().equals("/wait")){
				try {Thread.sleep(12000);} catch (InterruptedException e) {}
			}else{
				response.getWriter().println(testData);
			}
		}
	}
}

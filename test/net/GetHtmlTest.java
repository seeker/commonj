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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetHtmlTest {
static String refData = "<html><head><title>Test Page</title></head><body><p>Test Page</p></body></html>";
String testString = null;
static Server server;

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

static class TestHandler extends AbstractHandler{
	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest arg2,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(refData);
	}
}
	@Test(timeout=2000)
	public void testGetString() throws Exception {
		GetHtml getHtml = new GetHtml();
		
		testString = getHtml.get("http://localhost/");
		assertTrue(testString.equals(refData));
	}
}

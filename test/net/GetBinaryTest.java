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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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

public class GetBinaryTest {
	GetBinary getBinary;
	static Server server;
	static byte[] testData = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
	static byte[] testData2 = {20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
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
		getBinary = new GetBinary();
		server.start();
	}

	@Test
	public void testGetViaHttp() throws IOException {
		testData = generateRandomData(25);
		byte[] data = getBinary.getViaHttp(url);
		assertThat(data, is(testData));
	}

	@Test
	public void testGetLenght() throws Exception {
		testData = generateRandomData(25);
		Long size = getBinary.getLenght(new URL(url));
		assertThat(size, is((long)testData.length));
	}

	@Test
	public void testGetHeader() throws Exception{
		Map<String, List<String>> header = getBinary.getHeader(new URL(url));
		fail("Not yet implemented");
	}

	@Test
	public void testGetRange() throws Exception{
		testData = generateRandomData(25);
		byte[] subSet = Arrays.copyOfRange(testData, 10, 25);
	
		byte[] data = getBinary.getRange(new URL(url), 10, 15);
		assertThat(data, is(subSet));
	}

	@Test(timeout=5000)
	public void testReUse() throws Exception{
		assertThat(getBinary.getViaHttp(url), is(testData));
		assertThat(getBinary.getViaHttp(url2), is(testData2));
		assertThat(getBinary.getViaHttp(url), is(testData));
	}
	
	@Test(timeout=12000, expected=SocketTimeoutException.class)
	public void testConnectionTimeout() throws Exception{
		getBinary.setMaxRetry(0);
		getBinary.getViaHttp(urlWait);
	}
	
	@Test(timeout=10000, expected=SocketException.class)
	public void testConnectionFail() throws Exception{
		getBinary.setMaxRetry(0);
		server.stop();
		getBinary.getViaHttp(url);
	}
	
	static class TestHandler extends AbstractHandler{
		@Override
		public void handle(String arg0, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) throws IOException, ServletException {

			response.setContentType("application/octet-stream");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			if(request.getRequestURI().equals("/2")){
				response.getOutputStream().write(testData2);
				response.getOutputStream().close();
			}else if(request.getRequestURI().equals("/wait")){
				try {Thread.sleep(12000);} catch (InterruptedException e) {}
			}else if(request.getHeader("Range") != null && request.getHeader("Range").contains("bytes")){
				StringBuilder sb = new StringBuilder();
				sb.append(request.getHeader("Range"));
				
				sb.replace(0, 6, "");
				String[] marker = sb.toString().split("-"); 
				
				int start = Integer.parseInt(marker[0]);
				int offset = start + Integer.parseInt(marker[1]);
				
				byte[] selection = Arrays.copyOfRange(testData, start, offset);
				response.getOutputStream().write(selection);
				response.getOutputStream().close();
			}else{
				response.getOutputStream().write(testData);
				response.getOutputStream().close();
			}
		}
	}
	
	private byte[] generateRandomData(int numOfBytes){
		byte[] randomData = new byte[numOfBytes];
		for(int i = 0; i<numOfBytes; i++){
			randomData[i] = (byte)(Math.random()*Byte.MAX_VALUE);
		}

		return randomData;
	}
}

/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class GetBinaryTest {
	private IHttpClient httpClient;
	private GetBinary cut;
	private URL testUrl;
	
	private static final int OFFSET = 0;
	private static final long RANGE = 100;
	private static final String TEST_STRING = "http://example.com";

	@Before
	public void setUp() throws Exception {
		httpClient = mock(IHttpClient.class);
		cut = new GetBinary(httpClient);
		testUrl = new URL(TEST_STRING);
	}

	@Test
	public void testGetLenght() throws Exception {
		cut.getLenght(testUrl);
		verify(httpClient).getLenght(testUrl);
	}
	
	@Test(expected=SocketTimeoutException.class)
	public void testGetLenghtTimeout() throws Exception {
		when(httpClient.getLenght(any(URL.class))).thenThrow(new TimeoutException());
		cut.getLenght(testUrl);
	}
	
	@Test
	public void testGetLenghtInterrupted() throws Exception {
		when(httpClient.getLenght(any(URL.class))).thenThrow(new InterruptedException());
		assertThat(cut.getLenght(testUrl), is(-1L));
	}

	@Test
	public void testGetHeader() throws Exception {
		cut.getHeader(testUrl);
		verify(httpClient).getHeader(testUrl);
	}

	@Test
	public void testGetHeaderInterrupted() throws Exception {
		when(cut.getHeader(testUrl)).thenThrow(new InterruptedException());
		Map<String,List<String>> result = cut.getHeader(testUrl);

		assertThat(result.isEmpty(), is(true));
	}
	
	@Test
	public void testGetRange() throws Exception {
		cut.getRange(testUrl, OFFSET, RANGE);
		
		verify(httpClient).getDataRange(testUrl, OFFSET, RANGE);
	}
	
	@Test(expected=SocketTimeoutException.class)
	public void testGetRangeTimeout() throws Exception {
		when(httpClient.getDataRange(testUrl, OFFSET, RANGE)).thenThrow(new TimeoutException());
		cut.getRange(testUrl, OFFSET, RANGE);
	}
	
	@Test(expected=PageLoadException.class)
	public void testGetRangeInterrupted() throws Exception {
		when(httpClient.getDataRange(testUrl, OFFSET, RANGE)).thenThrow(new InterruptedException());
		cut.getRange(testUrl, OFFSET, RANGE);
	}

	@Test
	public void testGetViaHttpString() throws Exception {
		cut.getViaHttp(TEST_STRING);
		
		verify(httpClient).getData(new URL(TEST_STRING));
	}

	@Test
	public void testGetViaHttpURL() throws Exception {
		cut.getViaHttp(testUrl);
		
		verify(httpClient).getData(testUrl);
	}
	
	@Test
	public void testSetReadTimeoutPositive() throws Exception {
		assertThat(cut.setReadTimeout(42), is(true));
	}
	
	@Test
	public void testSetReadTimeoutZero() throws Exception {
		assertThat(cut.setReadTimeout(0), is(true));
	}
	
	@Test
	public void testSetReadTimeoutNegative() throws Exception {
		assertThat(cut.setReadTimeout(-42), is(false));
	}

	@Test
	public void testDownload() throws Exception {
		cut.download(testUrl);
		
		verify(httpClient).getData(testUrl);
	}
}

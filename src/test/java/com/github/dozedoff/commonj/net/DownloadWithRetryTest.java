package com.github.dozedoff.commonj.net;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DownloadWithRetryTest {
	private static final String TEST_URL_STRING = "http://example.com";
	private static final int MAX_RETRIES = 3;
	private static final int CONTENT_LENGTH = 50;
	
	private GetBinary getBinary;
	private DownloadWithRetry cut;
	private URL testUrl;
	private byte[] randomData;

	
	@Before
	public void setUp() throws Exception {
		randomData = generateRandomData(CONTENT_LENGTH);
		
		getBinary = mock(GetBinary.class);
		configureMock(getBinary);
		
		cut = new DownloadWithRetry(getBinary);
		testUrl = new URL(TEST_URL_STRING);
		
	}
	
	private void configureMock(GetBinary getBinary) throws Exception {
		when(getBinary.getLenght(any(URL.class))).thenReturn((long) CONTENT_LENGTH);
		when(getBinary.getRange(any(URL.class), anyInt(), anyLong())).thenReturn(randomData);
	}

	@Test
	public void testDownload() throws Exception {
		byte[] data = cut.download(testUrl, MAX_RETRIES);
		assertThat(data, is(randomData));
	}
	
	@Test
	public void testDownloadWithRetries() throws Exception {
		when(getBinary.getRange(any(URL.class), anyInt(), anyLong())).thenThrow(new IOException("Testing...")).thenThrow(new IOException("Testing...")).thenReturn(randomData);
		byte[] data = cut.download(testUrl, MAX_RETRIES);
		assertThat(data, is(randomData));
	}
	
	@Test
	public void testDownloadTooManyFailures() throws Exception {
		when(getBinary.getRange(any(URL.class), anyInt(), anyLong())).thenThrow(new IOException("Testing...")).thenThrow(new IOException("Testing...")).thenThrow(new IOException("Testing...")).thenReturn(randomData);
		byte[] data = cut.download(testUrl, MAX_RETRIES);
		assertThat(data, is(new byte[0]));
	}
	
	@Test
	public void testDownloadPageLoadException() throws Exception {
		when(getBinary.getRange(any(URL.class), anyInt(), anyLong())).thenThrow(new PageLoadException("Testing..."));
		byte[] data = cut.download(testUrl, MAX_RETRIES);
		assertThat(data, is(new byte[0]));
	}
	
	@Test
	public void testDownloadIOException() throws Exception {
		when(getBinary.getRange(any(URL.class), anyInt(), anyLong())).thenThrow(new IOException("Testing..."));
		byte[] data = cut.download(testUrl, MAX_RETRIES);
		assertThat(data, is(new byte[0]));
	}
	
	private byte[] generateRandomData(int numOfBytes) {
		byte[] randomData = new byte[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			randomData[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}

		return randomData;
	}

}

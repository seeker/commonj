/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileLoaderTest {
	private static final int DEFAULT_TIMEOUT = 1000;

	private FileLoader cut;
	private DataDownloader ddl;
	private FileLoaderAction actions;

	private static final String TEST_ADDRESS = "http://example.com";
	private static final String TEST_FILE_NAME = "foo";
	private static final int TEST_INSTANCES = 5;

	private static final int SLEEP_SHORT_DURATION = 10;
	private static final int SLEEP_LONG_DURATION = 80;

	private static final int DOWNLOAD_COUNT = 3;

	private void addDefaultSet() throws MalformedURLException {
		addSingleEntry();
		cut.add(new URL("http://example.com/bar"), "bar");
		cut.add(new URL("http://example.com/baz"), "baz");
	}

	private void addNumberOfInstance(URL url, String filename, int noOfCopies) throws MalformedURLException {
		for (int i = 0; i < noOfCopies; i++) {
			cut.add(url, filename);
		}
	}

	private void addSingleEntry() throws MalformedURLException {
		addNumberOfInstance(new URL(TEST_ADDRESS), TEST_FILE_NAME, 1);
	}

	@Before
	public void setUp() throws Exception {
		ddl = mock(DataDownloader.class);
		assertThat(ddl, notNullValue());
		when(ddl.download(any(URL.class))).thenReturn("42".getBytes(StandardCharsets.UTF_8));
		
		actions = mock(FileLoaderAction.class);
		assertThat(actions, notNullValue());
		
		// mimic old behavior
		when(actions.beforeFileAdd(any(URL.class), any(String.class))).thenReturn(true);
		when(actions.beforeProcessItem(any(DownloadItem.class))).thenReturn(true);
		
		cut = new FileLoader(Files.createTempDirectory("FileLoaderTest").toFile(), 1, ddl, actions);
	}
	
	@After
	public void tearDown() throws Exception {
		cut.shutdown();
	}
	
	@Test
	public void testAddBeforeFileAdd() throws Exception {
		addSingleEntry();
		
		verify(actions).beforeFileAdd(any(URL.class), any(String.class));
	}
	
	@Test
	public void testAddBeforeFileAddNegative() throws Exception {
		when(actions.beforeFileAdd(any(URL.class), any(String.class))).thenReturn(false);
		
		addSingleEntry();
		
		verify(actions).beforeFileAdd(any(URL.class), any(String.class));
		verify(actions, never()).afterFileAdd(any(URL.class), any(String.class));
	}

	@Test
	public void testAddAfterFileAdd() throws Exception {
		addSingleEntry();
		
		verify(actions).afterFileAdd(any(URL.class), any(String.class));
	}
	
	@Test
	public void testAddAlreadyInQueue() throws Exception {
		addNumberOfInstance(new URL(TEST_ADDRESS), TEST_FILE_NAME, TEST_INSTANCES);

		verify(actions, after(DEFAULT_TIMEOUT).atMost(2)).afterFileAdd(any(URL.class), any(String.class));
	}

	@Test
	public void testSetDownloadSleepShort() throws Exception {
		cut.setDownloadSleep(SLEEP_SHORT_DURATION);

		addDefaultSet();
		
		verify(actions, timeout(DEFAULT_TIMEOUT).times(DOWNLOAD_COUNT)).afterFileAdd(any(URL.class), any(String.class));
		verify(actions, timeout(DEFAULT_TIMEOUT).times(DOWNLOAD_COUNT)).afterFileDownload(any(byte[].class),
				any(File.class), any(URL.class));
	}

	@Test
	public void testSetDownloadSleepLong() throws Exception {
		cut.setDownloadSleep(SLEEP_LONG_DURATION);
		addDefaultSet();
		
		verify(actions, timeout(DEFAULT_TIMEOUT).atLeast(2)).afterFileAdd(any(URL.class), any(String.class));
		verify(actions, timeout(DEFAULT_TIMEOUT).atLeast(2)).afterFileDownload(any(byte[].class), any(File.class), any(URL.class));
	}

	@Test
	public void testClearQueue() throws Exception {
		addDefaultSet();
		cut.clearQueue();

		verify(actions).afterClearQueue();
		verify(actions, after(DEFAULT_TIMEOUT).atMost(1)).afterFileDownload(any(byte[].class), any(File.class),
				any(URL.class));
	}

	@Test
	public void testShutdown() throws Exception {
		addDefaultSet();
		cut.shutdown();
		
		verify(actions).afterClearQueue();
	}
	
	@Test
	public void testFailToLoadPage() throws Exception {
		when(ddl.download(any(URL.class))).thenThrow(new PageLoadException(HttpStatus.NOT_FOUND_404));
		cut.setDownloadSleep(0);
		
		addSingleEntry();

		verify(actions, timeout(DEFAULT_TIMEOUT)).onPageLoadException(any(PageLoadException.class));
	}
	
	@Test
	public void testFailToWriteFile() throws Exception {
		when(ddl.download(any(URL.class))).thenThrow(new IOException("Failed to write"));
		cut.setDownloadSleep(0);
		
		addSingleEntry();

		verify(actions, timeout(DEFAULT_TIMEOUT)).onIOException(any(IOException.class));
	}
	
	@Test
	public void testBeforeProcessItem() throws Exception {
		when(actions.beforeProcessItem(any(DownloadItem.class))).thenReturn(false);
		cut.setDownloadSleep(0);
		
		addSingleEntry();

		verify(actions, after(DEFAULT_TIMEOUT).never()).afterProcessItem(any(DownloadItem.class));
	}
	
	@Test
	public void testUnhandledWorkerException() throws Exception {
		when(ddl.download(any(URL.class))).thenThrow(new IllegalArgumentException("Testing..."));
		
		addSingleEntry();
		
		verify(actions, after(DEFAULT_TIMEOUT).never()).afterProcessItem(any(DownloadItem.class));
	}
}

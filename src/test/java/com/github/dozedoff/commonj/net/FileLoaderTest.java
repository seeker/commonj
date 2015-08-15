package com.github.dozedoff.commonj.net;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FileLoaderTest {
	FileLoader cut;
	DataDownloader ddl;
	FileLoaderAction actions;

	@Before
	public void setUp() throws Exception {
		ddl = mock(DataDownloader.class);
		assertThat(ddl, notNullValue());
		when(ddl.download(any(URL.class))).thenReturn("42".getBytes());
		
		actions = mock(FileLoaderAction.class);
		assertThat(actions, notNullValue());
		
		// mimic old behavior
		when(actions.beforeFileAdd(any(URL.class), any(String.class))).thenReturn(true);
		when(actions.beforeProcessItem(any(DownloadItem.class))).thenReturn(true);
		
		cut = new FileLoader(Files.createTempDirectory("FileLoaderTest").toFile(), 1, ddl, actions);
		cut.setDownloadSleep(0);
	}
	
	@After
	public void tearDown() throws Exception {
		cut.shutdown();
	}
	
	@Test
	public void testAddBeforeFileAdd() throws Exception {
		cut.add(new URL("http://example.com"), "foo");
		
		verify(actions).beforeFileAdd(any(URL.class), any(String.class));
	}
	
	@Test
	public void testAddBeforeFileAddNegative() throws Exception {
		when(actions.beforeFileAdd(any(URL.class), any(String.class))).thenReturn(false);
		
		cut.add(new URL("http://example.com"), "foo");
		
		verify(actions).beforeFileAdd(any(URL.class), any(String.class));
		verify(actions, never()).afterFileAdd(any(URL.class), any(String.class));
	}

	@Test
	public void testAddAfterFileAdd() throws Exception {
		cut.add(new URL("http://example.com"), "foo");
		
		verify(actions).afterFileAdd(any(URL.class), any(String.class));
	}
	
	@Test
	public void testAddAlreadyInQueue() throws Exception {
		cut.setDownloadSleep(70);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com"), "foo");

		verify(actions, timeout(200).times(2)).afterFileAdd(any(URL.class), any(String.class));
	}

	@Test
	@Ignore
	public void testSetDownloadSleepShort() throws Exception {
		cut.setDownloadSleep(10);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		
		Thread.sleep(200);
		
	//	assertThat(cut.getUrls().size(), is(2));
		fail("Add correct asserts / verify");
	}
	
	@Test
	@Ignore
	public void testSetDownloadSleepLong() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		
		Thread.sleep(200);
		
		//assertThat(cut.getUrls().size(), is(1));
		fail("Add correct asserts / verify");
	}

	@Test
	@Ignore
	public void testClearQueue() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		cut.add(new URL("http://example.com/baz"), "baz");
		cut.clearQueue();
		
		Thread.sleep(200);
		
		//assertThat(cut.getUrls().size(), is(1));
		fail("Add correct asserts / verify");
	}

	@Test
	@Ignore
	public void testShutdown() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		cut.add(new URL("http://example.com/baz"), "baz");
		cut.shutdown();
		
		Thread.sleep(200);
		
		//assertThat(cut.getUrls().size(), is(1));
		fail("Add correct asserts / verify");
	}
	
	@Test
	@Ignore
	public void testFailToLoadPage() throws Exception {
		when(ddl.download(any(URL.class))).thenThrow(new PageLoadException(404));
		
		cut.add(new URL("http://example.com"), "foo");
		
		Thread.sleep(200);
		
		//assertThat(cut.isPleCalled(), is(true));
		fail("Add correct asserts / verify");
	}
	
	@Test
	@Ignore
	public void testFailToWriteFile() throws Exception {
		when(ddl.download(any(URL.class))).thenThrow(new IOException("Failed to write"));
		
		cut.add(new URL("http://example.com"), "foo");
		
		Thread.sleep(200);
		
		//assertThat(cut.isIoeCalled(), is(true));
		fail("Add correct asserts / verify");
	}
}

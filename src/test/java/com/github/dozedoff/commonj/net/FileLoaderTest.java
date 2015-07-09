package com.github.dozedoff.commonj.net;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileLoaderTest {
	Dummy cut;
	
	class Dummy extends FileLoader {
		// Download parameters
		private LinkedList<byte[]> data = new LinkedList<byte[]>();
		private LinkedList<File> files = new LinkedList<File>();
		private LinkedList<URL> urls = new LinkedList<URL>();

		public LinkedList<byte[]> getData() {
			return data;
		}

		public LinkedList<File> getFiles() {
			return files;
		}

		public LinkedList<URL> getUrls() {
			return urls;
		}

		public Dummy(File workingDir, int fileQueueWorkers, DataDownloader ddl) {
			super(workingDir, fileQueueWorkers, ddl);
		}

		@Override
		protected void afterFileDownload(byte[] data, File fullpath, URL url) {
			this.data.add(data);
			this.files.add(fullpath);
			this.urls.add(url);
		}
	}

	@Before
	public void setUp() throws Exception {
		DataDownloader ddl = mock(DataDownloader.class);
		when(ddl.download(any(URL.class))).thenReturn("42".getBytes());
		
		assertThat(ddl, notNullValue());
		
		cut = new Dummy(Files.createTempDirectory("FileLoaderTest").toFile(), 1, ddl);
		cut.setDownloadSleep(0);
	}
	
	@After
	public void tearDown() throws Exception {
		cut.shutdown();
	}

	@Test(timeout=1000)
	public void testAdd() throws Exception {
		cut.add(new URL("http://example.com"), "foo");
		
		while(cut.getUrls().size() < 1) {
			// spin wait
		}
		
		assertThat(cut.getUrls().size(), is(1));
	}
	
	@Test
	public void testAddAlreadyInQueue() throws Exception {
		cut.setDownloadSleep(100);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com"), "foo");
		cut.setDownloadSleep(0);

		Thread.sleep(200);
		
		assertThat(cut.getUrls().size(), is(1));
	}

	@Test
	public void testSetDownloadSleepShort() throws Exception {
		cut.setDownloadSleep(10);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		
		Thread.sleep(200);
		
		assertThat(cut.getUrls().size(), is(2));
	}
	
	@Test
	public void testSetDownloadSleepLong() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		
		Thread.sleep(200);
		
		assertThat(cut.getUrls().size(), is(1));
	}

	@Test
	public void testClearQueue() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		cut.add(new URL("http://example.com/baz"), "baz");
		cut.clearQueue();
		
		Thread.sleep(200);
		
		assertThat(cut.getUrls().size(), is(1));
	}

	@Test
	public void testShutdown() throws Exception {
		cut.setDownloadSleep(150);
		cut.add(new URL("http://example.com"), "foo");
		cut.add(new URL("http://example.com/bar"), "bar");
		cut.add(new URL("http://example.com/baz"), "baz");
		cut.shutdown();
		
		Thread.sleep(200);
		
		assertThat(cut.getUrls().size(), is(1));
	}
}

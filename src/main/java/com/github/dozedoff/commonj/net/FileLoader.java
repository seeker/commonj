/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for downloading files from the Internet.
 */
public class FileLoader {
	private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

	protected LinkedBlockingQueue<DownloadItem> downloadList = new LinkedBlockingQueue<DownloadItem>();
	private LinkedList<DownloadWorker> workers = new LinkedList<>();

	/** Delay between downloads. This is used to limit the number of connections **/
	protected int downloadSleep = 1000;
	protected int fileQueueWorkers;

	private DataDownloader dataDownloader;
	private FileLoaderAction actions;

	protected File workingDir;

	/**
	 * Use {@link FileLoader#FileLoader(File, int, DataDownloader) instead.}
	 * @param workingDir
	 * @param fileQueueWorkers
	 */
	@Deprecated
	public FileLoader(File workingDir, int fileQueueWorkers) {
		this(workingDir,fileQueueWorkers,new GetBinary());
	}
	
	@Deprecated
	public FileLoader(File workingDir, int fileQueueWorkers, DataDownloader dataDownloader) {
		// TODO when this constructor is removed, change the protected methods to private
		this(workingDir, fileQueueWorkers, dataDownloader, new FileLoaderActionDefault());
	}
	
	public FileLoader(File workingDir, int fileQueueWorkers, DataDownloader dataDownloader, FileLoaderAction actions) {
		this.workingDir = workingDir;
		this.fileQueueWorkers = fileQueueWorkers;
		this.dataDownloader = dataDownloader;
		this.actions = actions;
		setUp(fileQueueWorkers);
	}

	/**
	 * Run before a file is added to the list.
	 * 
	 * @param url
	 *            URL that was added
	 * @param fileName
	 *            relative path to working directory
	 * @return if true operation will continue
	 */
	protected boolean beforeFileAdd(URL url, String fileName) {
		return actions.beforeFileAdd(url, fileName);
	}

	/**
	 * Run after a file was added to the list.
	 * 
	 * @param url
	 *            URL that was added
	 * @param fileName
	 *            relative path to working directory
	 */
	protected void afterFileAdd(URL url, String fileName) {
		actions.afterFileAdd(url, fileName);
	}

	public void add(URL url, String fileName) {
		if (!beforeFileAdd(url, fileName)) {
			return;
		}

		DownloadItem toadd = new DownloadItem(url, fileName);

		if (downloadList.contains(toadd)) {
			logger.info("Ignoring {} ({}), already in download queue", toadd.getImageUrl(), toadd.getImageName());
			return;
		}

		downloadList.add(toadd);

		afterFileAdd(url, fileName);
	}

	/**
	 * Set the delay between file downloads. Used to limit the number of connections.
	 * 
	 * @param sleep
	 *            time between downloads in milliseconds
	 */
	public void setDownloadSleep(int sleep) {
		this.downloadSleep = sleep;
	}

	public void clearQueue() {
		downloadList.clear();
		logger.info("Download queue cleared");
		afterClearQueue();
	}

	/**
	 * Called after the queue has been cleared.
	 */
	protected void afterClearQueue() {
		actions.afterClearQueue();
	}

	/**
	 * Download a file, how the data is used is handled in the method afterFileDownload
	 * 
	 * @param url
	 *            URL to save
	 * @param savePath
	 *            relative save path
	 */
	private void loadFile(URL url, File savePath) {
		File fullPath = new File(workingDir, savePath.toString());

		try {
			Thread.sleep(downloadSleep);
		} catch (InterruptedException ie) {
		}

		byte[] data = null;
		try {
			data = dataDownloader.download(url);
			afterFileDownload(data, fullPath, url);
		} catch (PageLoadException ple) {
			onPageLoadException(ple);
		} catch (IOException ioe) {
			onIOException(ioe);
		}
	}

	/**
	 * Called when a server could be contacted, but an error code was returned.
	 * 
	 * @param ple
	 *            the PageLoadException that was thrown
	 */
	protected void onPageLoadException(PageLoadException ple) {
		actions.onPageLoadException(ple);
	}

	/**
	 * Called when a page / File could not be loaded due to an IO error.
	 * 
	 * @param ioe
	 *            the IOException that was thrown
	 */
	protected void onIOException(IOException ioe) {
		actions.onIOException(ioe);
	}

	/**
	 * Called when the file was successfully downloaded.
	 * 
	 * @param data
	 *            the downloaded file
	 * @param fullpath
	 *            the absolute filepath
	 * @param url
	 *            the url of the file
	 */
	protected void afterFileDownload(byte[] data, File fullpath, URL url) {
		actions.afterFileDownload(data, fullpath, url);
	}

	private void setUp(int fileWorkers) {
		logger.debug("Setting up FileLoader with {} workers", fileWorkers);
		logger.debug("Creating worker threads");
		for (int i = 0; i < fileWorkers; i++) {
			workers.add(new DownloadWorker());
		}

		logger.debug("Starting worker threads");
		for (DownloadWorker t : workers) {
			t.start();
		}

		logger.debug("FileLoader setup complete");
	}

	public void shutdown() {
		logger.info("Shutting down FileLoader...");
		clearQueue();

		logger.debug("Stopping worker threads...");
		for (DownloadWorker t : workers) {
			t.kill();
			t.interrupt();
		}

		logger.debug("Waiting for worker threads to die...");
		for (DownloadWorker t : workers) {
			try {
				t.join();
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}

		logger.debug("FileLoader shutdown complete");
	}

	/**
	 * Called after a worker has processed an item from the list.
	 * 
	 * @param di
	 *            the DownloadItem that was processed
	 */
	protected void afterProcessItem(DownloadItem di) {
		actions.afterProcessItem(di);
	}

	/**
	 * Called before a worker processes an item from the list.
	 * 
	 * @param di
	 *            the DownloadItem that will be processed
	 * @return return true if the item should be processed, or false to discard
	 */
	protected boolean beforeProcessItem(DownloadItem di) {
		return actions.beforeProcessItem(di);
	}

	class DownloadWorker extends Thread {
		private boolean stopped = false;

		public DownloadWorker() {
			super("Download Worker");

			Thread.currentThread().setPriority(2);
		}

		public void kill() {
			this.stopped = true;
		}

		@Override
		public void run() {
			DownloadItem di = null;
			while (!stopped) {
				try {
					di = downloadList.take();

					if (!beforeProcessItem(di)) {
						continue;
					}

					loadFile(di.getImageUrl(), new File(di.getImageName()));
					afterProcessItem(di);

				} catch (InterruptedException ie) {
					interrupt(); // otherwise it will reset it's own interrupt flag
					logger.debug("FileLoader download worker was interrupted");
				} catch (Exception e) {
					Object[] logParams = { e, di.getImageUrl(), di.getImageName() };
					logger.error("Download Worker failed with {}, Parameters: URL: {} ImageName: {}", logParams);
				}
			}
			logger.debug("FileLoader Download worker has died gracefully");
		}
	}
}

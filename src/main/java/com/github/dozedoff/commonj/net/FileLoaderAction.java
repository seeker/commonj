/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Interface for the event hooks in the {@link FileLoader}.
 * 
 * @author Nicholas Wright
 *
 */
public interface FileLoaderAction {
	/**
	 * Called before a download is added to the queue.
	 * 
	 * @param url to be downloaded
	 * @param fileName of the file to store the download
	 * @return true if the file should be added to the queue
	 */
	public boolean beforeFileAdd(URL url, String fileName);
	
	/**
	 * Called after a download is added to the queue.
	 * 
	 * @param url of the download
	 * @param fileName of the download
	 */
	public void afterFileAdd(URL url, String fileName);
	
	/**
	 * Called after the queue has been cleared with {@link FileLoader#clearQueue()}.
	 */
	public void afterClearQueue();
	
	/**
	 * Called if the page failed to load due to a error in the request (eg. Not found, 404, 503, ect.)
	 * @param ple the exception that caused this hook to be called
	 */
	public void onPageLoadException(PageLoadException ple);
	
	/**
	 * Called if there is a error in the network communication or accessing the filesystem.
	 * @param ioe the exception that caused this hook to be called
	 */
	public void onIOException(IOException ioe);
	
	/**
	 * Called after the data was successfully downloaded
	 * @param data that was downloaded
	 * @param fullpath the absolute path that was resolved from the working directory and filename
	 * @param url from which the data originated
	 */
	public void afterFileDownload(byte[] data, File fullpath, URL url);
	
	/**
	 * Called after a worker has processed an item from the queue.
	 * 
	 * @param di download that was processed
	 */
	public void afterProcessItem(DownloadItem di);
	
	/**
	 * Called before a worker processes an item from the queue.
	 * 
	 * @param di download that the worker will process
	 * @return if false, the item will be skipped
	 */
	public boolean beforeProcessItem(DownloadItem di);
}

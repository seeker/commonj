/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface FileLoaderAction {
	public boolean beforeFileAdd(URL url, String fileName);
	public void afterFileAdd(URL url, String fileName);
	public void afterClearQueue();
	public void onPageLoadException(PageLoadException ple);
	public void onIOException(IOException ioe);
	public void afterFileDownload(byte[] data, File fullpath, URL url);
	public void afterProcessItem(DownloadItem di);
	public boolean beforeProcessItem(DownloadItem di);
}

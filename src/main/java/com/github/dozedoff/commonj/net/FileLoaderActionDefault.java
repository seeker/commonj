/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class replicates the behavior of the abstract FileLoader.
 * 
 * @author Nicholas Wright
 */
public class FileLoaderActionDefault implements FileLoaderAction {
	private static final Logger logger = LoggerFactory.getLogger(FileLoaderActionDefault.class);
	
	@Override
	public boolean beforeFileAdd(URL url, String fileName) {
		return true;
	}

	@Override
	public void afterFileAdd(URL url, String fileName) {
	}

	@Override
	public void afterClearQueue() {
	}

	@Override
	public void onPageLoadException(PageLoadException ple) {
		logger.warn("Unable to load {} , response is {}", ple.getUrl(), ple.getResponseCode());
	}

	@Override
	public void onIOException(IOException ioe) {
		logger.warn("Unable to load page {}", ioe.getMessage());
	}

	@Override
	public void afterFileDownload(byte[] data, File fullpath, URL url) {
	}

	@Override
	public void afterProcessItem(DownloadItem di) {
	}

	@Override
	public boolean beforeProcessItem(DownloadItem di) {
		return true;
	}
}

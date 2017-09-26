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
 * This class provides default behaviour for {@link FileLoader}. All files are
 * downloaded and errors are logged.
 * 
 * @author Nicholas Wright
 */
public class FileLoaderActionDefault implements FileLoaderAction {
	private static final Logger logger = LoggerFactory.getLogger(FileLoaderActionDefault.class);

	/**
	 * Always accept files.
	 * 
	 * @param url
	 *            {@inheritDoc}
	 * @param fileName
	 *            {@inheritDoc}
	 * @return is always true
	 */
	@Override
	public boolean beforeFileAdd(URL url, String fileName) {
		return true;
	}

	/**
	 * No action performed.
	 * 
	 * @param url
	 *            {@inheritDoc}
	 * @param fileName
	 *            {@inheritDoc}
	 */
	@Override
	public void afterFileAdd(URL url, String fileName) {
	}

	/**
	 * No action performed.
	 */
	@Override
	public void afterClearQueue() {
	}

	/**
	 * Log the page load error.
	 *
	 * @param ple
	 *            {@inheritDoc}
	 */
	@Override
	public void onPageLoadException(PageLoadException ple) {
		logger.warn("Unable to load {} , response is {}", ple.getUrl(), ple.getResponseCode());
	}

	/**
	 * Log the IO error.
	 *
	 * @param ioe
	 *            {@inheritDoc}
	 */
	@Override
	public void onIOException(IOException ioe) {
		logger.warn("Unable to load page {}", ioe.getMessage());
	}

	/**
	 * No action performed.
	 * 
	 * @param data
	 *            {@inheritDoc}
	 * @param url
	 *            {@inheritDoc}
	 */
	@Override
	public void afterFileDownload(byte[] data, File fullpath, URL url) {
	}

	/**
	 * No action performed.
	 * 
	 * @param di
	 *            {@inheritDoc}
	 */
	@Override
	public void afterProcessItem(DownloadItem di) {
	}

	/**
	 * No action performed.
	 * 
	 * @param di
	 *            {@inheritDoc}
	 */
	@Override
	public boolean beforeProcessItem(DownloadItem di) {
		return true;
	}
}

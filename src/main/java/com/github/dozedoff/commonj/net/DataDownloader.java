/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.io.IOException;
import java.net.URL;

/**
 * Interface for downloading binary data from {@link URL}s.
 * @author Nicholas Wright
 *
 */
public interface DataDownloader {
	/**
	 * Download the the {@link URL} as a array of bytes.
	 * 
	 * @param url to download
	 * @return a array of bytes containing the target of the {@link URL}
	 * @throws PageLoadException if there is a error accessing the {@link URL} (not found, forbidden, ect.)
	 * @throws IOException if there is a underlying network error
	 */
	byte[] download (URL url) throws PageLoadException, IOException;
}

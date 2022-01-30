/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods to work with files.
 * 
 * @author Nicholas Wright
 *
 */
public class FileIO {
	private static final Logger logger = LoggerFactory.getLogger(FileIO.class);

	/**
	 * Quietly close the {@link FileInputStream}. If there is a error, it will
	 * be logged.
	 * 
	 * @param stream
	 *            to close
	 */
	public static void closeFileInputStream(FileInputStream stream) {
		if (stream == null) {
			return;
		}

		try {
			stream.close();
		} catch (IOException e) {
			logger.warn("Failed to close FileInputStream, {}", e.getMessage());
		}
	}
}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIO {
	private static final Logger logger = LoggerFactory.getLogger(FileIO.class);

	@Deprecated
	public static FileInputStream openAsFileInputStream(File file) {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (IOException e) {
			logger.warn("Failed to open file {}, {}", file, e.getMessage());
		}

		return fis;
	}

	@Deprecated
	public static MappedByteBuffer openReadOnlyBuffer(File file) {
		FileInputStream fis = openAsFileInputStream(file);
		MappedByteBuffer buffer = null;

		if (fis == null) {
			return null;
		}

		FileChannel channel = fis.getChannel();

		try {
			buffer = channel.map(MapMode.READ_ONLY, 0, file.length());
		} catch (IOException e) {
			logger.warn("Failed to map {} to buffer, {}", file, e.getMessage());
		}

		return buffer;
	}

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

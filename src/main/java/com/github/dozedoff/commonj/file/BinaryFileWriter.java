/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BinaryFileWriter {
	/**
	 * Use {@link BinaryFileWriter#write(byte[], Path)} instead.
	 */
	@Deprecated
	public void write(byte[] byteData, String savePath) throws IllegalArgumentException, IOException {

		if (savePath == null || "".equals(savePath)) {
			throw new IllegalArgumentException("Filepath is invalid");
		}

		write(byteData, Paths.get(savePath));
	}

	public void write(byte[] byteData, Path savePath) throws IllegalArgumentException, IOException {
		if (savePath == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}

		if (byteData == null) {
			throw new IllegalArgumentException("Data cannot be null");
		}

		if (savePath != null && savePath.getParent() != null) {
			Files.createDirectories(savePath.getParent());
		}

		try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(savePath, CREATE, TRUNCATE_EXISTING))) {
			os.write(byteData);
		}
	}
}

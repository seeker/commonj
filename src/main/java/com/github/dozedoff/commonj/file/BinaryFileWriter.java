/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
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

/**
 * Convenience class to write binary data to files.
 * 
 * @author Nicholas Wright
 *
 */
public class BinaryFileWriter {
	/**
	 * Write the given data to the specified path. Creates missing directories in the path.
	 * 
	 * @param byteData
	 *            data to write
	 * @param savePath
	 *            path to save data to
	 * @throws IllegalArgumentException
	 *             if the provided data or path are invalid
	 * @throws IOException
	 *             if there is a error writing the file
	 */
	public void write(byte[] byteData, Path savePath) throws IllegalArgumentException, IOException {
		if (savePath == null) {
			throw new IllegalArgumentException("Path cannot be null");
		}

		if (byteData == null) {
			throw new IllegalArgumentException("Data cannot be null");
		}

		if (savePath.equals(Paths.get(""))) {
			throw new IllegalArgumentException("Filepath is invalid");
		}

		Path parent = savePath.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(savePath, CREATE, TRUNCATE_EXISTING))) {
			os.write(byteData);
		}
	}
}

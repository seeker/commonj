/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Filter paths for regular files, based on {@link Files#isRegularFile(Path, java.nio.file.LinkOption...)}.
 * 
 * @author Nicholas Wright
 *
 */
public class FileFilter implements Filter<Path> {

	/**
	 * Check if the file is a regular file.
	 * 
	 * @param entry
	 *            {@inheritDoc}
	 */
	@Override
	public boolean accept(Path entry) throws IOException {
		if (entry == null) {
			return false;
		}

		return Files.isRegularFile(entry);
	}
}

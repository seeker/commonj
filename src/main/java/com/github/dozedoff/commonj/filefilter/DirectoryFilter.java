/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Path filter to filter directories.
 * 
 * @author Nicholas Wright
 *
 */
public class DirectoryFilter implements Filter<Path> {

	/**
	 * A path is accepted if it is a directory.
	 * 
	 * @param entry
	 *            {@inheritDoc}
	 */
	@Override
	public boolean accept(Path entry) throws IOException {
		if (entry == null) {
			return false;
		}

		return Files.isDirectory(entry);
	}
}

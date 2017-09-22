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

public class DirectoryFilter implements Filter<Path> {
	@Override
	public boolean accept(Path entry) throws IOException {
		if (entry == null) {
			return false;
		}

		return Files.isDirectory(entry);
	}
}

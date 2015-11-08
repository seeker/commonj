/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryFilter implements FileFilter, Filter<Path> {
	/**
	 * Use {@link DirectoryFilter#accept(Path)} instead.
	 */
	@Deprecated
	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			return false;
		}
		
		try {
			return this.accept(pathname.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean accept(Path entry) throws IOException {
		if (entry == null) {
			return false;
		}

		return Files.isDirectory(entry);
	}
}

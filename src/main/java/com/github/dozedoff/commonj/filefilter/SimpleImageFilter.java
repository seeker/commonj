/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

public class SimpleImageFilter implements FileFilter, Filter<Path> {
	private static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "gif" };
	private final FileExtensionFilter fef = new FileExtensionFilter(IMAGE_EXTENSIONS);

	/**
	 * Use {@link SimpleImageFilter#accept(Path)} instead.
	 */
	@Deprecated
	@Override
	public boolean accept(File pathname) {
		try {
			return this.accept(pathname.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean accept(Path entry) throws IOException {
		return fef.accept(entry);
	}
}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;

public class SimpleImageFilter implements FileFilter {
	private static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "gif" };
	private final FileExtensionFilter fef = new FileExtensionFilter(IMAGE_EXTENSIONS);

	@Override
	public boolean accept(File pathname) {
		return fef.accept(pathname);
	}
}

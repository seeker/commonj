/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

public class SimpleImageFilter implements Filter<Path> {
	private static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "gif" };
	private final FileExtensionFilter fef = new FileExtensionFilter(IMAGE_EXTENSIONS);

	@Override
	public boolean accept(Path entry) throws IOException {
		return fef.accept(entry);
	}
}

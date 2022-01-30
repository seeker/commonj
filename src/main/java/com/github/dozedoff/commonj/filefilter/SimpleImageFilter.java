/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

/**
 * Convienience filter to filter paths for images, based on extension 'jpg', 'png' and 'gif'.
 * 
 * @author Nicholas Wright
 *
 */
public class SimpleImageFilter implements Filter<Path> {
	private static final String[] IMAGE_EXTENSIONS = { "jpg", "png", "gif" };
	private final FileExtensionFilter fef = new FileExtensionFilter(IMAGE_EXTENSIONS);

	/**
	 * Check if the files extension matches.
	 * 
	 * @param entry
	 *            {@inheritDoc}
	 */
	@Override
	public boolean accept(Path entry) throws IOException {
		return fef.accept(entry);
	}
}

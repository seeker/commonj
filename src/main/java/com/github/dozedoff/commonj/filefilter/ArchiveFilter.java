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

public class ArchiveFilter implements FileFilter, Filter<Path> {
	private static final String[] VALID_EXTENSIONS = { "7z", "XZ", "BZIP2", "GZIP", "TAR", "ZIP", "WIM", "ARJ", "CAB", "CHM", "CPIO", "CramFS",
			"DEB", "DMG", "FAT", "HFS", "ISO", "LZH", "LZMA", "MBR", "MSI", "NSIS", "NTFS", "RAR", "RPM", "SquashFS", "UDF", "VHD", "XAR",
			"Z" };
	private final FileExtensionFilter fef = new FileExtensionFilter(VALID_EXTENSIONS);

	/**
	 * Use {@link ArchiveFilter#accept(Path)} instead.
	 */
	// TODO REMOVE after 0.1.1
	@Deprecated
	@Override
	public boolean accept(File pathname) {
		return fef.accept(pathname);
	}

	@Override
	public boolean accept(Path arg0) throws IOException {
		return fef.accept(arg0);
	}
}

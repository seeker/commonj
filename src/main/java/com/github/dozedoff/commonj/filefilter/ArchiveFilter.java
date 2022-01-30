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
 * Path filter to filter archives based on file extension.
 * 
 * @author Nicholas Wright
 *
 */
public class ArchiveFilter implements Filter<Path> {
	private static final String[] VALID_EXTENSIONS = { "7z", "XZ", "BZIP2", "GZIP", "TAR", "ZIP", "WIM", "ARJ", "CAB", "CHM", "CPIO", "CramFS",
			"DEB", "DMG", "FAT", "HFS", "ISO", "LZH", "LZMA", "MBR", "MSI", "NSIS", "NTFS", "RAR", "RPM", "SquashFS", "UDF", "VHD", "XAR",
			"Z" };
	private final FileExtensionFilter fef = new FileExtensionFilter(VALID_EXTENSIONS);

	/**
	 * Accept the file if it's extension matches a known archive extension.
	 * 
	 * @param {@inheritDoc}
	 */
	@Override
	public boolean accept(Path arg0) throws IOException {
		return fef.accept(arg0);
	}
}

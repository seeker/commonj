/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;

public class ArchiveFilter implements FileFilter {
	private static final String[] VALID_EXTENSIONS = { "7z", "XZ", "BZIP2", "GZIP", "TAR", "ZIP", "WIM", "ARJ", "CAB", "CHM", "CPIO", "CramFS",
			"DEB", "DMG", "FAT", "HFS", "ISO", "LZH", "LZMA", "MBR", "MSI", "NSIS", "NTFS", "RAR", "RPM", "SquashFS", "UDF", "VHD", "XAR",
			"Z" };
	private final FileExtensionFilter fef = new FileExtensionFilter(VALID_EXTENSIONS);

	@Override
	public boolean accept(File pathname) {
		return fef.accept(pathname);
	}
}

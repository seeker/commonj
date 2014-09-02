/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;

public class ArchiveFilter implements FileFilter {
	private final String[] vaildExtensions = { "7z", "XZ", "BZIP2", "GZIP", "TAR", "ZIP", "WIM", "ARJ", "CAB", "CHM", "CPIO", "CramFS",
			"DEB", "DMG", "FAT", "HFS", "ISO", "LZH", "LZMA", "MBR", "MSI", "NSIS", "NTFS", "RAR", "RPM", "SquashFS", "UDF", "VHD", "XAR",
			"Z" };
	private final FileExtensionFilter fef = new FileExtensionFilter(vaildExtensions);

	@Override
	public boolean accept(File pathname) {
		return fef.accept(pathname);
	}
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileExtensionFilter implements FileFilter, Filter<Path> {
	private final List<String> validExtensions;

	public FileExtensionFilter(String... validExtensions) {
		if (validExtensions == null) {
			this.validExtensions = Collections.emptyList();
		} else {
			List<String> extensions = Arrays.asList(validExtensions);
			Set<String> uniqueExtensions = new HashSet<>(extensions);
			uniqueExtensions.remove(null);
			this.validExtensions = new ArrayList<String>(uniqueExtensions.size());

			for (String ext : uniqueExtensions) {
				this.validExtensions.add(ext.toLowerCase());
			}

			Collections.sort(this.validExtensions);
		}
	}

	/**
	 * Use {@link FileExtensionFilter#accept(Path) instead.}
	 */
	// TODO REMOVE after 0.1.1
	@Deprecated
	@Override
	public boolean accept(File pathname) {
		try {
			return accept(pathname.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean accept(Path pathname) throws IOException {
		if (!Files.isRegularFile(pathname)) {
			return false;
		}

		Path filename = pathname.getFileName();

		if (filename == null) {
			return false;
		}

		String name = filename.toString();

		int extensionIndex = name.lastIndexOf(".") + 1;

		if (extensionIndex <= 0) {
			return false;
		}

		String fileExtension = name.substring(extensionIndex).toLowerCase();

		if (Collections.binarySearch(validExtensions, fileExtension) >= 0) {
			return true;
		}

		return false;
	}
}

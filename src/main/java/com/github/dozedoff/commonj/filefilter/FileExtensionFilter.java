/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

public class FileExtensionFilter implements FileFilter {
	private final ArrayList<String> validExtensions;

	public FileExtensionFilter(String... validExtensions) {
		if (validExtensions == null) {
			this.validExtensions = new ArrayList<>(0);
		} else {
			this.validExtensions = new ArrayList<String>(validExtensions.length);
			createExtensionList(validExtensions);
		}
	}

	private void createExtensionList(String[] validExtensions) {
		for (String extension : validExtensions) {
			if (extension != null) {
				this.validExtensions.add(extension.toLowerCase());
			}
		}

		Collections.sort(this.validExtensions);
	}

	@Override
	public boolean accept(File pathname) {
		if (!pathname.isFile()) {
			return false;
		}

		String filename = pathname.getName();

		int extensionIndex = filename.lastIndexOf(".") + 1;

		if (extensionIndex <= 0) {
			return false;
		}

		String fileExtension = filename.substring(extensionIndex).toLowerCase();

		if (Collections.binarySearch(validExtensions, fileExtension) >= 0) {
			return true;
		}

		return false;
	}
}

/*  Copyright (C) 2013  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
			this.validExtensions.add(extension);
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
		String fileExtension = filename.substring(extensionIndex).toLowerCase();

		if (extensionIndex <= 0) {
			return false;
		}

		if (Collections.binarySearch(validExtensions, fileExtension) >= 0) {
			return true;
		}

		return false;
	}
}

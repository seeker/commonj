/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import java.io.File;

public class FileFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname == null) {
			return false;
		}

		return pathname.isFile();
	}
}

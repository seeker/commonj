/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Convenience class for reading text files. Handles file opening, buffering and closing.
 * 
 * @author Nicholas Wright
 * 
 */
public class TextFileReader {
	com.github.dozedoff.commonj.file.TextFileReader tfr = new com.github.dozedoff.commonj.file.TextFileReader();

	@Deprecated
	/**
	 *	use file.TextFileReader instead
	 */
	public String read(File path) throws IOException {
		return tfr.read(path);
	}

	@Deprecated
	/**
	 *	use file.TextFileReader instead
	 */
	public String read(InputStream is) throws IOException {
		return tfr.read(is);
	}

	@Deprecated
	/**
	 *	use file.TextFileReader instead
	 */
	public String read(String path) throws IOException {
		return tfr.read(path);
	}
}

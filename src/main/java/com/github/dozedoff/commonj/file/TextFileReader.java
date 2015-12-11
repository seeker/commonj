/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Convenience class for reading text files. Handles file opening, buffering and closing.
 */
public class TextFileReader {
	public String read(File path) throws IOException {
		FileReader fr = new FileReader(path);
		String readData = readData(fr);
		fr.close();

		return readData;
	}

	public String read(InputStream is) throws IOException {
		Reader reader = new InputStreamReader(is);
		String readData = readData(reader);
		reader.close();

		return readData;
	}

	private String readData(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		StringBuilder readData = new StringBuilder();
		String read;

		while ((read = br.readLine()) != null) {
			readData.append(read + "\n");
		}

		readData.deleteCharAt(readData.length() - 1);

		br.close();

		return readData.toString();
	}

	public String read(String path) throws IOException {
		return read(new File(path));
	}
}
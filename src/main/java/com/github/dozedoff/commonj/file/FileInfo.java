/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.File;
import java.nio.file.Path;

/**
 * A simple storage class for file information.
 */
public class FileInfo {
	Path file = null;
	long size = -1L;
	String hash = null;

	public FileInfo(File file, String hash) {
		super();
		this.file = file.toPath();
		this.hash = hash;
	}

	public FileInfo(Path file, String hash) {
		super();
		this.file = file;
		this.hash = hash;
	}

	public FileInfo(File file) {
		setFile(file);
	}

	public FileInfo(Path path) {
		setFile(path);
	}

	public File getFile() {
		if (file == null) {
			return null;
		}

		return file.toFile();
	}

	public long getSize() {
		return size;
	}

	public String getHash() {
		return hash;
	}

	public void setFile(File file) {
		if (file == null) {
			this.file = null;
		} else {
			this.file = file.toPath();
		}
	}

	public void setFile(Path file) {
		this.file = file;
	}

	public Path getFilePath() {
		return file;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean hasHash() {
		return hash == null ? false : true;
	}

	public boolean hasPath() {
		return file == null ? false : true;
	}

	public boolean hasSize() {
		return size == -1 ? false : true;
	}
}

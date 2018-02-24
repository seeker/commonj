/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.File;
import java.nio.file.Path;

// TODO move to aid project

/**
 * A simple storage class for file information.
 */
public class FileInfo {
	private Path file;
	private long size = -1L;
	private String hash;

	/**
	 * Create a new {@link FileInfo} with the given {@link File} and hash.
	 * 
	 * @param file
	 *            the path of the file
	 * @param hash
	 *            of this file
	 */
	public FileInfo(File file, String hash) {
		super();
		this.file = file.toPath();
		this.hash = hash;
	}

	/**
	 * Create a new {@link FileInfo} with the given {@link Path} and hash.
	 * 
	 * @param file
	 *            the path of the file
	 * @param hash
	 *            of this file
	 */
	public FileInfo(Path file, String hash) {
		super();
		this.file = file;
		this.hash = hash;
	}

	private static final Path convertToPath(File file) {
		if (file == null) {
			return null;
		} else {
			return file.toPath();
		}
	}

	/**
	 * Create a new {@link FileInfo} with the given path. The hash is set to null.
	 * 
	 * @param file
	 *            the path of the file
	 */
	public FileInfo(File file) {
		this.file = convertToPath(file);
	}

	/**
	 * Create a new {@link FileInfo} with the given path. The hash is set to null.
	 * 
	 * @param path
	 *            the path of the file
	 */
	public FileInfo(Path path) {
		this.file = path;
	}

	/**
	 * Get the {@link File} associated to this {@link FileInfo}.
	 * 
	 * @return the {@link File} or null if not set
	 */
	public File getFile() {
		if (file == null) {
			return null;
		}

		return file.toFile();
	}

	/**
	 * Get the size of this file
	 * 
	 * @return the size of the file
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Get the hash of this file.
	 * 
	 * @return the hash of the file or null if not set
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Set the path for this file
	 * 
	 * @param file
	 *            to set
	 */
	public void setFile(File file) {
		this.file = convertToPath(file);
	}

	/**
	 * Set the path for this file
	 * 
	 * @param file
	 *            to set
	 */
	public void setFile(Path file) {
		this.file = file;
	}

	/**
	 * Get the {@link Path} associated to this {@link FileInfo}.
	 * 
	 * @return the {@link Path} or null if not set
	 */
	public Path getFilePath() {
		return file;
	}

	/**
	 * Set the size of this file
	 * 
	 * @param size
	 *            of this file
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Set the hash of this file.
	 * 
	 * @param hash
	 *            of this file
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * Check if this {@link FileInfo} has a hash set
	 * 
	 * @return true if set
	 */
	public boolean hasHash() {
		return hash == null ? false : true;
	}

	/**
	 * Check if this {@link FileInfo} has a valid path set.
	 * 
	 * @return true if the path is valid
	 */
	public boolean hasPath() {
		return file == null ? false : true;
	}

	/**
	 * Check if this {@link FileInfo} has a valid size set.
	 * 
	 * @return true if the size is valid
	 */
	public boolean hasSize() {
		return size == -1 ? false : true;
	}
}

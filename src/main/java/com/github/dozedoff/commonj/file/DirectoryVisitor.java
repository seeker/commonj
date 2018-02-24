/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

/**
 * Convenience visitor to find all directories.
 * 
 * @author Nicholas Wright
 *
 */
public class DirectoryVisitor extends SimpleFileVisitor<Path> {
	private LinkedList<Path> directoryList;

	/**
	 * Create a new {@link DirectoryVisitor} with the given list.
	 * 
	 * @param directoryList
	 *            to store found directories in
	 */
	public DirectoryVisitor(LinkedList<Path> directoryList) {
		this.directoryList = directoryList;
	}

	/**
	 * Record the directory before entering it.
	 * 
	 * @param dir
	 *            {@inheritDoc}
	 * @param attrs
	 *            {@inheritDoc}
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		directoryList.add(dir);
		return FileVisitResult.CONTINUE;
	}
}

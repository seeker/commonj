/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

/**
 * Convenience Visitor to find files that match the given {@link FileFilter}.
 * 
 * @author Nicholas Wright
 *
 */
public class FilenameFilterVisitor extends SimpleFileVisitor<Path> {
	private Collection<Path> resultOutputList;
	private FileFilter fileFilter;

	/**
	 * Create a new Visitor that returns a list of paths that match the filter.
	 * 
	 * @param resultOutputList
	 *            list where matching paths will be stored
	 * @param fileFilter
	 *            filter to match paths
	 */
	public FilenameFilterVisitor(Collection<Path> resultOutputList, FileFilter fileFilter) {
		this.resultOutputList = resultOutputList;
		this.fileFilter = fileFilter;
	}

	/**
	 * Check if the file matches the provided filter.
	 * 
	 * @param file
	 *            {@inheritDoc}
	 * @param attrs
	 *            {@inheritDoc}
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (isAcceptedFile(file)) {
			resultOutputList.add(file);
		}

		return FileVisitResult.CONTINUE;
	}

	private boolean isAcceptedFile(Path file) {
		return fileFilter.accept(file.toFile());
	}
}

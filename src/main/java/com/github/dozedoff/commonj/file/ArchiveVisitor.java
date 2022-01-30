/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.github.dozedoff.commonj.filefilter.ArchiveFilter;

/**
 * Convenience Visitor for finding archives. Uses {@link ArchiveFilter} to match files by extension.
 * 
 * @author Nicholas Wright
 *
 */
public class ArchiveVisitor extends SimpleFileVisitor<Path> {
	private ArchiveFilter archiveFilter = new ArchiveFilter();
	private List<Path> archiveList;

	/**
	 * Create a new {@link ArchiveVisitor}, storing all found archives in the given list.
	 * 
	 * @param archiveList
	 *            to store archive paths
	 */
	public ArchiveVisitor(List<Path> archiveList) {
		if (archiveList == null) {
			throw new IllegalArgumentException("List cannot be null");
		}

		this.archiveList = archiveList;
	}

	/**
	 * Check a file against the {@link ArchiveFilter} and store it if accepted.
	 * 
	 * @param path
	 *            {@inheritDoc}
	 * @param attributes
	 *            {@inheritDoc}
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
		if (archiveFilter.accept(path)) {
			archiveList.add(path);
		}

		return FileVisitResult.CONTINUE;
	}
}

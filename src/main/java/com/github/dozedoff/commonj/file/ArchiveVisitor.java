/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

import com.github.dozedoff.commonj.filefilter.ArchiveFilter;

public class ArchiveVisitor extends SimpleFileVisitor<Path> {
	ArchiveFilter archiveFilter = new ArchiveFilter();
	LinkedList<Path> archiveList;

	public ArchiveVisitor(LinkedList<Path> archiveList) {
		if (archiveList == null) {
			throw new IllegalArgumentException("List cannot be null");
		}

		this.archiveList = archiveList;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
		if (archiveFilter.accept(file.toFile())) {
			archiveList.add(file);
		}

		return FileVisitResult.CONTINUE;
	}
}

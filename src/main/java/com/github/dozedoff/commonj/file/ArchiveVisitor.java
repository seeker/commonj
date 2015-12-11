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
import java.util.List;

import com.github.dozedoff.commonj.filefilter.ArchiveFilter;

public class ArchiveVisitor extends SimpleFileVisitor<Path> {
	ArchiveFilter archiveFilter = new ArchiveFilter();
	List<Path> archiveList;

	public ArchiveVisitor(List<Path> archiveList) {
		if (archiveList == null) {
			throw new IllegalArgumentException("List cannot be null");
		}

		this.archiveList = archiveList;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
		if (archiveFilter.accept(path)) {
			archiveList.add(path);
		}

		return FileVisitResult.CONTINUE;
	}
}

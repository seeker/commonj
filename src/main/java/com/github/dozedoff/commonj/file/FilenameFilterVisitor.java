/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
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

public class FilenameFilterVisitor extends SimpleFileVisitor<Path> {
	Collection<Path> resultOutputList;
	FileFilter fileFilter;

	public FilenameFilterVisitor(Collection<Path> resultOutputList, FileFilter fileFilter) {
		this.resultOutputList = resultOutputList;
		this.fileFilter = fileFilter;
	}

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

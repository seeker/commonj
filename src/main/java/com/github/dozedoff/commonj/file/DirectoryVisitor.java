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

public class DirectoryVisitor extends SimpleFileVisitor<Path> {
	LinkedList<Path> directoryList;

	public DirectoryVisitor(LinkedList<Path> directoryList) {
		this.directoryList = directoryList;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		directoryList.add(dir);
		return FileVisitResult.CONTINUE;
	}
}

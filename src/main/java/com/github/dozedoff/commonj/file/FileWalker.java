/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.filefilter.SimpleImageFilter;

/**
 * This class will search all folders and the respective subfolders for files and return them as a List of File items.
 */
public class FileWalker {
	private static final Logger logger = LoggerFactory.getLogger(FileWalker.class);

	public static LinkedList<Path> getAllDirectories(Path startDirectory) throws IOException {
		LinkedList<Path> directoryList = new LinkedList<>();
		Files.walkFileTree(startDirectory, new DirectoryVisitor(directoryList));
		return directoryList;
	}

	/**
	 * Returns all directories in the given directory.
	 * 
	 * @param currentDirectory
	 *            path to check
	 * @return a list of paths for directories
	 */
	public static LinkedList<Path> getCurrentDirectorySubdirectories(Path currentDirectory) {
		LinkedList<Path> subDirectories = new LinkedList<>();
		// TODO change return type to interface
		try {
			Iterator<Path> iter = Files.list(currentDirectory).iterator();

			while (iter.hasNext()) {
				Path path = iter.next();
				if (Files.isDirectory(path)) {
					subDirectories.add(path);
				}
			}
		} catch (IOException e) {
			logger.error("Filewalk for {} failed with {}", currentDirectory, e);
		}

		return subDirectories;
	}

	public static LinkedList<Path> getAllImages(Path startDirectory) throws IOException {
		return walkFileTreeWithFilter(startDirectory, new SimpleImageFilter());
	}

	public static LinkedList<Path> getCurrentFolderImages(Path currentFolder) throws IOException {
		LinkedList<Path> imageList = new LinkedList<>();
		Iterator<Path> iter = Files.list(currentFolder).iterator();

		SimpleImageFilter sif = new SimpleImageFilter();

		while (iter.hasNext()) {
			Path path = iter.next();

			if (sif.accept(path.toFile())) {
				imageList.add(path);
			}
		}

		return imageList;
	}

	public static LinkedList<Path> walkFileTreeWithFilter(Path startDirectory, FileFilter fileFilter) throws IOException {
		LinkedList<Path> foundFiles = new LinkedList<>();

		Files.walkFileTree(startDirectory, new FilenameFilterVisitor(foundFiles, fileFilter));

		return foundFiles;
	}

	public static LinkedList<Path> walkFileTree(Path... directories) throws IOException {
		ArrayList<Path> foundFiles = new ArrayList<Path>();
		for (Path directory : directories) {
			LinkedList<Path> newFiles = walkFileTreeWithFilter(directory, new com.github.dozedoff.commonj.filefilter.FileFilter());
			addWithoutDuplicates(foundFiles, newFiles);
		}

		return new LinkedList<>(foundFiles);
	}

	public static LinkedList<Path> walkFileTreePathList(List<Path> list) throws IOException {
		return walkFileTree(list.toArray(new Path[1]));
	}

	@Deprecated
	public static LinkedList<Path> walkFileTree(File... directories) throws IOException {
		Path[] paths = new Path[directories.length];

		for (int i = 0; i < directories.length; i++) {
			paths[i] = directories[i].toPath();
		}

		return walkFileTree(paths);
	}

	public static LinkedList<Path> walkFileTreeStringList(List<String> list) throws IOException {
		return walkFileTree(list.toArray(new String[1]));
	}

	public static LinkedList<Path> walkFileTree(String... directories) throws IOException {
		Path[] paths = new Path[directories.length];

		for (int i = 0; i < directories.length; i++) {
			paths[i] = Paths.get(directories[i]);
		}

		return walkFileTree(paths);
	}

	public static LinkedList<Path> walkFileTreeFileList(List<File> list) throws IOException {
		return walkFileTree(list.toArray(new File[1]));
	}

	private static void addWithoutDuplicates(ArrayList<Path> foundFiles, LinkedList<Path> newFiles) {
		foundFiles.ensureCapacity(foundFiles.size() + newFiles.size());

		for (Path file : newFiles) {
			int index = Collections.binarySearch(foundFiles, file);

			if (index < 0) {
				foundFiles.add(-index - 1, file);
			}
		}
	}
}
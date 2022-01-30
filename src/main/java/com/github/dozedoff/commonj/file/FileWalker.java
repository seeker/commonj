/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.filefilter.SimpleImageFilter;

/**
 * This class will search all folders and the respective subfolders for files and return them as a List of File items.
 */
public class FileWalker {
	private static final Logger logger = LoggerFactory.getLogger(FileWalker.class);

	/**
	 * Find all directories in the given path.
	 * 
	 * @param startDirectory
	 *            path to start search from
	 * @return a list of paths to found directories
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
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

	/**
	 * Returns all images in the given directory. Uses {@link SimpleImageFilter} to find images.
	 * 
	 * @param startDirectory
	 *            to start search from
	 * @return a list of paths to found images
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> getAllImages(Path startDirectory) throws IOException {
		return walkFileTreeWithFilter(startDirectory, new FileFilter() {
			private SimpleImageFilter sif = new SimpleImageFilter();

			@Override
			public boolean accept(File pathname) {
				try {
					return sif.accept(pathname.toPath());
				} catch (IOException e) {
					logger.error("Failed to filter image {}: ", pathname, e);
					return false;
				}
			}
		});
	}

	/**
	 * Find images in the current directory.
	 * 
	 * @param currentFolder
	 *            directory to search
	 * @return a list of paths to found images
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> getCurrentFolderImages(Path currentFolder) throws IOException {
		LinkedList<Path> imageList = new LinkedList<>();
		Iterator<Path> iter = Files.list(currentFolder).iterator();

		SimpleImageFilter sif = new SimpleImageFilter();

		while (iter.hasNext()) {
			Path path = iter.next();

			if (sif.accept(path)) {
				imageList.add(path);
			}
		}

		return imageList;
	}

	/**
	 * Walk the given path with the provided {@link FileFilter}. Returns paths that match the filter.
	 * 
	 * @param startDirectory
	 *            to start walk from
	 * 
	 * @param fileFilter
	 *            to filter files
	 * @return a list of paths that matched the filter
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTreeWithFilter(Path startDirectory, FileFilter fileFilter) throws IOException {
		LinkedList<Path> foundFiles = new LinkedList<>();

		Files.walkFileTree(startDirectory, new FilenameFilterVisitor(foundFiles, fileFilter));

		return foundFiles;
	}

	/**
	 * Walk the given paths and return a list of regular files.
	 * 
	 * @param directories
	 *            paths to walk
	 * @return a list of paths to files
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTree(Path... directories) throws IOException {
		ArrayList<Path> foundFiles = new ArrayList<Path>();
		for (Path directory : directories) {
			LinkedList<Path> newFiles = walkFileTreeWithFilter(directory, new FileFilter() {
				private com.github.dozedoff.commonj.filefilter.FileFilter ff = new com.github.dozedoff.commonj.filefilter.FileFilter();

				@Override
				public boolean accept(File pathname) {
					try {
						return ff.accept(pathname.toPath());
					} catch (IOException e) {
						logger.error("Failed to filter {}: ", pathname, e);
						return false;
					}
				}
			});
			addWithoutDuplicates(foundFiles, newFiles);
		}

		return new LinkedList<>(foundFiles);
	}

	/**
	 * Walk the given paths and return a list of regular files.
	 * 
	 * @param list
	 *            paths to walk
	 * @return a list of paths to files
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTreePathList(List<Path> list) throws IOException {
		return walkFileTree(list.toArray(new Path[1]));
	}

	/**
	 * Walk the given paths and return a list of regular files.
	 * 
	 * @param list
	 *            paths to walk
	 * @return a list of paths to files
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTreeStringList(List<String> list) throws IOException {
		return walkFileTree(list.toArray(new String[1]));
	}

	/**
	 * Walk the given paths and return a list of regular files.
	 * 
	 * @param directories
	 *            paths to walk
	 * @return a list of paths to files
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTree(String... directories) throws IOException {
		Path[] paths = new Path[directories.length];

		for (int i = 0; i < directories.length; i++) {
			paths[i] = Paths.get(directories[i]);
		}

		return walkFileTree(paths);
	}

	/**
	 * Walk the given paths and return a list of regular files.
	 * 
	 * @param list
	 *            paths to walk
	 * @return a list of paths to files
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static LinkedList<Path> walkFileTreeFileList(List<File> list) throws IOException {
		return walkFileTreePathList(list.stream().map(file -> file.toPath()).collect(Collectors.toList()));
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
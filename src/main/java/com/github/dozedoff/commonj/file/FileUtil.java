/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with files, directories and paths.
 * 
 * @author Nicholas Wright
 *
 */
public class FileUtil {
	private static final String[] ILLEGAL_FILENAME_CHARS = { "/", "\\", ":", "?", "\"", "<", ">", "|" };
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Get the current working directory of the program.
	 * 
	 * @return the current working directory
	 */
	public static Path workingDir() {
		return Paths.get(System.getProperty("user.dir"));
	}

	/**
	 * Split a path string into individual tokens
	 * 
	 * @param path
	 *            to tokenize
	 * @return a list of tokens
	 */
	public static List<String> pathTokenList(String path) {
		LinkedList<String> resultList = new LinkedList<String>();

		Scanner scanner = new Scanner(path);
		scanner.useDelimiter("\\\\");
		// to handle network paths
		if (path.startsWith("\\\\")) {
			int endPos = path.indexOf("\\", 2);
			resultList.add(path.substring(0, endPos));
			scanner.next();
			scanner.next();
		}

		while (scanner.hasNext()) {
			resultList.add(scanner.next());
		}

		// check if the last entry (filename) has an extension, if so, split them
		if (resultList.peekLast() != null && resultList.peekLast().contains(".")) {
			String lastEntry = resultList.pollLast();
			int dotPosition = lastEntry.lastIndexOf(".");

			String filename = lastEntry.substring(0, dotPosition);
			String extension = lastEntry.substring(dotPosition);

			resultList.add(filename);
			resultList.add(extension);
		}

		scanner.close();

		return resultList;

	}

	/**
	 * Move a directory, sub-directories and files to the new location. src\a , dst will result in dst\a
	 * 
	 * @param source
	 *            the path of the source directory
	 * @param destination
	 *            the path of the destination directory
	 * @throws IOException
	 *             if there is an error accessing the files
	 */
	public static void moveDirectory(Path source, Path destination) throws IOException {
		Files.walkFileTree(source, new DirectoryMover(source, destination));
	}

	/**
	 * Move a file to the destination, retaining the the directory structure up to paths root.
	 * 
	 * @param source
	 *            file to move
	 * @param dstDirectory
	 *            to move the file to
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public static void moveFileWithStructure(Path source, Path dstDirectory) throws IOException {
		if (source == null) {
			throw new IllegalArgumentException("Source cannot be null");
		}
		
		if (dstDirectory == null) {
			throw new IllegalArgumentException("Destination directory cannot be null");
		}
		
		Path relativeSource = relativizeToRoot(source);
		Path destinationPath = dstDirectory.resolve(relativeSource);
		
		Path destinationParent = destinationPath.getParent();

		if(destinationParent == null) {
			throw new IllegalArgumentException("Destination path does not have a parent");
		}
		
		Files.createDirectories(destinationParent);

		Files.move(source, destinationPath);
	}

	/**
	 * Convert a path to it's string representation. If the path does not have a root, it is converted to a Microsoft
	 * UNC.
	 * 
	 * @param directory
	 *            path to convert
	 * @return the path in string form or null if the provided path was null
	 */
	static public String convertDirPathToString(Path directory) {
		if (directory == null) {
			return null;
		} else if ((directory.getRoot() != null) && (directory.equals(directory.getRoot()))) {
			return directory.toString().toLowerCase();
		} else {
			return directory.toString().toLowerCase() + "\\";
		}
	}

	/**
	 * Remove the root component from the path, returning a relative path. If the path is already relative, it will not be changed. C:\temp\
	 * becomes \temp\
	 * 
	 * @param path
	 *            path to remove root from
	 * @return a relative path
	 */
	static public Path removeDriveLetter(Path path) {
		if (path == null) {
			return null;
		}

		if (path.isAbsolute()) {
			return relativizeToRoot(path);
		} else {
			return path;
		}
	}
	
	private static Path relativizeToRoot(Path path) {
		Path root = path.getRoot();
		
		if(root == null) {
			throw new IllegalArgumentException("Path does not have a root component");
		}
		
		return root.relativize(path);
	}

	/**
	 * Remove the windows drive letter from a path.
	 * 
	 * @param path
	 *            to strip the drive letter from
	 * @return path without a drive letter
	 */
	static public String removeDriveLetter(String path) {
		if (path == null) {
			return null;
		}

		Path relPath = removeDriveLetter(Paths.get(path));
		
		Path filenamePath = relPath.getFileName();
		
		if(filenamePath == null) {
			throw new IllegalArgumentException("Filename was null, path had zero elements.");
		}
		
		String filename = filenamePath.toString();

		if (filename.contains(".")) {
			return relPath.toString();
		} else {
			return relPath.toString() + "\\";
		}
	}

	/**
	 * Check if the filename is valid on windows systems.
	 * 
	 * @param filename
	 *            to test
	 * @return true if valid on windows
	 */
	static public boolean hasValidWindowsFilename(String filename) {
		return hasValidWindowsFilename(Paths.get(filename));
	}

	/**
	 * Check if the filename is valid on windows systems.
	 * 
	 * @param fullpath
	 *            to test
	 * @return true if valid on windows
	 */
	static public boolean hasValidWindowsFilename(Path fullpath) {
		if (fullpath == null) {
			return false;
		}

		Path fileNameElement = fullpath.getFileName();

		if (fileNameElement == null) {
			return false;
		}

		String filename = fileNameElement.toString();

		if (filename.isEmpty()) {
			return false;
		}

		for (String illegalChar : ILLEGAL_FILENAME_CHARS) {
			if (filename.contains(illegalChar)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Sanitize the filename for use on windows, by replacing invalid characters with _.
	 * 
	 * @param filename
	 *            to sanitize
	 * @return a sanitized filename
	 */
	static public String sanitizeFilenameForWindows(String filename) {

		String sanitized = filename;

		for (String ic : ILLEGAL_FILENAME_CHARS) {
			sanitized = sanitized.replace(ic, "_");
		}

		return sanitized;
	}

	/**
	 * Visitor for moving entire directories
	 * 
	 * @author Nicholas Wright
	 *
	 */
	static class DirectoryMover extends SimpleFileVisitor<Path> {
		private Path currMoveDir;
		private Path dstDir;
		private Path srcDir;

		/**
		 * Create a new {@link DirectoryMover}.
		 * 
		 * @param srcDir
		 *            source directory to be moved
		 * @param dstDir
		 *            destination directory where the source should be moved to
		 */
		public DirectoryMover(Path srcDir, Path dstDir) {
			this.dstDir = dstDir;
			this.srcDir = srcDir.getParent();
		}

		/**
		 * Create the directory in the destination before entering the source directory, to mirror the directory
		 * structure.
		 * 
		 * @param dir
		 *            {@inheritDoc}
		 * @param attrs
		 *            {@inheritDoc}
		 */
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			File f = new File(dstDir.toFile(), srcDir.relativize(dir).toString());
			
			if(f.mkdirs() == false){
				logger.error("Failed to create directory {}", f);
			}
			
			currMoveDir = f.toPath();
			return super.preVisitDirectory(dir, attrs);
		}

		/**
		 * Move files on visit.
		 * 
		 * @param file
		 *            {@inheritDoc}
		 * @param attrs
		 *            {@inheritDoc}
		 */
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.move(file, currMoveDir.resolve(file.getFileName()));
			return super.visitFile(file, attrs);
		}

		/**
		 * Delete the source directory after visiting the directory.
		 * 
		 * @param arg0
		 *            {@inheritDoc}
		 * 
		 */
		@Override
		public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
			Files.delete(arg0);
			return super.postVisitDirectory(arg0, arg1);
		}
	}
}

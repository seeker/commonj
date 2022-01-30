/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.hash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.file.FileInfo;

/**
 * Class for hashing files in a directory.
 */
public class DirectoryHasher {
	private LinkedBlockingQueue<FileInfo> outputQueue;
	private final static Logger logger = LoggerFactory.getLogger(DirectoryHasher.class);
	private HashWorker hashWorker;
	private FilenameFilter filter;

	/**
	 * Create a new {@link DirectoryHasher} with the given queue to store results. A worker thread is created and
	 * started.
	 * 
	 * @param outputQueue
	 *            to store results for processed files
	 */
	public DirectoryHasher(LinkedBlockingQueue<FileInfo> outputQueue) {
		this.filter = new AcceptAllFilter();
		this.outputQueue = outputQueue;
		hashWorker = new HashWorker();
		hashWorker.setDaemon(true);
		hashWorker.start();
	}

	/**
	 * Set the filter to use when hashing a directory.
	 * 
	 * @param filter
	 *            to use for accepting files
	 */
	public void setFilter(FilenameFilter filter) {
		this.filter = filter;
	}

	/**
	 * Walk the directory and hash all files matching the set filter.
	 * 
	 * @param directory
	 *            to recursively hash
	 * @throws IOException
	 *             if there is an error accessing the filesystem
	 */
	public void hashDirectory(String directory) throws IOException {
		File dir = new File(directory);

		// check if the directory exists
		if (!dir.exists()) {
			throw new FileNotFoundException("Directory " + dir + " does not exist");
		}

		logger.info("Starting to walk {}", dir);
		java.nio.file.Files.walkFileTree(dir.toPath(), new DirectoryVisitor(filter));
	}

	/**
	 * Default filter, accepts all files.
	 * 
	 * @author Nicholas Wright
	 *
	 */
	class AcceptAllFilter implements FilenameFilter {
		/**
		 * Accept all files
		 * 
		 * @param dir
		 *            {@inheritDoc}
		 * @param name
		 *            {@inheritDoc}
		 */
		@Override
		public boolean accept(File dir, String name) {
			return true;
		}
	}


	class DirectoryVisitor extends SimpleFileVisitor<Path> {
		private FilenameFilter filter;

		/**
		 * Create a new directory visitor with the given filter.
		 * 
		 * @param filter
		 *            to use for filtering files
		 */
		public DirectoryVisitor(FilenameFilter filter) {
			this.filter = filter;
		}

		/**
		 * Add files that match the filter to the worker thread for processing.
		 * 
		 * @param file
		 *            {@inheritDoc}
		 * @param attrs
		 *            {@inheritDoc}
		 */
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (file == null) {
				return FileVisitResult.CONTINUE;
			}
			
			Path dir = file.getParent();
			
			if(dir == null) {
				return FileVisitResult.CONTINUE;
			}
			
			Path filename = file.getFileName();

			if (filename == null) {
				return FileVisitResult.CONTINUE;
			}
					

			if (filter.accept(dir.toFile(), filename.toString())) {
					FileInfo fi = new FileInfo(file.toFile(), null);
					hashWorker.addFile(fi);
				}
			return super.visitFile(file, attrs);
		}
	}

	/**
	 * This Tread hashes files and adds them to a queue.
	 */
	class HashWorker extends Thread {
		private LinkedBlockingQueue<FileInfo> inputQueue = new LinkedBlockingQueue<>();
		private LinkedList<FileInfo> workingList = new LinkedList<>();
		private HashMaker hash = new HashMaker();

		/**
		 * Add a file to the processing queue.
		 * 
		 * @param file
		 *            to add to the queue
		 */
		public void addFile(FileInfo file) {
			inputQueue.add(file);
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				// grab some work
				inputQueue.drainTo(workingList);

				// process items
				for (FileInfo f : workingList) {
					try {
						f.setHash(hash.hash(Files.readAllBytes(f.getFilePath())));
						outputQueue.add(f);
					} catch (IOException e) {
						logger.warn("Could not hash file: " + e.getMessage());
					}
				}

				workingList.clear();
			}
		}
	}
}

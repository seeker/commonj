/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.hash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.file.BinaryFileReader;
import com.github.dozedoff.commonj.file.FileInfo;

/**
 * Class for hashing files in a directory.
 */
public class DirectoryHasher {
	LinkedBlockingQueue<FileInfo> outputQueue = null;
	final static Logger logger = LoggerFactory.getLogger(DirectoryHasher.class);
	private HashWorker hashWorker;
	private FilenameFilter filter = null;

	public DirectoryHasher(LinkedBlockingQueue<FileInfo> outputQueue) {
		this.filter = new AcceptAllFilter();
		this.outputQueue = outputQueue;
		hashWorker = new HashWorker();
		hashWorker.setDaemon(true);
		hashWorker.start();
	}

	public void setFilter(FilenameFilter filter) {
		this.filter = filter;
	}

	public void hashDirectory(String directory) throws IOException {
		File dir = new File(directory);

		// check if the directory exists
		if (!dir.exists()) {
			throw new FileNotFoundException("Directory " + dir + " does not exist");
		}

		logger.info("Starting to walk {}", dir);
		java.nio.file.Files.walkFileTree(dir.toPath(), new DirectoryVisitor(filter));
	}

	class AcceptAllFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return true;
		}
	}

	class DirectoryVisitor extends SimpleFileVisitor<Path> {
		FilenameFilter filter;

		public DirectoryVisitor(FilenameFilter filter) {
			this.filter = filter;
		}

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
		LinkedBlockingQueue<FileInfo> inputQueue = new LinkedBlockingQueue<>();
		LinkedList<FileInfo> workingList = new LinkedList<>();

		HashMaker hash = new HashMaker();
		BinaryFileReader bfr = new BinaryFileReader();

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
						f.setHash(hash.hash(bfr.get(f.getFile())));
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

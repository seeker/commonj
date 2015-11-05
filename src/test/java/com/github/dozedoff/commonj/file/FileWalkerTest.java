/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.dozedoff.commonj.filefilter.FileFilter;

public class FileWalkerTest {

	File rootFolder;
	Path rootPath;
	List<File> files;
	List<File> folders;

	FileWalker fw;

	@Before
	public void setUp() throws Exception {
		/*
		 * rootFolder : one.txt : +----subFolderOne : two.txt : \----subFoldertwo : three.jpg : \----subSubFolderOne four.txt
		 */
		files = new ArrayList<>();
		folders = new ArrayList<>();

		rootPath = Files.createTempDirectory("FileWalkerTest");
		rootFolder = rootPath.toFile();
		folders.add(new File(rootFolder, "subFolderOne"));
		folders.add(new File(rootFolder, "subFoldertwo"));
		folders.add(new File(folders.get(1), "subSubFolderOne"));

		for (File f : folders) {
			f.mkdirs();
		}

		files.add(new File(rootFolder, "one.txt"));
		files.add(new File(folders.get(0), "two.txt"));
		files.add(new File(folders.get(1), "three.jpg"));
		files.add(new File(folders.get(2), "four.txt"));

		for (File f : files) {
			f.createNewFile();
		}

		fw = new FileWalker();

	}

	@After
	public void tearDown() throws Exception {
		rootFolder.delete();
		fw = null;
	}

	@Test
	/**
	 * Test if all files and folders have been created.
	 */
	public void testCorrectSetup() {
		assertTrue(folders.get(0).exists());
		assertTrue(folders.get(1).exists());
		assertTrue(folders.get(2).exists());

		for (File f : files)
			assertTrue(f.exists());
	}

	@Test
	public void testGetAllDirectories() throws IOException {
		assertThat(FileWalker.getAllDirectories(rootPath), hasItems(convertFileToPath(folders)));
	}

	@Test
	public void testGetCurrentDirectorySubdirectories() {
		List<Path> result = FileWalker.getCurrentDirectorySubdirectories(rootPath);

		assertThat(result, hasItems(folders.get(0).toPath(), folders.get(1).toPath()));
		assertThat(result, not(hasItem(folders.get(2).toPath())));
	}

	@Test
	public void testGetCurrentDirectorySubdirectoriesNotDirectory() {
		assertThat(FileWalker.getCurrentDirectorySubdirectories(files.get(0).toPath()), is(empty()));
	}

	@Test
	public void testGetAllImages() throws IOException {
		assertThat(FileWalker.getAllImages(rootPath), hasItem(files.get(2).toPath()));
	}

	@Test
	public void testGetCurrentFolderImages() throws IOException {
		assertThat(FileWalker.getCurrentFolderImages(rootPath).size(), is(0));
	}

	@Test
	public void testWalkFileTreeWithFilter() throws IOException {
		assertThat(FileWalker.walkFileTreeWithFilter(rootPath, new FileFilter()), hasItems(convertFileToPath(files)));
	}

	@Test
	public void testWalkFileTreePathArray() throws IOException {
		assertThat(FileWalker.walkFileTree(convertFileToPath(folders.get(0), folders.get(1))),
				hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testWalkFileTreePathList() throws IOException {
		LinkedList<Path> sourceList = new LinkedList<>();
		sourceList.add(folders.get(0).toPath());
		sourceList.add(folders.get(1).toPath());

		assertThat(FileWalker.walkFileTreePathList(sourceList), hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testWalkFileTreeFileArray() throws IOException {
		assertThat(FileWalker.walkFileTree(folders.get(0), folders.get(1)),
				hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testWalkFileTreeStringList() throws IOException {
		LinkedList<String> sourceList = new LinkedList<>();
		sourceList.add(folders.get(0).toString());
		sourceList.add(folders.get(1).toString());

		assertThat(FileWalker.walkFileTreeStringList(sourceList), hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testWalkFileTreeStringArray() throws IOException {
		assertThat(FileWalker.walkFileTree(folders.get(0).toString(), folders.get(1).toString()),
				hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testWalkFileTreeFileList() throws IOException {
		LinkedList<File> sourceList = new LinkedList<>();
		sourceList.add(folders.get(0));
		sourceList.add(folders.get(1));

		assertThat(FileWalker.walkFileTreeFileList(sourceList), hasItems(convertFileToPath(files.get(1), files.get(2), files.get(3))));
	}

	@Test
	public void testCheckDuplicateElimination() throws IOException {
		LinkedList<Path> results = FileWalker.walkFileTree(rootPath, rootPath);

		assertThat(results, hasItems(convertFileToPath(files)));
		assertThat(results.size(), is(4));
	}

	@Test
	public void testFindImageInFolder() throws IOException {
		LinkedList<Path> results = FileWalker.getCurrentFolderImages(folders.get(1).toPath());

		assertThat(results, hasItem(files.get(2).toPath()));
		assertThat(results.size(), is(1));
	}

	private Path[] convertFileToPath(Collection<File> files) {
		List<Path> paths = new ArrayList<>(files.size());

		for (File f : files) {
			paths.add(f.toPath());
		}

		return paths.toArray(new Path[0]);
	}

	private Path[] convertFileToPath(File... files) {
		return convertFileToPath(Arrays.asList(files));
	}
}

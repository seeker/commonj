/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.dozedoff.commonj.filefilter.FileFilter;

@SuppressWarnings("deprecation")
public class FileWalkerTest {

	File rootFolder;
	Path rootPath;
	File files[] = new File[4];
	File folders[] = new File[3];

	FileWalker fw;

	@Before
	public void setUp() throws Exception {
		/*
		 * rootFolder : one.txt : +----subFolderOne : two.txt : \----subFoldertwo : three.jpg : \----subSubFolderOne four.txt
		 */
		rootPath = Files.createTempDirectory("FileWalkerTest");
		rootFolder = rootPath.toFile();
		folders[0] = new File(rootFolder, "subFolderOne");
		folders[1] = new File(rootFolder, "subFoldertwo");
		folders[2] = new File(folders[1], "subSubFolderOne");

		for (File f : folders) {
			f.mkdirs();
		}

		files[0] = new File(rootFolder, "one.txt");
		files[1] = new File(folders[0], "two.txt");
		files[2] = new File(folders[1], "three.jpg");
		files[3] = new File(folders[2], "four.txt");

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
		assertTrue(folders[0].exists());
		assertTrue(folders[1].exists());
		assertTrue(folders[2].exists());

		for (File f : files)
			assertTrue(f.exists());
	}

	@Test
	public void testGetAllDirectories() throws IOException {
		assertThat(FileWalker.getAllDirectories(rootPath), hasItems(convertFileToPath(folders)));
	}

	@Test
	public void testGetCurrentDirectorySubdirectories() {
		assertThat(FileWalker.getCurrentDirectorySubdirectories(rootPath), hasItems(folders[0].toPath(), folders[1].toPath()));

		List<Path> result = FileWalker.getCurrentDirectorySubdirectories(rootPath);
		assertThat(result, hasItems(folders[0].toPath(), folders[1].toPath()));
		assertThat(result, not(hasItem(folders[2].toPath())));
	}

	@Test
	public void testGetAllImages() throws IOException {
		assertThat(FileWalker.getAllImages(rootPath), hasItem(files[2].toPath()));
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
		assertThat(FileWalker.walkFileTree(convertFileToPath(folders[0], folders[1])),
				hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testWalkFileTreePathList() throws IOException {
		LinkedList<Path> sourceList = new LinkedList<>();
		sourceList.add(folders[0].toPath());
		sourceList.add(folders[1].toPath());

		assertThat(FileWalker.walkFileTreePathList(sourceList), hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testWalkFileTreeFileArray() throws IOException {
		assertThat(FileWalker.walkFileTree(folders[0], folders[1]), hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testWalkFileTreeStringList() throws IOException {
		LinkedList<String> sourceList = new LinkedList<>();
		sourceList.add(folders[0].toString());
		sourceList.add(folders[1].toString());

		assertThat(FileWalker.walkFileTreeStringList(sourceList), hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testWalkFileTreeStringArray() throws IOException {
		assertThat(FileWalker.walkFileTree(folders[0].toString(), folders[1].toString()),
				hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testWalkFileTreeFileList() throws IOException {
		LinkedList<File> sourceList = new LinkedList<>();
		sourceList.add(folders[0]);
		sourceList.add(folders[1]);

		assertThat(FileWalker.walkFileTreeFileList(sourceList), hasItems(convertFileToPath(files[1], files[2], files[3])));
	}

	@Test
	public void testCheckDuplicateElimination() throws IOException {
		LinkedList<Path> results = FileWalker.walkFileTree(rootPath, rootPath);

		assertThat(results, hasItems(convertFileToPath(files)));
		assertThat(results.size(), is(4));
	}

	@Test
	public void testFindImageInFolder() throws IOException {
		LinkedList<Path> results = FileWalker.getCurrentFolderImages(folders[1].toPath());

		assertThat(results, hasItem(files[2].toPath()));
		assertThat(results.size(), is(1));
	}

	private Path[] convertFileToPath(File... files) {
		Path[] paths = new Path[files.length];

		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}

		return paths;
	}
}

/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("deprecation")
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public class FileUtilTest {
	private LinkedList<Path> srcDirs;
	private LinkedList<Path> srcFiles;
	private LinkedList<Path> dstFiles;
	
	private Path srcDir;
	private Path dstDir;
	private Path simplePath;
	
	private static final String SIMPLE_PATH = "C:\\foo\\bar\\";
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilTest.class);
	
	
	@Before
	public void setUp() throws Exception {
		srcDirs = new LinkedList<>();
		srcFiles = new LinkedList<>();
		dstFiles = new LinkedList<>();

		Path baseDir = Files.createTempDirectory("testFileUtil");

		srcDir = baseDir.resolve("src").resolve("test");
		srcDirs.add(srcDir);

		dstDir = baseDir.resolve("dst").resolve("test");
		
		simplePath = Paths.get(SIMPLE_PATH);
	}

	@Test
	public void testPathTokenListLocalFileName() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("C:\\test\\me\\testfile.txt");
		String mustContain[] = { "C:", "test", "me", "testfile", ".txt" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testPathTokenListLocalFileNameSpecialChar() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("C:\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = { "C:", "test", "me", "testfile", ".txt", "test.me" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testPathTokenListNetworkFileName() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("\\\\nas\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = { "\\\\nas", "test", "me", "testfile", ".txt", "test.me" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testDirectoryMover() throws IOException {

		/*
		 * \test │ a.txt │ └───dir1 │ b.txt │ └───dir2 c.txt
		 */

		buildStructure(srcDirs, srcFiles, srcDir);

		createFiles(srcDirs, srcFiles);

		// guard condition
		for (Path p : srcFiles) {
			assertTrue(p.toFile().exists());
		}

		// copy /src/test/ to /dst/
		FileUtil.moveDirectory(srcDir, dstDir.getParent());

		for (Path p : dstFiles) {
			assertTrue(p.toString(), p.toFile().exists());
		}
	}

	@Test
	public void testCopyFile() throws IOException {
		buildStructure(srcDirs, srcFiles, srcDir);

		createFiles(srcDirs, srcFiles);
		/*
		 * ...\srcDir\src\test\dir1\dir2\c.txt
		 * 
		 * to
		 * 
		 * ...\dstdir\...\srcDir\src\test\dir1\dir2\c.txt
		 */

		FileUtil.moveFileWithStructure(srcFiles.get(2), dstDir);
		assertTrue(Files.exists(dstDir.resolve(srcFiles.get(2).getRoot().relativize(srcFiles.get(2)))));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCopyFileSourceNull() throws IOException {
		FileUtil.moveFileWithStructure(null, dstDir);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCopyFileDestinationNull() throws IOException {
		FileUtil.moveFileWithStructure(srcDir, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCopyFileDestinationNoRootComponent() throws IOException {
		Path noRoot = Paths.get("foo/");
		FileUtil.moveFileWithStructure(noRoot, dstDir);
	}

	private void buildStructure(LinkedList<Path> dirs, LinkedList<Path> files, Path base) {
		dirs.add(base.resolve("dir1"));
		dirs.add(base.resolve("dir1").resolve("dir2"));

		files.add(base.resolve("a.txt"));
		files.add(base.resolve("dir1").resolve("b.txt"));
		files.add(base.resolve("dir1").resolve("dir2").resolve("c.txt"));
	}

	private void createFiles(LinkedList<Path> dirs, LinkedList<Path> files) throws IOException {
		for (Path p : dirs) {
			if (p.toFile().mkdirs()) {
				LOGGER.error("Failed to create directory {}", p);
			}
		}

		for (Path p : files) {
			if (p.toFile().createNewFile()) {
				LOGGER.error("Failed to create file {}", p);
			}
		}
	}

	@Test
	public void testRemoveDriveLetter() {
		Path pathToTest = buildAbsolutePath("test", "me", "now", "squirrel.jpg");
		Path path = FileUtil.removeDriveLetter(pathToTest);

		assertThat(path, is(buildRelativePath("test", "me", "now", "squirrel.jpg")));
	}

	private Path buildAbsolutePath(String... elements) {
		Path absolutePath = null;
		Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();
		Path root = null;

		for (Path p : roots) {
			root = p;
			break;
		}

		// guard
		if (root == null) {
			fail("No root directory found");
		}

		absolutePath = root;

		Path relativePath = buildRelativePath(elements);
		absolutePath = absolutePath.resolve(relativePath);

		return absolutePath;
	}

	private Path buildRelativePath(String... elements) {
		Path relativePath = null;

		relativePath = Paths.get("", elements);

		return relativePath;
	}

	@Test
	public void testRemoveDriveLetterNoDrive() {
		Path path = FileUtil.removeDriveLetter(Paths.get("\\test\\me\\now\\squirrel.jpg"));

		assertThat(path, is(Paths.get("\\test\\me\\now\\squirrel.jpg")));
	}

	@Test
	public void testRemoveDriveLetterNull() {
		Path p = null;
		Path path = FileUtil.removeDriveLetter(p);

		assertNull(path);
	}

	@Test
	public void testRemoveDriveLetterDirOnly() {
		Path pathToTest = buildAbsolutePath("test", "me", "now");
		Path path = FileUtil.removeDriveLetter(pathToTest);

		Path validPath = buildRelativePath("test", "me", "now");
		assertThat(path, is(validPath));
	}

	@Test
	public void testRemoveDriveLetterString() {
		Path pathToTest = buildAbsolutePath("test", "me", "now", "squirrel.jpg");
		String path = FileUtil.removeDriveLetter(pathToTest.toString());

		Path validPath = buildRelativePath("test", "me", "now", "squirrel.jpg");
		assertThat(path, is(validPath.toString()));
	}

	@Test
	public void testRemoveDriveLetterNoDriveString() {
		String path = FileUtil.removeDriveLetter("\\test\\me\\now\\squirrel.jpg");

		assertThat(path, is("\\test\\me\\now\\squirrel.jpg"));
	}
	
	@Ignore("tests will fail on Linux systems")
	@Test
	public void testRemoveDriveLetterOnlyDirString() {
		String path = FileUtil.removeDriveLetter("\\test\\me\\now\\");

		assertThat(path, is("\\test\\me\\now\\"));
	}

	@Test
	public void testRemoveDriveLetterNullString() {
		String s = null;
		String path = FileUtil.removeDriveLetter(s);

		assertNull(path);
	}

	@Test
	public void testRemoveDriveLetterStringDirOnly() {
		Path pathToTest = buildAbsolutePath("test", "me", "now");
		String path = FileUtil.removeDriveLetter(pathToTest).toString();

		String validPath = buildRelativePath("test", "me", "now").toString();
		assertThat(path, is(validPath));
	}

	@Test
	public void testSanitizeFilenameForWindows() {
		String original = "foo:bar";
		String clean = FileUtil.sanitizeFilenameForWindows(original);

		assertThat(clean, is("foo_bar"));
	}

	@Ignore("tests will fail on Linux systems")
	@Test
	public void testHasValidWindowsFilenameString() {
		assertThat(FileUtil.hasValidWindowsFilename("C:\\foobar"), is(true));
	}

	@Ignore("tests will fail on Linux systems")
	@Test
	public void testConvertDirPathToString() throws Exception {
		assertThat(FileUtil.convertDirPathToString(simplePath), is(SIMPLE_PATH.toLowerCase()));
	}
	
	@Test
	public void testConvertDirPathToStringNoEndingSlashes() throws Exception {
		String noSlashes = "c:\\foo\\bar";
		assertThat(FileUtil.convertDirPathToString(Paths.get(noSlashes)), is(SIMPLE_PATH.toLowerCase()));
	}
	
	@Test
	public void testConvertDirPathToStringNull() throws Exception {
		assertThat(FileUtil.convertDirPathToString(null), is(nullValue()));
	}
}

package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPathTokenList_LocalFileName() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("C:\\test\\me\\testfile.txt");
		String mustContain[] = { "C:", "test", "me", "testfile", ".txt" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testPathTokenList_LocalFileName_specialChar() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("C:\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = { "C:", "test", "me", "testfile", ".txt", "test.me" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testPathTokenList_NetworkFileName() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("\\\\nas\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = { "\\\\nas", "test", "me", "testfile", ".txt", "test.me" };
		assertThat(result, hasItems(mustContain));
	}

	@Test
	public void testDirectoryMover() throws IOException {

		/*
		 * \test │ a.txt │ └───dir1 │ b.txt │ └───dir2 c.txt
		 */
		Path baseDir = Files.createTempDirectory("testDirectoryMover");

		LinkedList<Path> srcDirs, srcFiles;
		LinkedList<Path> dstDirs, dstFiles;

		srcDirs = new LinkedList<>();
		srcFiles = new LinkedList<>();
		dstDirs = new LinkedList<>();
		dstFiles = new LinkedList<>();

		Path srcDir = baseDir.resolve("src").resolve("test");
		srcDirs.add(srcDir);

		Path dstDir = baseDir.resolve("dst").resolve("test");
		dstDirs.add(dstDir);

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
		Path baseDir = Files.createTempDirectory("testFileMover");

		LinkedList<Path> srcDirs, srcFiles;
		LinkedList<Path> dstDirs;

		srcDirs = new LinkedList<>();
		srcFiles = new LinkedList<>();
		dstDirs = new LinkedList<>();

		Path srcDir = baseDir.resolve("src").resolve("test");
		srcDirs.add(srcDir);

		Path dstDir = baseDir.resolve("dst").resolve("test");
		dstDirs.add(dstDir);

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

	private void buildStructure(LinkedList<Path> dirs, LinkedList<Path> files, Path base) {
		dirs.add(base.resolve("dir1"));
		dirs.add(base.resolve("dir1").resolve("dir2"));

		files.add(base.resolve("a.txt"));
		files.add(base.resolve("dir1").resolve("b.txt"));
		files.add(base.resolve("dir1").resolve("dir2").resolve("c.txt"));
	}

	private void createFiles(LinkedList<Path> dirs, LinkedList<Path> files) throws IOException {
		for (Path p : dirs) {
			p.toFile().mkdirs();
		}

		for (Path p : files) {
			p.toFile().createNewFile();
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
}

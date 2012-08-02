package file;

import static org.hamcrest.CoreMatchers.is;
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

import filefilter.FileFilter;


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
		 * rootFolder
		 * :	one.txt
		 * :
		 * +----subFolderOne
		 * :		two.txt
		 * :
		 * \----subFoldertwo
		 * 		: 	three.jpg
		 * 		:
		 * 		\----subSubFolderOne
		 * 				four.txt
		 */
		rootPath = Files.createTempDirectory("FileWalkerTest");
		rootFolder = rootPath.toFile();
		folders[0] = new File(rootFolder,"subFolderOne");
		folders[1] = new File(rootFolder,"subFoldertwo");
		folders[2] = new File(folders[1],"subSubFolderOne");
		
		for(File f : folders){
			f.mkdirs();
		}

		files[0] = new File(rootFolder,"one.txt");
		files[1] = new File(folders[0],"two.txt");
		files[2] = new File(folders[1],"three.jpg");
		files[3] = new File(folders[2], "four.txt");

		for(File f : files){
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

		for(File f : files)
			assertTrue(f.exists());
	}

	@Test
	/**
	 * Read only Files in the root folder.
	 */
	public void testNoSub() {
		fw.addPath(rootFolder);
		fw.setnoSub(true);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(1));
		assertThat(results, hasItem(files[0]));
	}
	
	@Test
	/**
	 * Read all files.
	 */
	public void testFullWalk() {
		fw.addPath(rootFolder);
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(4));
		
		assertThat(results, hasItems(files));
	}
	@Test
	/**
	 * Read all files.
	 */
	public void testFullWalkList() {
		fw.addPath(rootFolder);
		fw.setnoSub(false);
		List<File> results = fw.fileWalkList();
		assertThat(results.size(), is(4));
		
		assertThat(results, hasItems(files));
	}
	
	@Test
	/**
	 * Read all files.
	 */
	public void testFullWalkStringList() {
		fw.addPath(rootFolder);
		fw.setnoSub(false);
		List<String> results = fw.fileWalkStringList();
		assertThat(results.size(), is(4));
		
		String[] filesAsStrings = new String[files.length];
		
		for(int i=0; i<files.length; i++){
			filesAsStrings[i] = files[i].toString();
		}
		
		assertThat(results, hasItems(filesAsStrings));
	}
	
	@Test
	/**
	 * Read all files.
	 */
	public void testFullWalkString() {
		fw.addPath(rootFolder.toString());
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(4));
		
		assertThat(results, hasItems(files));
	}
	
	@Test
	/**
	 * Read files from two select folders
	 */
	public void testSelectFolderWalk() {
		File[] dirsToSearch = new File[2];
		dirsToSearch[0] = folders[0];
		dirsToSearch[1] = folders[2];
		
		fw.addPath(dirsToSearch);
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(2));
		
		assertThat(results, hasItem(files[1]));
		assertThat(results, hasItem(files[3]));
	}
	
	@Test
	/**
	 * Read files from two select folders
	 */
	public void testSelectFolderWalkList() {
		LinkedList<File> list = new LinkedList<>();
		list.add(folders[0]);
		list.add(folders[2]);
		fw.addPath(list);
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(2));
		
		assertThat(results, hasItem(files[1]));
		assertThat(results, hasItem(files[3]));
	}
	
	@Test
	/**
	 * Read files from two select folders
	 */
	public void testSelectFolderWalkString() {
		String[] dirsToSearch = new String[2];
		dirsToSearch[0] = folders[0].toString();
		dirsToSearch[1] = folders[2].toString();
		
		fw.addPath(dirsToSearch);
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(2));
		
		assertThat(results, hasItem(files[1]));
		assertThat(results, hasItem(files[3]));
	}
	
	@Test
	/**
	 * Read all files.
	 */
	public void testCheckDuplicateAdd() {
		fw.addPath(rootFolder);
		fw.addPath(rootFolder);
		fw.setnoSub(false);
		LinkedList<File> results = fw.fileWalk();
		assertThat(results.size(), is(4));
		
		assertThat(results, hasItems(files));
	}
	
	@Test
	/**
	 * Read only files with .jpg , .gif or .png extension.
	 */
	public void testFilterWalk() {
		fw.addPath(rootFolder);
		fw.setnoSub(false);
		fw.setImagesOnly(true);
		LinkedList<File> results = fw.fileWalk();

		assertThat(results.size(), is(1));
		assertTrue(results.get(0).equals(files[2]));
	}
	
	@Test
	/**
	 * same as testFilterWalk(), but without sub-directories.
	 */
	public void testFilterWalk_noSub() {
		fw.addPath(rootFolder);
		fw.setnoSub(true);
		fw.setImagesOnly(true);
		LinkedList<File> results = fw.fileWalk();

		assertThat(results.size(), is(0)); // should not find any files
	}
	
	@Test
	/**
	 * Find files in a sub-directory
	 */
	public void testFileWalkSubFolder() {
		fw.addPath(folders[1]);
		fw.setnoSub(false);
		fw.setImagesOnly(false);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(2));
	}
	
	@Test
	/**
	 * Find all directories
	 */
	public void testFileWalkDirectories() {
		fw.addPath(rootFolder);
		fw.setfolderOnly(true);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(4));
		assertThat(results, hasItems(folders));
		assertThat(results, hasItem(rootFolder));
	}
	
	@Test
	/**
	 * Find all directories
	 */
	public void testFileWalkDirectoriesSubFolder() {
		fw.addPath(folders[1]);
		fw.setfolderOnly(true);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(2));
		assertThat(results, hasItem(folders[2]));
		assertThat(results, hasItem(folders[1]));
	}
	
	@Test
	/**
	 * Find all directories
	 */
	public void testFileWalkDirectoriesNoSub() {
		fw.addPath(folders[1]);
		fw.setfolderOnly(true);
		fw.setnoSub(true);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(2));
		assertThat(results, hasItem(folders[2]));
		assertThat(results, hasItem(folders[1]));
	}
	
	@Test
	public void testGetAllDirectories() throws IOException {
		assertThat(FileWalker.getAllDirectories(rootPath), hasItems(convertFileToPath(folders)));
	}

	@Test
	public void testGetCurrentDirectorySubdirectories() {
		assertThat(FileWalker.getCurrentDirectorySubdirectories(rootPath), hasItems(folders[0].toPath(), folders[1].toPath()));
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
		assertThat(FileWalker.walkFileTree(convertFileToPath(folders[0],folders[1])), hasItems(convertFileToPath(files[1], files[2], files[3])));
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
		assertThat(FileWalker.walkFileTree(folders[0],folders[1]), hasItems(convertFileToPath(files[1], files[2], files[3])));
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
		assertThat(FileWalker.walkFileTree(folders[0].toString(),folders[1].toString()), hasItems(convertFileToPath(files[1], files[2], files[3])));
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
		LinkedList<Path> results = FileWalker.walkFileTree(rootPath,rootPath);
		
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
		
		for(int i=0; i < files.length; i++){
			paths[i] = files[i].toPath();
		}
		
		return paths;
	}
}

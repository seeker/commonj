package file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.io.File;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



public class FileWalkerTest {

	@Rule
	public TemporaryFolder rootFolder = new TemporaryFolder();
	public File files[] = new File[4];
	public File folders[] = new File[3];

	FileWalker fw;
	@Before
	public void setUp() throws Exception {	
		/*
		 * rootFolder
		 * :	files[1].txt
		 * :
		 * +----folders[0]
		 * :	files[2].txt
		 * :
		 * :
		 * \----folders[1]
		 * 		: files[3].jpg
		 * 		:
		 * 		\----folders[2]
		 * 				file_04.txt
		 */
		folders[0] = rootFolder.newFolder("subFolderOne");
		folders[1] = rootFolder.newFolder("subFoldertwo");
		folders[2] = new File(folders[1],"subSubFolderOne");
		folders[2].mkdir();

		files[0] = rootFolder.newFile("one.txt");

		files[1] = new File(folders[0],"two.txt");
		files[1].createNewFile();

		files[2] = new File(folders[1],"three.jpg");
		files[2].createNewFile();

		files[3] = new File(folders[2], "four.txt");
		files[3].createNewFile();

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
		fw.addPath(rootFolder.getRoot());
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
		fw.addPath(rootFolder.getRoot());
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
		fw.addPath(rootFolder.getRoot());
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
		fw.addPath(rootFolder.getRoot());
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
		fw.addPath(rootFolder.getRoot());
		fw.setfolderOnly(true);
		LinkedList<File> results = fw.fileWalk();
		
		assertThat(results.size(), is(4));
		assertThat(results, hasItems(folders));
		assertThat(results, hasItem(rootFolder.getRoot()));
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
}

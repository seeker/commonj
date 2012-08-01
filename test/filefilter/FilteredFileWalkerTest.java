/*  Copyright (C) 2012  Nicholas Wright
	
	part of 'AidUtil', a collection of maintenance tools for 'Aid'.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package filefilter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class FilteredFileWalkerTest {
	
	public Path rootFolder;
	public Path files[] = new Path[4];
	public Path folders[] = new Path[3];

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
		rootFolder = Files.createTempDirectory("FilteredFileWalkerTest");
		folders[0] = rootFolder.resolve("subFolderOne");
		folders[1] = rootFolder.resolve("subFoldertwo");
		folders[2] = folders[1].resolve("subSubFolderOne");
		
		for(Path folder : folders){
			Files.createDirectories(folder);
		}

		files[0] = rootFolder.resolve("one.txt");
		files[1] = folders[0].resolve("two.txt");
		files[2] = folders[1].resolve("three.jpg");
		files[3] = folders[2].resolve("four.txt");

		for(Path file : files){
			Files.createFile(file);
		}
	}

	@Test
	public void testWalkFileTree() throws IOException {
		LinkedList<Path> foundFiles = FilteredFileWalker.walkFileTree(rootFolder, new SimpleImageFilter());
		
		assertThat(foundFiles, hasItem(files[2]));
		assertThat(foundFiles.size(), is(1));
	}
}

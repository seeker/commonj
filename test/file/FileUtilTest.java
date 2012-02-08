/*  Copyright (C) 2012  Nicholas Wright

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
package file;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

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
		String mustContain[] = {"C:","test","me","testfile",".txt"};
		assertThat(result, hasItems(mustContain));
	}
	
	@Test
	public void testPathTokenList_LocalFileName_specialChar() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("C:\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = {"C:","test","me","testfile",".txt","test.me"};
		assertThat(result, hasItems(mustContain));
	}
	
	@Test
	public void testPathTokenList_NetworkFileName() {
		LinkedList<String> result = (LinkedList<String>) FileUtil.pathTokenList("\\\\nas\\test\\me\\test.me\\testfile.txt");
		String mustContain[] = {"\\\\nas","test","me","testfile",".txt","test.me"};
		assertThat(result, hasItems(mustContain));
	}


}

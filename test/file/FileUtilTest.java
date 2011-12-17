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

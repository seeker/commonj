package file;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryFileReaderTest {
	private static byte testData[] = {1,2,3,5,7,13,17};
	private static File testFile;
	
	@BeforeClass
	public static void before() throws IOException{
		testFile = Files.createTempFile("BFRtestfile","tmp").toFile();
		testFile.createNewFile();
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(testFile));
		bos.write(testData, 0, testData.length);
		bos.close();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBinaryFileReaderInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFile() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testReUse() {
		fail("Not yet implemented");
	}

}

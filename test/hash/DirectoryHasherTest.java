package hash;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import file.BinaryFileWriter;
import file.FileInfo;
import hash.DirectoryHasher;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

public class DirectoryHasherTest {
	File tempDir;
	BinaryFileWriter bfr = new BinaryFileWriter();
	byte[] testData = {12,45,6,12,99};	// SHA-256: 95F6A79D2199FC2CFA8F73C315AA16B33BF3544C407B4F9B29889333CA0DB815
	byte[] testData2 = {99,21,6,45,12}; // SHA-256: 20FC038E00E13585E68E7EBE50D79CBE7D476A74D8FDE71872627DA6CD8FC8BB
	
	LinkedBlockingQueue<FileInfo> fileQueue;
	DirectoryHasher dh;
	
	@Before
	public void setUp() throws Exception {
		tempDir  = Files.createTempDirectory("DirectoryHasherTest").toFile();
		bfr.write(testData, new File(tempDir,"testFile1.txt").toString());
		bfr.write(testData2, new File(tempDir,"testFile2.txt").toString());
		
		fileQueue  = new LinkedBlockingQueue<>();
		dh = new DirectoryHasher(fileQueue);
	}

	@Test
	public void testHashDirectory() throws Exception {
		dh.hashDirectory(tempDir.toString());
		Thread.sleep(2000);
		assertThat(fileQueue.size(), is(2));
	}

}

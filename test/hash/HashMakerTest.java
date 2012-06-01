package hash;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

public class HashMakerTest {
	byte[] testData = {12,45,6,12,99};	// SHA-256: 95F6A79D2199FC2CFA8F73C315AA16B33BF3544C407B4F9B29889333CA0DB815
	byte[] testData2 = {99,21,6,45,12}; // SHA-256: 20FC038E00E13585E68E7EBE50D79CBE7D476A74D8FDE71872627DA6CD8FC8BB
	byte[] testData3 = {}; 				// SHA-256: E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855
	
	HashMaker hm;
	
	@Before
	public void setUp() throws Exception {
		hm = new HashMaker();
	}

	@Test
	public void testHash() {
		assertThat(hm.hash(testData), is("95F6A79D2199FC2CFA8F73C315AA16B33BF3544C407B4F9B29889333CA0DB815"));
		assertThat(hm.hash(testData2), is("20FC038E00E13585E68E7EBE50D79CBE7D476A74D8FDE71872627DA6CD8FC8BB"));
	}
	
	@Test
	public void testNoData() {
		assertThat(hm.hash(testData3), is("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855"));
	}
	
	@Test
	public void testNullData(){
		assertNull(hm.hash(null));
	}
}

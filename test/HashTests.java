import hash.DirectoryHasherTest;
import hash.HashMakerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DirectoryHasherTest.class,
	HashMakerTest.class
})
public class HashTests {

}

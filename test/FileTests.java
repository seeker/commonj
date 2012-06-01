import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import file.*;
@RunWith(Suite.class)
@SuiteClasses({ 
	FileUtilTest.class, 
	FileWalkerTest.class 
})
public class FileTests {

}

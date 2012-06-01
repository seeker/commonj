import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ 
	FileTests.class, 
	HashTests.class, 
	IoTests.class, 
	NetTests.class 
})
public class AllTests {

}

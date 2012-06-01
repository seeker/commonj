import net.GetBinaryTest;
import net.GetHtmlTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	GetBinaryTest.class, 
	GetHtmlTest.class
})
public class NetTests {

}

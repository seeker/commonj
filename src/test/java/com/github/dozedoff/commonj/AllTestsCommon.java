package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	FileTests.class, 
	HashTests.class, 
	NetTests.class,
	ImageTests.class
})
public class AllTestsCommon {

}

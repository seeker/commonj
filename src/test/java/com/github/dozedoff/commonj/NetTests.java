package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.net.GetBinaryTest;
import com.github.dozedoff.commonj.net.GetHtmlTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	GetBinaryTest.class, 
	GetHtmlTest.class
})
public class NetTests {

}
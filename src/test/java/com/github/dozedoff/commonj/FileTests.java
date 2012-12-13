package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.file.FileUtilTest;
import com.github.dozedoff.commonj.file.FileWalkerTest;
import com.github.dozedoff.commonj.file.TextFileReaderTest;
@RunWith(Suite.class)
@SuiteClasses({ 
	FileUtilTest.class, 
	FileWalkerTest.class,
	TextFileReaderTest.class
})
public class FileTests {

}

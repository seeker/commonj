/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.file.ArchiveVisitorTest;
import com.github.dozedoff.commonj.file.BinaryFileReaderTest;
import com.github.dozedoff.commonj.file.BinaryFileWriterTest;
import com.github.dozedoff.commonj.file.FileInfoTest;
import com.github.dozedoff.commonj.file.FileUtilTest;
import com.github.dozedoff.commonj.file.FileWalkerTest;
import com.github.dozedoff.commonj.file.TextFileReaderTest;
@RunWith(Suite.class)
@SuiteClasses({ 
	FileUtilTest.class, 
	FileWalkerTest.class,
	TextFileReaderTest.class,
	BinaryFileWriterTest.class,
	BinaryFileReaderTest.class,
	ArchiveVisitorTest.class,
	FileInfoTest.class
})
public class FileTests {

}

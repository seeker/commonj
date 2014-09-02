/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.filefilter.ArchiveFilterTest;
import com.github.dozedoff.commonj.filefilter.DirectoryFilterTest;
import com.github.dozedoff.commonj.filefilter.FileExtensionFilterTest;
import com.github.dozedoff.commonj.filefilter.FileFilterTest;
import com.github.dozedoff.commonj.filefilter.SimpleImageFilterTest;

@RunWith(Suite.class)
@SuiteClasses({ FileExtensionFilterTest.class,
				ArchiveFilterTest.class,
				DirectoryFilterTest.class,
				SimpleImageFilterTest.class,
				FileFilterTest.class
})
public class FileFilterTests {

}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// @formatter:off
@RunWith(Suite.class)
@SuiteClasses({ 
	FileTests.class, 
	HashTests.class, 
	NetTests.class,
	ImageTests.class,
	StringTests.class,
	IoTests.class,
	UtilTests.class,
	TimeTests.class,
	FileFilterTests.class,
	EncodingTests.class,
	SettingsTests.class,
	ThreadTests.class
})
public class AllTestsCommon {}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.hash.DirectoryHasherTest;
import com.github.dozedoff.commonj.hash.HashMakerTest;
import com.github.dozedoff.commonj.hash.ImagePHashTest;

//@formatter:off
@RunWith(Suite.class)
@SuiteClasses({ 
	DirectoryHasherTest.class,
	HashMakerTest.class,
	ImagePHashTest.class
})
public class HashTests {}

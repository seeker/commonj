/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.encoding.EntropyTest;
import com.github.dozedoff.commonj.encoding.HuffmanTest;
import com.github.dozedoff.commonj.encoding.SymbolTest;

@RunWith(Suite.class)
@SuiteClasses({
	SymbolTest.class,
	EntropyTest.class,
	HuffmanTest.class
})

public class EncodingTests {}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.util.FileIOTest;
import com.github.dozedoff.commonj.util.PairTest;
import com.github.dozedoff.commonj.util.RandomTest;

@RunWith(Suite.class)
@SuiteClasses({ PairTest.class,
				RandomTest.class,
				FileIOTest.class
})
public class UtilTests {

}

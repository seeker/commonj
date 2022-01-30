/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.ParallelSuite;
import com.googlecode.junittoolbox.SuiteClasses;

@RunWith(ParallelSuite.class)
@SuiteClasses({ "**/*Test.class", "!**/*IT.class", "!**/*NTS.class", "!**/learning*" })
public class AllUnitTests {

}

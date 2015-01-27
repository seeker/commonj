/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.time.StopWatchTest;
import com.github.dozedoff.commonj.time.TickerTest;

@RunWith(Suite.class)
@SuiteClasses({ StopWatchTest.class, TickerTest.class })
public class TimeTests {

}

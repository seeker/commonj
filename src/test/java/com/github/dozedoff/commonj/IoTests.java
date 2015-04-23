/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.io.DataProducerTest;
import com.github.dozedoff.commonj.io.ResourcePoolTest;
import com.github.dozedoff.commonj.io.StreamGobblerTest;

@RunWith(Suite.class)
@SuiteClasses({ DataProducerTest.class, ResourcePoolTest.class, StreamGobblerTest.class })
public class IoTests {

}

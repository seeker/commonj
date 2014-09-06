/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.settings.AbstractSettingsTest;
import com.github.dozedoff.commonj.settings.AbstractSettingsValidatorTest;

//@formatter:off
@RunWith(Suite.class)
@SuiteClasses({
	AbstractSettingsTest.class,
	AbstractSettingsValidatorTest.class
})
public class SettingsTests {}

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.string.ConvertTest;

@RunWith(Suite.class)
@SuiteClasses({
	ConvertTest.class
})
public class StringTests {}

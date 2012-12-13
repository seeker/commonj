package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.hash.DirectoryHasherTest;
import com.github.dozedoff.commonj.hash.HashMakerTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	DirectoryHasherTest.class,
	HashMakerTest.class
})
public class HashTests {

}

/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.dozedoff.commonj.net.DownloadItemTest;
import com.github.dozedoff.commonj.net.DownloadWithRetryTest;
import com.github.dozedoff.commonj.net.FileLoaderTest;
import com.github.dozedoff.commonj.net.GetBinaryTest;
import com.github.dozedoff.commonj.net.GetHtmlTestNTS;
import com.github.dozedoff.commonj.net.JettyHttpClientTestNTS;

@RunWith(Suite.class)
@SuiteClasses({ 
	GetBinaryTest.class, 
		GetHtmlTestNTS.class,
	DownloadItemTest.class,
	FileLoaderTest.class,
	DownloadWithRetryTest.class,
		JettyHttpClientTestNTS.class
})
public class NetTests {

}

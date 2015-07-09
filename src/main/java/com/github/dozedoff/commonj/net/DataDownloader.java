package com.github.dozedoff.commonj.net;

import java.io.IOException;
import java.net.URL;

public interface DataDownloader {
	byte[] download (URL url) throws PageLoadException, IOException;
}

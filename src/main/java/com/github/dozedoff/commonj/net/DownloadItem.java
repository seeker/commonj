/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.net;

import java.net.URL;

/**
 * Storage class for an Image.
 */
public class DownloadItem {
	private URL imageUrl;
	private String imageName;

	public DownloadItem(URL imageUrl, String imageName) {
		this.imageUrl = imageUrl;
		this.imageName = imageName;
	}

	public URL getImageUrl() {
		return imageUrl;
	}

	public String getImageName() {
		return imageName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DownloadItem)) {
			return false;
		} else {
			DownloadItem ii = (DownloadItem) obj;
			if (ii.getImageUrl().equals(imageUrl) && ii.getImageName().equals(imageName))
				return true;
		}
		return false;
	}
}

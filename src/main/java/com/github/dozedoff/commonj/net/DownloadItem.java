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
	private final URL imageUrl;
	private final String imageName;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (! (obj instanceof DownloadItem))
			return false;
		DownloadItem other = (DownloadItem) obj;
		if (imageName == null) {
			if (other.imageName != null)
				return false;
		} else if (!imageName.equals(other.imageName))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		return true;
	}
}

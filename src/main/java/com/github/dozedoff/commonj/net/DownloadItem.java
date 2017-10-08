/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
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

	/**
	 * Create a new {@link DownloadItem}.
	 * 
	 * @param imageUrl
	 *            that points to the image
	 * @param imageName
	 *            the filename for the image
	 */
	public DownloadItem(URL imageUrl, String imageName) {
		this.imageUrl = imageUrl;
		this.imageName = imageName;
	}

	/**
	 * Get the {@link URL} that points to the image.
	 * 
	 * @return the {@link URL} to the image
	 */
	public URL getImageUrl() {
		return imageUrl;
	}

	/**
	 * Get the filename for the image.
	 * 
	 * @return the filename for the image
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * Calculate a hash code based on the image name and {@link URL}.
	 * 
	 * @return the calculated hash code for this object
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imageName == null) ? 0 : imageName.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		return result;
	}

	/**
	 * Compare an object to check if they are equal. Comparison is based on the
	 * image name and image {@link URL}.
	 * 
	 * @param obj
	 *            object to compare
	 * @return if the other object is of the type {@link DownloadItem} and the
	 *         fields match
	 */
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

/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import java.net.URL;
import java.util.Objects;

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

	// TODO check Objects.equals handling of URL.equals (performance impact)

	/**
	 * Calculate a hash code based on the image name and {@link URL}.
	 * 
	 * @return the calculated hash code for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(imageName, imageUrl);
	}

	// TODO check Objects.equals handling of URL.equals (performance impact)

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
		if (obj instanceof DownloadItem) {
			DownloadItem other = (DownloadItem) obj;
		
			
			return Objects.equals(this.imageName, other.imageName) && Objects.equals(this.imageUrl, other.imageUrl);
		}

		return false;
	}
}

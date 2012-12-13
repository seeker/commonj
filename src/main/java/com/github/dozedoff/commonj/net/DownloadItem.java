/*  Copyright (C) 2012  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
		if( !(obj instanceof DownloadItem)){
			return false;
		}else{
			DownloadItem ii = (DownloadItem)obj;
			if(ii.getImageUrl().equals(imageUrl) && ii.getImageName().equals(imageName))
				return true;
		}
		return false;
	}
}

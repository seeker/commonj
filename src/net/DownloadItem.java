/*  Copyright (C) 2011  Nicholas Wright
	
	part of 'Aid', an imageboard downloader.

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
package net;

/**
 * Storage class for an Image.
 */
public class DownloadItem {
	private String imageUrl;
	private String imageName;

	public DownloadItem(String imageUrl, String imageName) {
		this.imageUrl = imageUrl;
		this.imageName = imageName;
	}

	public String getImageUrl() {
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

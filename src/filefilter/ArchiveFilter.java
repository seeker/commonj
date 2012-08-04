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
package filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;

public class ArchiveFilter implements FileFilter {
	final String[] vaildExtensions = {"7z", "XZ", "BZIP2", "GZIP", "TAR", "ZIP", "WIM", "ARJ", "CAB", "CHM", "CPIO", "CramFS", "DEB", "DMG", "FAT", "HFS", "ISO", "LZH", "LZMA", "MBR", "MSI", "NSIS", "NTFS", "RAR", "RPM", "SquashFS", "UDF", "VHD", "XAR", "Z"};
	final ArrayList<String> vaildArchiveExtensions = new ArrayList<>(vaildExtensions.length);

	public ArchiveFilter(){
		for(String validExtension : vaildExtensions){
			vaildArchiveExtensions.add(validExtension.toLowerCase());
		}
		
		Collections.sort(vaildArchiveExtensions);
	}
	
	@Override
	public boolean accept(File pathname) {
		if(! pathname.isFile()){
			return false;
		}
		
		String filename = pathname.getName();
		
		int extensionIndex = filename.lastIndexOf(".") + 1;
		String fileExtension = filename.substring(extensionIndex).toLowerCase();
		
		if(extensionIndex == -1){
			return false;
		}
		
		if(Collections.binarySearch(vaildArchiveExtensions, fileExtension) >= 0){
			return true;
		}
		
		return false;
	}
}

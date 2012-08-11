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
package file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

import filefilter.ArchiveFilter;

public class ArchiveVisitor extends SimpleFileVisitor<Path> {
	ArchiveFilter archiveFilter = new ArchiveFilter();
	LinkedList<Path> archiveList;
	
	public ArchiveVisitor(LinkedList<Path> archiveList) {
		this.archiveList = archiveList;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
		if(archiveFilter.accept(file.toFile())){
			archiveList.add(file);
		}
		
		return FileVisitResult.CONTINUE;
	}
}

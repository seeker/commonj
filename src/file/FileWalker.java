/*  Copyright (C) 2011  Nicholas Wright

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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class will search all folders and the respective subfolders for files
 * and return them as a List of File items.
 */
public class FileWalker{
	/** output list for Files or Folders */
	private LinkedList<File> resultList = new LinkedList<File>();
	/** Directory's to process */
	private LinkedList<File> dirToSearch = new LinkedList<File>();
	private boolean noSub = false;
	private boolean folderOnly = false;
	private boolean imageOnly = false;

	///////////////////////////////////////////////////////////////////////////////
	public void addPath(File file){
		File[] f = {file};
		addPath(f);
	}

	public void addPath(File[] file){  //main add Method
		for (File f : file){ 
			if(!dirToSearch.contains(f)){
				dirToSearch.add(f);
			}
			
		}
	}

	public void addPath(String string) {
		File[] f = {new File(string)}; // make a 1 element array
		addPath(f);
	}

	public void addPath(String[] dirs){
		File[] conv = new File[dirs.length];
		int i=0;
		for (String s : dirs){ //string to File
			conv[i]=(new File(s));
			i++;
		}
		addPath(conv);
	}

	public void addPath(List<File> dirs){
		File[] conv = new File[dirs.size()];
		dirs.toArray(conv);
		addPath(conv);
	}

	public void setnoSub(boolean set){
		this.noSub = set;
	}

	public void setfolderOnly(boolean set){
		this.folderOnly = set;
	}

	public void setImagesOnly(boolean set){
		this.imageOnly = set;
	}
	///////////////////////////////////////////////////////////////////////////////	
	public List<File> fileWalkList(){
		LinkedList<File> files = fileWalk();
		ArrayList<File> list = new ArrayList<File>(100);

		Iterator<File> ite = files.iterator();
		while(ite.hasNext()){
			list.add(ite.next());
		}
		return list;
	}

	public List<String> fileWalkStringList(){
		LinkedList<File> files = fileWalk();
		List<String> list = new ArrayList<String>(100);
		Iterator<File> ite = files.iterator();
		while(ite.hasNext()){
			list.add(ite.next().toString());
		}
		return list;
	}

	public LinkedList<File> fileWalk(){
		resultList.clear();
		for(File f : dirToSearch){
			try {
				if(noSub && folderOnly){
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),2, new FetchFiles());
				}else if(noSub && !folderOnly){
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),1, new FetchFiles());
				}else{
					Files.walkFileTree( f.toPath(), new FetchFiles());
				}
			} catch (IOException e) {
				e.printStackTrace(); // should not reach this...
			}
		}
		dirToSearch.clear();
		return new LinkedList<File>(resultList);
	}

	class FetchFiles extends SimpleFileVisitor<Path>{
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
			if(! folderOnly && attrs.isRegularFile()){
				if(imageOnly){
					String toTest = path.toString().toLowerCase();

					if (toTest.endsWith(".jpg") || toTest.endsWith(".png") || toTest.endsWith(".gif")){
						resultList.add(path.toFile());	
					}
				}else{
					resultList.add(path.toFile());	
				}
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if(folderOnly && attrs.isDirectory()){
				resultList.add(dir.toFile());
			}

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			System.err.println("unable to access "+file.toString());
			return FileVisitResult.CONTINUE;
		}

	}
}
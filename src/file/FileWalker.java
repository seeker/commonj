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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
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

import filefilter.DirectoryFilter;
import filefilter.SimpleImageFilter;

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
	
	//TODO move FilteredFileWalker into this class

	//TODO replace all addPath(...) methods with walkFileTree(...) methods that accept lists , VarArgs
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

	/**
	 * Use {@link FileWalker#getCurrentDirectorySubdirectories(Path)} or {@link FileWalker#getCurrentFolderImages(Path)} instead
	 */
	@Deprecated
	public void setnoSub(boolean set){
		this.noSub = set;
	}

	/**
	 * Use {@link FileWalker#getAllDirectories(Path)} or {@link FileWalker#getCurrentDirectorySubdirectories(Path)} instead.
	 * @param set
	 */
	@Deprecated
	public void setfolderOnly(boolean set){
		this.folderOnly = set;
	}
	
	public static LinkedList<Path> getAllDirectories(Path startDirectory) throws IOException{
		LinkedList<Path> directoryList = new LinkedList<>();
		Files.walkFileTree(startDirectory, new DirectoryVisitor(directoryList));
		return directoryList;
	}
	
	public static LinkedList<Path> getCurrentDirectorySubdirectories(Path currentDirectory){
		LinkedList<Path> subDirectories = new LinkedList<>();
		File[] foundDirectories = currentDirectory.toFile().listFiles(new DirectoryFilter());
		
		for(File file : foundDirectories){
			subDirectories.add(file.toPath());
		}
		
		return subDirectories;
	}

	/**
	 * Use {@link FileWalker#getAllImages(Path)} or {@link FileWalker#getCurrentFolderImages(Path)} instead.
	 */
	@Deprecated
	public void setImagesOnly(boolean set){
		this.imageOnly = set;
	}
	
	public static LinkedList<Path> getAllImages(Path startDirectory) throws IOException{
		return walkFileTreeWithFilter(startDirectory, new SimpleImageFilter());
	}
	
	public static LinkedList<Path> getCurrentFolderImages(Path currentFolder) throws IOException {
		LinkedList<Path> imageList = new LinkedList<>();
		
		File[] foundImages = currentFolder.toFile().listFiles(new SimpleImageFilter());
		
		for(File file : foundImages){
			imageList.add(file.toPath());
		}
		
		return imageList;
	}
	
	public static LinkedList<Path> walkFileTreeWithFilter(Path startDirectory, FileFilter fileFilter) throws IOException {
		LinkedList<Path> foundFiles = new LinkedList<>();
		
		Files.walkFileTree(startDirectory, new FilenameFilterVisitor(foundFiles, fileFilter));
		
		return foundFiles;
	}
	
	
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
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),2, new FileVisitor());
				}else if(noSub && !folderOnly){
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),1, new FileVisitor());
				}else{
					Files.walkFileTree( f.toPath(), new FileVisitor());
				}
			} catch (IOException e) {
				e.printStackTrace(); // should not reach this...
			}
		}
		dirToSearch.clear();
		return new LinkedList<File>(resultList);
	}

	class FileVisitor extends SimpleFileVisitor<Path>{
		SimpleImageFilter imageFilter = new SimpleImageFilter();
		
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
			if(! folderOnly && attrs.isRegularFile()){
				if(imageOnly){

					if (imageFilter.accept(path.toFile())){
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
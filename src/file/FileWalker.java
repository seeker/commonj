package file;

import java.io.File;
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
			if(!dirToSearch.contains(f));
			dirToSearch.add(f);
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
		ArrayList<String> list = new ArrayList<String>(100);
		Iterator<File> ite = files.iterator();
		while(ite.hasNext()){
			list.add(ite.next().toString());
		}
		return list;
	}
	/**
	 * Returns the Files in the given directory.
	 * @param dir Directory to search
	 * @param useImageFilter if true, will only return files with jpg, gif or png
	 * @return a list of files
	 */
	public LinkedList<File> fileFind(File dir, boolean useImageFilter){
		resultList.clear();
		FilenameFilter filter = null;
		if(useImageFilter)
			filter = new ImageFileFilter();
		
		for(File f : dir.listFiles(filter)){
			if(f.isFile()){
				resultList.add(f);
			}
		}
		return new LinkedList<File>(resultList);
	}

	public LinkedList<File> fileWalk(){
		for(File f : dirToSearch){
			try {
				if(noSub && folderOnly)
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),2, new FetchFiles());
				else if(noSub && !folderOnly)
					Files.walkFileTree( f.toPath(), EnumSet.noneOf(FileVisitOption.class),1, new FetchFiles());
				else
					Files.walkFileTree( f.toPath(), new FetchFiles());

			} catch (IOException e) {
				e.printStackTrace(); // should not reach this...
			}
		}
		return new LinkedList<File>(resultList);
	}

	public void clearAll(){
		this.resultList.clear();
		this.dirToSearch.clear();
	}

	class FetchFiles extends SimpleFileVisitor<Path>{
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
			if(! folderOnly && attrs.isRegularFile()){
				if(imageOnly){
					String toTest = path.toString().toLowerCase();

					if (toTest.endsWith(".jpg") || toTest.endsWith(".png") || toTest.endsWith(".gif"))
						resultList.add(path.toFile());	
				}else{
					resultList.add(path.toFile());	
				}
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if(folderOnly && attrs.isDirectory())
				resultList.add(dir.toFile());

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			System.err.println("unable to access "+file.toString());
			return FileVisitResult.CONTINUE;
		}

	}
	/**
	 * FileFilter class that will return true on jpg,png and gif
	 * @author Nicholas Wright
	 *
	 */
	class ImageFileFilter implements FilenameFilter
	{
		public boolean accept( File f, String s )
		{
			String toTest = s.toLowerCase();

			if (toTest.endsWith(".jpg") || toTest.endsWith(".png") || toTest.endsWith(".gif"))
				return true;
			else
				return false;
		}
	}
}
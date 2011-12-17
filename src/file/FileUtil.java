package file;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
	
	public static File WorkingDir(){
		return new File( System.getProperty("user.dir") );
	}

	public static List<String> pathTokenList(String path){
		LinkedList<String> resultList = new LinkedList<String>();
		
		Scanner scanner = new Scanner(path).useDelimiter("\\\\");
		//to handle network paths
		if(path.startsWith("\\\\")){
			int endPos = path.indexOf("\\", 2);
			resultList.add(path.substring(0, endPos));
			scanner.next();
			scanner.next();
		}
		
		while(scanner.hasNext()){
			resultList.add(scanner.next());
		}
		
		//check if the last entry (filename) has an extension, if so, split them
		if(resultList.peekLast() != null && resultList.peekLast().contains(".")){
			String lastEntry = resultList.pollLast();
			int dotPosition = lastEntry.lastIndexOf(".");
			
			String filename = lastEntry.substring(0, dotPosition);
			String extension = lastEntry.substring(dotPosition);
			
			resultList.add(filename);
			resultList.add(extension);
		}
		
		return resultList;
		
	}
}

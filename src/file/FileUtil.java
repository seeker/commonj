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

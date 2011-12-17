package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
/**
 * Convenience class for reading text files.
 * Handles file opening, buffering and closing.
 * @author Nicholas Wright
 *
 */
public class TextFileReader {
	public String read(File path) throws IOException{
		String readData = "";
		String read;

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		while((read = br.readLine()) != null)
			readData += read+"\n";
		
		br.close();
		fr.close();

		return readData;
	}
	
	public String read(InputStream is) throws IOException{
		String readData = "";
		String read;
		Reader r = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(r);

		while((read = br.readLine()) != null)
			readData += read+"\n";
		
		br.close();
		r.close();

		return readData;
	}
	
	public String read(String path) throws IOException{
		return read(new File(path));
	}
}

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
package io;

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

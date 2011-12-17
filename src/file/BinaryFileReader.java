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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BinaryFileReader {
	ByteBuffer classBuffer;

	public BinaryFileReader(){
		classBuffer  = ByteBuffer.allocate(31457280); //30mb
	}

	public BinaryFileReader(int buffersize){
		classBuffer = ByteBuffer.allocate(buffersize);
	}

	public byte[] get(String path) throws Exception{
		return get(new File(path));
	}

	public byte[] get(File path) throws IOException{

		BufferedInputStream binary = null;

		if(path.length() > classBuffer.capacity())
			classBuffer = ByteBuffer.allocate((int)path.length());

		FileInputStream fileStream = new FileInputStream(path); 
		binary = new BufferedInputStream(fileStream);
		classBuffer.clear();

		int count = 0;
		byte[] c = new byte[8192];			//Datei wird Block um Block in den Speicher übertragen

		while ((count=binary.read(c)) != -1){
			classBuffer.put(c, 0, count);
		}
		classBuffer.flip();
		byte[] varBuffer = new byte[classBuffer.limit()];

		classBuffer.get(varBuffer);

		fileStream.close();
		binary.close();

		return varBuffer;
	}

}

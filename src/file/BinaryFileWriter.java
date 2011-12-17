package file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryFileWriter {
	public void write(byte[] byteData, String savePath)throws Exception{
		
			File myFile = new File(savePath);
			
		try{
			new File(myFile.getParent()).mkdirs();	// alle Verzeichnisse erstellen
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			myFile.createNewFile();
		}catch(IOException e){
			e.printStackTrace();
		}
			
		FileOutputStream writeMe = new FileOutputStream(myFile);
	     BufferedOutputStream buffOut = new BufferedOutputStream(writeMe,1024);
		buffOut.write(byteData);
		
		writeMe.close();
		buffOut.close();
	}
}

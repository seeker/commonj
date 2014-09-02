/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryFileWriter {
	public void write(byte[] byteData, String savePath) throws IllegalArgumentException, IOException {

		if (savePath == null || savePath.equals("")) {
			throw new IllegalArgumentException("Filepath is invalid");
		}

		if (byteData == null) {
			throw new IllegalArgumentException("Data cannot be null");
		}

		File myFile = new File(savePath);

		new File(myFile.getParent()).mkdirs(); // create all directories
		myFile.createNewFile();

		FileOutputStream writeMe = new FileOutputStream(myFile);
		BufferedOutputStream buffOut = new BufferedOutputStream(writeMe, 1024);
		buffOut.write(byteData);

		buffOut.close();
	}
}

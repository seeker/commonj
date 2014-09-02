/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BinaryFileReader {
	int blockLength = 8192;

	ByteBuffer classBuffer;
	byte[] c = new byte[blockLength];

	public BinaryFileReader() {
		classBuffer = ByteBuffer.allocate(31457280); // 30mb
	}

	/**
	 * Set the Buffers initial capacity in bytes.
	 * 
	 * @param buffersize
	 *            size in bytes
	 */
	public BinaryFileReader(int buffersize) {
		classBuffer = ByteBuffer.allocate(buffersize);
	}

	public BinaryFileReader(int buffersize, int blocklength) {
		classBuffer = ByteBuffer.allocate(buffersize);
		this.blockLength = blocklength;
	}

	public byte[] get(String path) throws Exception {
		return get(new File(path));
	}

	public byte[] get(File path) throws IOException {

		BufferedInputStream binary = null;

		if (path.length() > classBuffer.capacity())
			classBuffer = ByteBuffer.allocate((int) path.length());

		FileInputStream fileStream = new FileInputStream(path);
		binary = new BufferedInputStream(fileStream);
		classBuffer.clear();

		int count = 0;

		while ((count = binary.read(c)) != -1) {
			classBuffer.put(c, 0, count);
		}
		classBuffer.flip();
		byte[] varBuffer = new byte[classBuffer.limit()];

		classBuffer.get(varBuffer);

		fileStream.close();
		binary.close();

		return varBuffer;
	}

	public byte[] getViaDataInputStream(File path) throws IOException {

		DataInputStream dis;
		byte[] data = new byte[(int) path.length()];

		FileInputStream fileStream = new FileInputStream(path);
		dis = new DataInputStream(fileStream);

		dis.readFully(data);

		dis.close();

		return data;
	}

}

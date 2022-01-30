/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Deprecated Use file access provided by the language
 */
// TODO REMOVE after 0.1.1
@Deprecated
public class BinaryFileReader {
	private static final String ERROR_MSG_NULL = "Null is not a valid argument";
	int blockLength = 8192;

	ByteBuffer classBuffer;
	byte[] c = new byte[blockLength];

	@Deprecated
	public BinaryFileReader() {
		classBuffer = ByteBuffer.allocate(31457280); // 30mb
	}

	/**
	 * Set the Buffers initial capacity in bytes.
	 * 
	 * @param buffersize
	 *            size in bytes
	 */
	@Deprecated
	public BinaryFileReader(int buffersize) {
		classBuffer = ByteBuffer.allocate(buffersize);
	}

	@Deprecated
	public BinaryFileReader(int buffersize, int blocklength) {
		classBuffer = ByteBuffer.allocate(buffersize);
		this.blockLength = blocklength;
	}

	@Deprecated
	public byte[] get(String path) throws Exception {
		return get(Paths.get(path));
	}
	
	@Deprecated
	public byte[] get(Path path) throws IOException, IllegalArgumentException {
		if(path == null) {
			throw new IllegalArgumentException(ERROR_MSG_NULL);
		}
		
		BufferedInputStream binary = null;
		long fileSize = Files.size(path);

		if (fileSize > classBuffer.capacity())
			classBuffer = ByteBuffer.allocate((int) fileSize);

		InputStream fileStream = Files.newInputStream(path);
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

	/**
	 * Use {@link BinaryFileReader#get(Path)} instead.
	 */
	@Deprecated
	public byte[] get(File path) throws IOException {
		return get(path.toPath());
	}

	@Deprecated
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

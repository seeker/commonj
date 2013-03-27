package com.github.dozedoff.commonj.encoding;


public class LZ77Encoder {
	StringBuilder dataBuffer;
	StringBuilder searchBuffer;
	StringBuilder previewBuffer;

	public LZ77Encoder() {
		clear();
	}
	
	public void addData(String data) {
		dataBuffer.append(data);
	}
	
	public void clear() {
		dataBuffer = new StringBuilder();
		searchBuffer = new StringBuilder();
		previewBuffer = new StringBuilder();
	}
	
	public void encode() {
		
	}
}
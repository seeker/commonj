package com.github.dozedoff.commonj.encoding;

import java.security.InvalidParameterException;

public class LZ77Encoder {
	StringBuilder dataBuffer;
	StringBuilder searchBuffer;
	StringBuilder previewBuffer;
	StringBuilder outputBuffer;

	int searchBufferSize, previewBufferSize;

	public LZ77Encoder(int searchBufferSize, int previewBufferSize) {
		this.searchBufferSize = searchBufferSize;
		this.previewBufferSize = previewBufferSize;

		if (searchBufferSize <= 0) {
			throw new InvalidParameterException(
					"Search buffer must be larger than 0");
		}

		if (previewBufferSize <= 0) {
			throw new InvalidParameterException(
					"Preview buffer must be larger than 0");
		}

		clear();
	}

	public void addData(String data) {
		dataBuffer.append(data);
	}

	public void clear() {
		dataBuffer = new StringBuilder();
		searchBuffer = new StringBuilder(searchBufferSize);
		previewBuffer = new StringBuilder(previewBufferSize);
		outputBuffer = new StringBuilder();
	}

	public void encode() {
		preLoadBuffer();
		while (dataBuffer.length() != 0 && previewBuffer.length() != 0) {
			if (searchBuffer.length() == 0) {
				outputBuffer.append(0).append(0)
						.append(previewBuffer.charAt(0));
				bufferShift();
				continue;
			}

		}
	}

	private TokenMatch findBestMatch() {
		if (searchBuffer.length() == 0) {
			outputBuffer.append(0).append(0).append(previewBuffer.charAt(0));
			return new TokenMatch(0, 0, previewBuffer.charAt(0));
		}

		int searchBufferCursor = searchBuffer.length();
		int previewBufferCursor = 0;
		TokenMatch bestMatch = null;
		int symbolComparisonWindow = 1;
		
		String symbolGroup = previewBuffer.substring(0, symbolComparisonWindow);
		int instanceIndex = searchBuffer.indexOf(symbolGroup);

		while (instanceIndex != -1) {
			instanceIndex = searchBuffer.indexOf(symbolGroup);
			
			TokenMatch match = findLargestMatch(instanceIndex);
			
			if(match.getMatchSize() > bestMatch.getMatchSize()) {
				bestMatch = match;
			}
			
		}

		return null;
	}
	
	private TokenMatch findLargestMatch(int searchBufferIndex) {
		return null;
	}

	public String getTokens() {
		return outputBuffer.toString();
	}

	private void preLoadBuffer() {
		int preloadSize = 0;

		if (dataBuffer.length() >= previewBufferSize) {
			preloadSize = previewBufferSize;
		} else {
			preloadSize = dataBuffer.length();
		}

		previewBuffer.append(dataBuffer.subSequence(0, preloadSize));
		dataBuffer.delete(0, preloadSize);
	}

	private void bufferShift() {
		searchBuffer.append(previewBuffer.charAt(0));
		if (searchBuffer.length() > searchBufferSize) {
			searchBuffer.deleteCharAt(0);
		}

		previewBuffer.deleteCharAt(0);
		previewBuffer.append(dataBuffer.charAt(0));
		dataBuffer.deleteCharAt(0);
	}

	class TokenMatch {
		int searchBufferOffset, matchSize;
		char nextSymbol;

		public TokenMatch(int searchBufferOffset, int matchSize, char nextSymbol) {
			super();
			this.searchBufferOffset = searchBufferOffset;
			this.matchSize = matchSize;
			this.nextSymbol = nextSymbol;
		}

		public int getSearchBufferOffset() {
			return searchBufferOffset;
		}

		public void setSearchBufferOffset(int searchBufferOffset) {
			this.searchBufferOffset = searchBufferOffset;
		}

		public int getMatchSize() {
			return matchSize;
		}

		public void setMatchSize(int matchSize) {
			this.matchSize = matchSize;
		}

		public char getNextSymbol() {
			return nextSymbol;
		}

		public void setNextSymbol(char nextSymbol) {
			this.nextSymbol = nextSymbol;
		}
	}
}
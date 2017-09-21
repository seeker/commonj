/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.image;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sun.awt.image.ImageFormatException;

@SuppressWarnings("restriction")
public class SubsamplingImageLoader {
	// TODO throw exception for negative dimensions
	public static Image loadAsImage(Path imagepath, Dimension targetDimension) throws ImageFormatException, IOException {
		ImageInputStream iis = getImageInputStream(imagepath);
		ImageReader reader = getImageReader(iis);

		if (reader == null) {
			iis.close();
			throw new ImageFormatException("Could not decode file");
		}

		Image image = subsampleRead(iis, reader, targetDimension);
		iis.close();
		return image;
	}

	public static JLabel loadAsLabel(Path imagepath, Dimension targetDimension) throws ImageFormatException, IOException {
		Image image = loadAsImage(imagepath, targetDimension);
		ImageIcon imageicon = new ImageIcon(image);
		JLabel imageLabel = new JLabel(imageicon, JLabel.CENTER);
		return imageLabel;
	}

	private static Image subsampleRead(ImageInputStream iis, ImageReader reader, Dimension targetDimension) throws IOException {
		ImageReadParam readerParameters = reader.getDefaultReadParam();
		reader.setInput(iis, true, true);

		int readerWidth = reader.getWidth(0);
		int readerHeight = reader.getHeight(0);

		Dimension imageSize = new Dimension(readerWidth, readerHeight);
		int sampleRate = getSampleRate(imageSize, targetDimension);

		readerParameters.setSourceSubsampling(sampleRate, sampleRate, 0, 0);

		Image image = reader.read(0, readerParameters);
		return image;
	}

	private static ImageInputStream getImageInputStream(Path image) throws IOException {
		InputStream is = Files.newInputStream(image);
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		return iis;
	}

	private static int getSampleRate(Dimension image, Dimension displayArea) {
		double heightRatio = (double) image.getHeight() / (double) displayArea.getHeight();
		double widthRato = (double) image.getWidth() / (double) displayArea.getWidth();

		int xSampleRate = (int) Math.ceil(heightRatio);
		int ySampleRate = (int) Math.ceil(widthRato);

		int sampleRate = Math.max(xSampleRate, ySampleRate);

		if (sampleRate < 1) {
			sampleRate = 1;
		}

		return sampleRate;
	}

	private static ImageReader getImageReader(ImageInputStream iis) {
		Iterator<?> iter = ImageIO.getImageReaders(iis);
		if (!iter.hasNext()) {
			return null;
		}

		ImageReader reader = (ImageReader) iter.next();
		return reader;
	}
}

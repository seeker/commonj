/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for converting images.
 * 
 * @author Nicholas Wright
 *
 */
public class ImageUtil {
	private static final ColorConvertOp colorConverter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
	private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	private static int resizeType = BufferedImage.TYPE_INT_ARGB_PRE;

	static {
		resizeType = getResizeImageType();
	}

	private static int getResizeImageType() {
		logger.debug("Java version: {}, {}, {}", System.getProperty("java.vendor"), System.getProperty("java.vm.name"),
				System.getProperty("java.version"));
		if ((!System.getProperty("java.vm.name").startsWith("OpenJDK")) && System.getProperty("java.version").startsWith("1.7")) {
			logger.debug("Selected TYPE_INT_ARGB, value: ({})", BufferedImage.TYPE_INT_ARGB);
			logger.debug("You should only see this if you are running Oracle JRE/JDK 7");
			return BufferedImage.TYPE_INT_ARGB;
		}

		logger.debug("Selected TYPE_INT_ARGB_PRE, value: ({})", BufferedImage.TYPE_INT_ARGB_PRE);
		return BufferedImage.TYPE_INT_ARGB_PRE;
	}

	/**
	 * Covnvert the image to gray-scale by using the JRE built-in
	 * {@link ColorSpace} {@link ColorSpace#CS_GRAY}.
	 * 
	 * @param image
	 *            to convert to gray-scale
	 * @return the converted gray-scale image
	 */
	public static BufferedImage toGrayscale(BufferedImage image) {
		colorConverter.filter(image, image);
		return image;
	}

	/**
	 * Resize a image to the given dimensions using Java AWT.
	 * 
	 * @param image
	 *            to resize
	 * @param width
	 *            of the resized image
	 * @param height
	 *            of the resized image
	 * @return the resized image
	 */
	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, resizeType);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Copy the pixel values of the image into a matrix. The method assumes that
	 * the image is gray-scale, non-gray-scale images will result in undefined
	 * behaviour.
	 * 
	 * @param grayscaleImage
	 *            gray scale image
	 * @return matrix with pixel values
	 */
	public static double[][] toDoubleMatrix(BufferedImage grayscaleImage) {
		int width = grayscaleImage.getWidth();
		int height = grayscaleImage.getHeight();
		double reducedValues[][] = new double[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				reducedValues[x][y] = getBlue(grayscaleImage, x, y);
			}
		}

		return reducedValues;
	}

	private static int getBlue(BufferedImage img, int x, int y) {
		return (img.getRGB(x, y)) & 0xff;
	}

	/**
	 * Read a image from a {@link InputStream}. This is a trivial wrapper around
	 * {@link ImageIO#read(InputStream).
	 * 
	 * @param is
	 *            stream containing the image to read
	 * @return read image as a {@link BufferedImage}
	 * 
	 * @throws IOException
	 *             if there is a issue reading the underling stream
	 */
	public static BufferedImage readImage(InputStream is) throws IOException {
		return ImageIO.read(is);
	}
}

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

	public static BufferedImage toGrayscale(BufferedImage image) {
		colorConverter.filter(image, image);
		return image;
	}

	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, resizeType);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	/**
	 * Copy the pixel values of the image into a matrix. The method assumes that the image is grayscale, non-grayscale images will result in
	 * undefined behavior.
	 * 
	 * @param grayscaleImage
	 *            gray scale image
	 * @return matrix with pixel values
	 */
	public static double[][] toDoubleMatrix(BufferedImage grayscaleImage) {
		int cols = grayscaleImage.getWidth();
		int rows = grayscaleImage.getHeight();
		double reducedValues[][] = new double[cols][rows];

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				reducedValues[row][col] = getBlue(grayscaleImage, col, row);
			}
		}

		return reducedValues;
	}

	private static int getBlue(BufferedImage img, int x, int y) {
		return (img.getRGB(x, y)) & 0xff;
	}

	public static BufferedImage readImage(InputStream is) throws IOException {
		return ImageIO.read(is);
	}
}

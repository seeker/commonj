package com.github.dozedoff.commonj.util;

import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
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
	
	public static Mat openCvLoadGrayscale(Path file) throws IIOException {
		Mat image = imread(file.toString(), CV_LOAD_IMAGE_GRAYSCALE);

		if (image.empty()) {
			throw new IIOException("Image " + file + " is empty");
		}

		return image;
	}

	public static Mat openCvResizeImage(Mat image, int width, int height) {
		Size sz = new Size(width, height);
		Mat resized = new Mat(sz);
		resize(image, resized, sz);

		return resized;
	}

	public static double[][] openCvToMatrix(Mat image) {
		int rows = image.rows();
		int cols = image.cols();
		double matrix[][] = new double[rows][cols];

		UByteBufferIndexer idx = image.createIndexer();

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				matrix[row][col] = idx.get(row, col);
			}
		}

		return matrix;
	}

	public static Mat openCvConvertToGrayscale(Mat image) {
		Mat gray = new Mat();
		cvtColor(image, gray, org.bytedeco.javacpp.opencv_core.CV_8UC1);
		return gray;
	}
}

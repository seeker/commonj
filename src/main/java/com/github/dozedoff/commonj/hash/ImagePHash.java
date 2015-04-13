package com.github.dozedoff.commonj.hash;

/*
 * pHash-like image hash.
 * Author: Elliot Shepherd (elliot@jarofworms.com)
 * Based On: http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
 * 
 * Original Source:		http://pastebin.com/Pj9d8jt5#
 */

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.helper.TransformHelper;
import com.github.dozedoff.commonj.util.Bits;

public class ImagePHash {
	private static final int DEFAULT_RESIZED_IMAGE_SIZE = 32;
	private static final int DEFAULT_DCT_MATRIX_SIZE = 8;

	private int resizedImageSize = 0;
	private int dctMatrixSize = 0;
	private static int resizeType = BufferedImage.TYPE_INT_ARGB_PRE;
	private static final ColorConvertOp colorConverter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
	private TransformHelper transformHelper;

	private static final Logger logger = LoggerFactory.getLogger(ImagePHash.class);

	static {
		resizeType = getResizeImageType();
	}

	public ImagePHash() {
		this(DEFAULT_RESIZED_IMAGE_SIZE, DEFAULT_DCT_MATRIX_SIZE);
	}

	public ImagePHash(int resizedImageSize, int dctMatrixSize) {
		this.resizedImageSize = resizedImageSize;
		this.dctMatrixSize = dctMatrixSize;

		// TODO validate parameters

		transformHelper = new TransformHelper(resizedImageSize);
		ImageIO.setUseCache(false);
	}

	/**
	 * Use {@link Bits#hammingDistance(String, String)} instead.
	 */
	@Deprecated
	public int distance(String s1, String s2) {
		return Bits.hammingDistance(s1, s2);
	}

	/**
	 * 
	 * @param is
	 *            file to hash
	 * @return hash in as long
	 * @throws IOException
	 */
	public long getLongHash(InputStream is) throws IOException {
		return getLongHash(readImage(is));
	}

	public long getLongHash(BufferedImage img) throws IOException {
		double[][] dct = calculateDctMap(img);

		/*
		 * 4. Reduce the DCT. This is the magic step. While the DCT is 32x32, just keep the top-left 8x8. Those represent the lowest
		 * frequencies in the picture.
		 */
		/*
		 * 5. Compute the average value. Like the Average Hash, compute the mean DCT value (using only the 8x8 DCT low-frequency values and
		 * excluding the first term since the DC coefficient can be significantly different from the other values and will throw off the
		 * average).
		 */

		double dctAvg = TransformHelper.dctAverage(dct, dctMatrixSize);
		long hash = convertToLong(dct, dctAvg);
		return hash;
	}

	/**
	 * Use {@link ImagePHash#getLongHash(BufferedImage)} instead.
	 */
	@Deprecated
	public long getLongHashScaledImage(BufferedImage img) throws Exception {
		double[][] dct = calculateDctMapScaledDown(img);
		double dctAvg = TransformHelper.dctAverage(dct, dctMatrixSize);
		long hash = convertToLong(dct, dctAvg);
		return hash;
	}

	/**
	 * 
	 * @param is
	 *            file to hash
	 * @return a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
	 * @throws Exception
	 */
	public String getStringHash(InputStream is) throws IOException {
		/*
		 * 6. Further reduce the DCT. This is the magic step. Set the 64 hash bits to 0 or 1 depending on whether each of the 64 DCT values
		 * is above or below the average value. The result doesn't tell us the actual low frequencies; it just tells us the very-rough
		 * relative scale of the frequencies to the mean. The result will not vary as long as the overall structure of the image remains the
		 * same; this can survive gamma and color histogram adjustments without a problem.
		 */

		long hash = getLongHash(is);
		hash = Long.rotateRight(hash, 1);
		return Long.toBinaryString(hash);
	}

	/**
	 * Use {@link ImagePHash#getStringHash(InputStream)} instead.
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String getHash(InputStream is) throws Exception {
		return getStringHash(is);
	}

	private static BufferedImage readImage(InputStream is) throws IOException {
		return ImageIO.read(is);
	}

	private double[][] reduceColor(BufferedImage img) {
		double reducedValues[][] = new double[resizedImageSize][resizedImageSize];

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				reducedValues[x][y] = getBlue(img, x, y);
			}
		}

		return reducedValues;
	}

	/**
	 * This method should not be public, there will be no replacement.
	 */

	@Deprecated
	public double[][] calculateDctMap(InputStream is) throws IOException {
		BufferedImage img = readImage(is);

		return calculateDctMap(img);
	}

	public double[][] calculateDctMap(BufferedImage img) throws IOException {
		/*
		 * 1. Reduce size. Like Average Hash, pHash starts with a small image. However, the image is larger than 8x8; 32x32 is a good size.
		 * This is really done to simplify the DCT computation and not because it is needed to reduce the high frequencies.
		 */
		img = resize(img, resizedImageSize, resizedImageSize);
		return calculateDctMapScaledDown(img);
	}

	public double[][] calculateDctMapScaledDown(BufferedImage img) throws IOException {
		/*
		 * 2. Reduce color. The image is reduced to a grayscale just to further simplify the number of computations.
		 */
		BufferedImage grayscaleImage = grayscale(img);

		double[][] reducedColorValues = reduceColor(grayscaleImage);

		/*
		 * 3. Compute the DCT. The DCT separates the image into a collection of frequencies and scalars. While JPEG uses an 8x8 DCT, this
		 * algorithm uses a 32x32 DCT.
		 */
		double[][] dctMap = transformHelper.transformDCT(reducedColorValues);

		return dctMap;
	}

	private long convertToLong(double[][] dctVals, double avg) {
		if (dctMatrixSize > 9) {
			throw new IllegalArgumentException("The selected smallerSize value is to big for the long datatype");
		}

		long hash = 0;

		for (int x = 0; x < dctMatrixSize; x++) {
			for (int y = 0; y < dctMatrixSize; y++) {
				hash += (dctVals[x][y] > avg ? 1 : 0);
				hash = Long.rotateLeft(hash, 1);
			}
		}

		return hash;
	}

	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, resizeType);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	private static BufferedImage grayscale(BufferedImage img) {
		colorConverter.filter(img, img);
		return img;
	}

	private static int getBlue(BufferedImage img, int x, int y) {
		return (img.getRGB(x, y)) & 0xff;
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
}

/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.hash;

/*
 * pHash-like image hash.
 * Author: Elliot Shepherd (elliot@jarofworms.com)
 * Based On: http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
 * 
 * Original Source:		http://pastebin.com/Pj9d8jt5#
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.github.dozedoff.commonj.helper.TransformHelper;
import com.github.dozedoff.commonj.util.Bits;
import com.github.dozedoff.commonj.util.ImageUtil;

/**
 * Class for calculating a DCT based hash for images.
 * 
 * @author Nicholas Wright
 *
 */
public class ImagePHash {
	private static final int DEFAULT_RESIZED_IMAGE_SIZE = 32;
	private static final int DEFAULT_DCT_MATRIX_SIZE = 8;

	private int resizedImageSize = 0;
	private int dctMatrixSize = 0;
	private TransformHelper transformHelper;

	/**
	 * Create a default hasher for images with a size of 32x32.
	 */
	public ImagePHash() {
		this(DEFAULT_RESIZED_IMAGE_SIZE, DEFAULT_DCT_MATRIX_SIZE);
	}

	/**
	 * Create a new hasher for the given image size and DCT matrix
	 * 
	 * @param resizedImageSize
	 *            size of the image
	 * @param dctMatrixSize
	 *            size of the top left portion of the DCT matrix to keep
	 */
	public ImagePHash(int resizedImageSize, int dctMatrixSize) {
		this.resizedImageSize = resizedImageSize;
		this.dctMatrixSize = dctMatrixSize;

		// TODO validate parameters

		transformHelper = new TransformHelper(resizedImageSize);
		ImageIO.setUseCache(false);
	}

	/**
	 * 
	 * @param is
	 *            file to hash
	 * @return hash in as long
	 * @throws IOException
	 *             if there is an error reading the stream
	 */
	public long getLongHash(InputStream is) throws IOException {
		return getLongHash(ImageUtil.readImage(is));
	}

	/**
	 * Calculate the hash for a image.
	 * 
	 * @param img
	 *            image to calculate the hash for
	 * @return hash for the image, encoded as a long
	 * @throws IOException if there is a error 
	 */
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
	 * 
	 * @param is
	 *            file to hash
	 * @return a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
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
	 * Calculates the DCT map of the image.
	 * 
	 * @param img
	 *            image to use
	 * @return the calculated DCT matrix
	 */
	public double[][] calculateDctMap(BufferedImage img) throws IOException {
		/*
		 * 1. Reduce size. Like Average Hash, pHash starts with a small image. However, the image is larger than 8x8; 32x32 is a good size.
		 * This is really done to simplify the DCT computation and not because it is needed to reduce the high frequencies.
		 */
		img = ImageUtil.resizeImage(img, resizedImageSize, resizedImageSize);
		return calculateDctMapScaledDown(img);
	}

	/**
	 * Calculate the DCT map for a scaled down image.
	 * 
	 * @param img
	 *            image resized to the image size specified for this hasher
	 * @return a DCT matrix for the provided image
	 */
	public double[][] calculateDctMapScaledDown(BufferedImage img) throws IOException {
		/*
		 * 2. Reduce color. The image is reduced to a grayscale just to further simplify the number of computations.
		 */
		BufferedImage grayscaleImage = ImageUtil.toGrayscale(img);

		double[][] reducedColorValues = ImageUtil.toDoubleMatrix(grayscaleImage);

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
}

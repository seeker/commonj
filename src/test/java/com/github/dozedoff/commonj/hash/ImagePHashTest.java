/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.hash;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImagePHashTest {
	private static Path testImage, testImageSmall;
	private int imageSize = 32;

	private ImagePHash iph;

	private static ColorConvertOp colorConvert;

	@BeforeClass
	public static void setUpClass() throws Exception {
		testImage = Paths.get(Thread.currentThread().getContextClassLoader().getResource("testImage.jpg").toURI());
		testImageSmall = Paths.get(Thread.currentThread().getContextClassLoader().getResource("testImage_small.jpg").toURI());
		colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
	}

	@Before
	public void setUp() throws Exception {
		iph = new ImagePHash();
	}

	@Test
	public void testgetLongHashCompareScaledandUnscaled() throws Exception {
		long normal = hashWithNoScale();
		long scaled = hashWithScale();

		assertThat(scaled, is(normal));
	}

	@Test
	public void testCompareScaledSourceImage() throws Exception {
		long normal = hashImage(testImage);
		long scaled = hashImage(testImageSmall);

		assertThat(getHammingDistance(normal, scaled), is(4));
	}

	@Test
	public void testSourceImageHash() throws Exception {
		long normal = hashImage(testImage);

		assertThat(normal, is(-6261023631344080448L));
	}

	@Test
	public void testScaledSourceImageHash() throws Exception {
		long scaled = hashImage(testImageSmall);

		assertThat(scaled, is(-6261023624918439488L));
	}

	@Test
	public void testGrayScaleConversionDirect() throws Exception {
		InputStream is = Files.newInputStream(testImage);
		BufferedImage img = ImageIO.read(is);

		colorConvert.filter(img, img);

		assertThat(img.getRGB(100, 100), is(-197380));
	}

	@Test
	public void testGrayScaleConversionDirectWithResize() throws Exception {
		InputStream is = Files.newInputStream(testImage);
		BufferedImage img = ImageIO.read(is);

		img = ImagePHash.resize(img, 32, 32);

		colorConvert.filter(img, img);

		assertThat(img.getRGB(10, 10), is(-1776412));
	}

	@Test
	public void testGrayScaleConversionNewObject() throws Exception {
		InputStream is = Files.newInputStream(testImage);
		BufferedImage img = ImageIO.read(is);

		BufferedImage grayImage = colorConvert.filter(img, null);

		assertThat(grayImage.getRGB(100, 100), is(-197380));
	}

	private int getHammingDistance(long a, long b) {
		long xor = a ^ b;
		int distance = Long.bitCount(xor);
		return distance;
	}

	private long hashImage(Path path) throws IOException, Exception {
		return iph.getLongHash(new BufferedInputStream(Files.newInputStream(path)));
	}

	private long hashWithNoScale() throws Exception {
		return hashImage(testImage);
	}

	private long hashWithScale() throws Exception {
		BufferedImage bi = ImageIO.read(testImage.toFile());
		bi = ImagePHash.resize(bi, imageSize, imageSize);

		return iph.getLongHashScaledImage(bi);
	}
}

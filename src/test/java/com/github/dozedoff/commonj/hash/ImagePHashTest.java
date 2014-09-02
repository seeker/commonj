/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.hash;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImagePHashTest {
	private static Path testImage;
	private int imageSize = 32;

	private ImagePHash iph;

	@BeforeClass
	public static void setUpClass() throws Exception {
		testImage = Paths.get(Thread.currentThread().getContextClassLoader().getResource("test.jpg").toURI());
	}

	@Before
	public void setUp() throws Exception {
		iph = new ImagePHash();
	}

	@Test
	public void testgetLongHashCompareScaledandUnscaled() throws Exception {
		long noPrescale = hashWithNoScale();
		long withPrescale = hashWithScale();

		assertThat(withPrescale, is(noPrescale));
	}

	private long hashWithNoScale() throws Exception {
		InputStream is = new BufferedInputStream(Files.newInputStream(testImage));
		return iph.getLongHash(is);
	}

	private long hashWithScale() throws Exception {
		BufferedImage bi = ImageIO.read(testImage.toFile());
		bi = ImagePHash.resize(bi, imageSize, imageSize);

		return iph.getLongHashScaledImage(bi);
	}
}

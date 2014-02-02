/*  Copyright (C) 2013  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

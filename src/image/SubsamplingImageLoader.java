/*  Copyright (C) 2012  Nicholas Wright

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
package image;

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

public class SubsamplingImageLoader {
	public static JLabel loadImage(Path imagepath, Dimension targetDimension) throws ImageFormatException, IOException {
		ImageInputStream iis = getImageInputStream(imagepath);
		ImageReader reader = getImageReader(iis);

		if (reader == null) {
			throw new ImageFormatException("Could not decode file");
		}

		Image image = subsampleRead(iis, reader, targetDimension);
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
		double heightRatio = (double) image.getHeight() / (double)displayArea.getHeight();
		double widthRato = (double) image.getWidth() / (double)displayArea.getWidth();
		
		int xSampleRate =  (int)Math.ceil(heightRatio);
	    int ySampleRate = (int)Math.ceil(widthRato);

	    int sampleRate = Math.max(xSampleRate, ySampleRate);
	    
	    if(sampleRate < 1){
	    	sampleRate = 1;
	    }
	    
	    return sampleRate;
	}
	
	private static ImageReader getImageReader(ImageInputStream iis) {
	    Iterator<?> iter = ImageIO.getImageReaders(iis);
	    if (!iter.hasNext()) {
	        return null;
	    }

	    ImageReader reader = (ImageReader)iter.next();
	    return reader;
	}
}

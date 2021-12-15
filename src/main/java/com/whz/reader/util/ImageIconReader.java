package com.whz.reader.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Simple image reader class which takes a file name and returns a BufferedImage
 * created from an image located in the resources folder.
 * 
 * @author Timon Schwalbe
 */
public class ImageIconReader {

	private static final Logger log = Logger.getLogger(ImageIconReader.class.getName());

	/**
	 * Finds and returns the image in the defined resource folder based on the given
	 * file name. If the search result returns null, the default image will be used
	 * instead.
	 * 
	 * @param fileName - File name of the image to search for
	 * @return BufferedImage - The generated image related to the fileName
	 */
	public static BufferedImage readImage(String fileName) {
		BufferedImage image = null;

		try {
			URL resource = ImageIconReader.class.getClassLoader().getResource(fileName);
			image = ImageIO.read(resource);
		} catch (IllegalArgumentException e) {
			log.warning("Wrong input: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not retrieve image: " + e.getMessage());
			e.printStackTrace();
		}

		return image;
	}

}

package javaNK.util.files;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageHandler extends FileLoader
{
	private static Map<Object, String> memory = new HashMap<Object, String>();
	
	/**
	 * Load a BufferedImage object from resources folder.
	 * @param path - Full logic path of the file (including file type ending)
	 * @return a BufferedImage object.
	 */
	public static BufferedImage loadImage(String path) {
		BufferedImage image = load(path);
		memory.put(image, path);
		return image;
	}
	
	/**
	 * Load an ImageIcon object from resources folder.
	 * @param path - Full logic path of the file (including file type ending)
	 * @return an ImageIcon object.
	 */
	public static ImageIcon loadIcon(String path) {
		ImageIcon icon = new ImageIcon(load(path));
		memory.put(icon, path);
		return icon;
	}
	
	private static BufferedImage load(String path) {
		path = HEADER_PATH + path;

		try	{
			File file = new File(path);
			InputStream fis = new FileInputStream(file);
			BufferedImage image = ImageIO.read(fis);
			return image;
		}
		catch (Exception e) { error("image", path, e); }
		
		return null;
	}
	
	/**
	 * Create an image in resources folder.
	 * @param image - BufferedImage object that was created during runtime
	 * @param path - Where to save the image
	 * @return true if the image was created successfully or false otherwise.
	 */
	public static boolean create(BufferedImage image, String path) {
		path = HEADER_PATH + path;
		
		try {
			File imageFile = new File(path);
			ImageIO.write(image, "png", imageFile.getAbsoluteFile());
			return true;
		}
		catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Copy an image.
	 * @param source - The source image to copy
	 * @return a copy of the source.
	 */
	public static BufferedImage cloneImage(BufferedImage source) {
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	/**
	 * Copy an icon.
	 * @param source - The source, as a BufferedImage object, to copy
	 * @return a copy of the source.
	 */
	public static ImageIcon cloneIcon(BufferedImage source) {
		return new ImageIcon(cloneImage(source));
	}
	
	/**
	 * Copy an icon.
	 * @param source - The source, as an ImageIcon object, to copy
	 * @return a copy of the source.
	 */
	public static ImageIcon cloneIcon(ImageIcon source) {
		BufferedImage sourceImage = (BufferedImage) source.getImage();
		return new ImageIcon(cloneImage(sourceImage));
	}
	
	/**
	 * Get the absolute path of an image that was previously loaded via the ImageHandler.
	 * 
	 * @param image - The image to get the path of
	 * @return the absolute path of the image.
	 */
	public static String getPath(Object image) { return memory.get(image); }
	
	/**
	 * Check if one image equals another (by every pixel).
	 * @param imgA - The first image
	 * @param imgB - The second image
	 * @return true if both images are equal.
	 */
	public static boolean compare(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight())
			return false;

		int width  = imgA.getWidth();
		int height = imgA.getHeight();

		//loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
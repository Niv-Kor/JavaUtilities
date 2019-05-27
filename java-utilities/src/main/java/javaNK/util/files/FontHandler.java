package javaNK.util.files;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FontHandler extends FileLoader
{
	/**
	 * Font styles to place as an argument in "load()" method.
	 * @author Niv Kor
	 */
	public static enum FontStyle {
		PLAIN("Regular", 1),
		ITALIC("Italic", 0),
		EXTRA_LIGHT("ExtraLight", 1),
		EXTRA_LIGHT_ITALIC("ExtraLightItalic", 0),
		THIN("Thin", 1),
		THIN_ITALIC("ThinItalic", 0),
		LIGHT("Light", 1),
		LIGHT_ITALIC("LightItalic", 0),
		MEDIUM("Medium", 1),
		MEDIUM_ITALIC("MediumItalic", 0),
		SEMI_BOLD("SemiBold", 2),
		SEMI_BOLD_ITALIC("SemiBoldItalic", 0),
		BOLD("Bold", 2),
		BOLD_ITALIC("BoldItalic", 0),
		EXTRA_BOLD("ExtraBold", 2),
		EXTRA_BOLD_ITALIC("ExtraBoldItalic", 0),
		BLACK("Black", 2),
		BLACK_ITALIC("BlackItalic", 0);
		
		private String formalName;
		private int hardnessGroup;
		
		private FontStyle(String formalName, int hardnessGroup) {
			this.formalName = formalName;
			this.hardnessGroup = hardnessGroup;
		}
		
		/**
		 * Find a similar font style. Used when the requested style is not available.
		 * Big bold styles will return BOLD, lighter styles will return PLAIN,
		 * and all compound italic styles will return ITALIC.
		 * @param style - The style to convert
		 * @return a similar font style.
		 */
		public static FontStyle getSimilar(FontStyle style) {
			switch(style.hardnessGroup) {
				case 0: return ITALIC;
				case 1: return PLAIN;
				case 2: return BOLD;
				default: return PLAIN;
			}
		}
		
		/**
		 * @return the formal name of the font as it's written in resources folder. 
		 */
		public String formalName() { return formalName; }
	}
	
	private static final String HEADER_PATH = FileLoader.HEADER_PATH + "fonts/";
	private static final Font DEF_FONT = new Font("Arial", Font.PLAIN, 12);
	private static final String FILE_TYPE = ".ttf";
	
	/**
	 * Load a Font object from resources folder.
	 * Fonts that are unfamiliar with the JVM will be added to it permanently.
	 * If a font does not come in certain style that was requested,
	 * the method will return a one with a similar available style.
	 * If a font cannot be loaded for some reason, a default font will be returned. 
	 * @param fontName - The name of the font as it's written in resourced folder
	 * @param style - FontStyle constant
	 * @param size - The size of the font
	 * @return a Font object.
	 */
	public static Font load(String fontName, FontStyle style, double size) {
		String path = HEADER_PATH + fontName + "/" + fontName + "-";
		File fontFile;
		Font font;
		
		try {
			//we have the font and style that client requested
			fontFile = new File(path + style.formalName + FILE_TYPE);
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
		}
		catch(FontFormatException | IOException e) {
			//we probably have the font that client requested but not the style
			try { 
				//check if we have a similar style to the one that client requested in resources
				FontStyle similarStyle = FontStyle.getSimilar(style);
				fontFile = new File(path + similarStyle.formalName + FILE_TYPE);
				font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			}
			catch(FontFormatException | IOException ex1) {
				try {
					//we don't have that similar style, try plain
					fontFile = new File(path + FontStyle.PLAIN.formalName + FILE_TYPE);
					font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
				}
				catch(FontFormatException | IOException ex2) {
					//we don't have the font at all so return pre-defined default font
					font = DEF_FONT;
				}
			}
		}
		
		//add that font to jvm graphics enviroment if it's not already there
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		if (!Arrays.asList(ge.getAllFonts()).contains(font)) ge.registerFont(font);
		
		//set size and return
		return font.deriveFont((float) size);
	}
}
package javaNK.util.GUI;
import java.awt.Color;
import javaNK.util.math.RNG;

public class ColorPicker
{
	public static Color generate(Integer red, Integer green, Integer blue, Integer alpha) {
		int genRed = (red != null) ? red : RNG.generate(0x0, 0xFF);
		int genGrn = (green != null) ? green : RNG.generate(0x0, 0xFF);
		int genBlu = (blue != null) ? blue : RNG.generate(0x0, 0xFF);
		int genAlp = (alpha != null) ? alpha : RNG.generate(0x0, 0xFF);
		return new Color(genRed, genGrn, genBlu, genAlp);
	}
	
	public static Color generate(Integer red, Integer green, Integer blue) {
		return generate(red, green, blue, 0xFF);
	}
}

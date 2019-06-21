package javaNK.util.math;
import java.awt.Component;
import java.awt.Dimension;

public class DimensionalHandler
{
	/**
	 * Create a new dimension, using another dimension and modified percentages of its width and height.
	 * 
	 * @param source - The dimension to copy
	 * @param widthPercent - Percentage of the source dimension's width
	 * @param heightPercent - percentage of the source dimension's height
	 * @return a new adjusted Dimension.
	 */
	public static Dimension adjust(Dimension source, double widthPercent, double heightPercent) {
		int width = (int) Percentage.percentOfNum(widthPercent, source.width);
		int height = (int) Percentage.percentOfNum(heightPercent, source.height);
		return new Dimension(width, height);
	}
	
	public static Dimension adjust(Component source, double widthPercent, double heightPercent) {
		return adjust(source.getPreferredSize(), widthPercent, heightPercent);
	}
}
package java.util.math;
public class Range<T extends Number>
{
	private T min, max;
	
	/**
	 * @param min - Minimum number of the range
	 * @param max - Maximum number in the range (included)
	 */
	public Range(T min, T max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Copy constructor.
	 * @param other - The other Range object to copy from
	 */
	public Range(Range<T> other) {
		this.min = other.min;
		this.max = other.max;
	}
	
	/**
	 * Calculate the percentage of a number in the entire range.
	 * (Ex. 49 (number) out of 5-50 is 97.77%).
	 * @param number - The number to take percent of
	 * @return the percentage of the number out of the entire range.
	 */
	public double numOf(double number) {
		double min = (double) this.min.doubleValue();
		double max = (double) this.max.doubleValue();
		
		return Percentage.numOfNum(number - min, max - min);
	}
	
	/**
	 * Calculate the number that X% of the entire range is.
	 * (Ex. 0% (percent) of 12-100 is 12).
	 * @param percent - The percentage
	 * @return a number that is X% of the entire range. 
	 */
	public double percentOf(double percent) {
		double min = (double) this.min.doubleValue();
		double max = (double) this.max.doubleValue();
		
		return Percentage.percentOfNum(percent, max - min) + min;
	}
	
	/**
	 * Check if a number is included within the range.
	 * @param number - The number to check
	 * @return true if the number is within the range or false otherwise.
	 */
	public boolean intersects(T number) {
		double min = (double) this.min.doubleValue();
		double max = (double) this.max.doubleValue();
		
		return number.floatValue() <= max && number.doubleValue() >= min;
	}
	
	/**
	 * Check if a range intersects another range.
	 * (Ex. 67-110 intersects 18-80 but doesn't intersect 256-300).
	 * @param other - The other range
	 * @return true if the number is within the range or false otherwise.
	 */
	public boolean intersects(Range<T> other) {
		double min = (double) this.min.doubleValue();
		double max = (double) this.max.doubleValue();
		
		return other.min.doubleValue() <= max && other.max.doubleValue() >= min;
	}
	
	/**
	 * Calculate the significance of a number within a given range,
	 * choosing from another given range of values.
	 * (Ex. 30 (number) out of 20-200, ranging from the values of 1-10 is 1.5)
	 * @param number - The number to check the significance of
	 * @param range - The range from which the number check its sinificance
	 * @param effectModel - The range of values to consider
	 * @return the significance of a number within the range, considering the range of values it can get.
	 */
	public static <T extends Number> double significanceOf(double number, Range<? extends Number> range,
													 Range<? extends Number> effectModel) {
		
		double rangeOfNum = range.numOf(number);
		double rangePercent = effectModel.percentOf(rangeOfNum);
		return Percentage.limit(rangePercent, effectModel);
	}
	
	/**
	 * @return a random number within the range. 
	 */
	public double generate() { return RNG.generateDouble(min.doubleValue(), max.doubleValue()); }
	
	@Override
	public String toString() {
		double minVal = NumeralHandler.round(min.doubleValue(), 2);
		double maxVal = NumeralHandler.round(max.doubleValue(), 2);
		return "[" + minVal + ", " + maxVal + "]";
	}
	
	/**
	 * @return minimum value of the range.
	 */
	public T getMin() { return min; }
	
	/**
	 * @return maximum value of the range.
	 */
	public T getMax() { return max; }
}
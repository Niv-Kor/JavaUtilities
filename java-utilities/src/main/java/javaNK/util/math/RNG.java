package javaNK.util.math;
import java.util.List;
import java.util.Random;

public class RNG
{
	private static Random rng = new Random();
	
	/**
	 * Generate a random integer from within a range.
	 * This method also supports negative numbers.
	 * 
	 * @param a - Minimum integer
	 * @param b - Maximum integer (included in the range)
	 * @return a random integer within the range.
	 */
	public static int generate(int a, int b) {
		if (a == b) return a;
		if (b < a) { //swap
			int c = a;
			a = b;
			b = c;
		}
		
		//supports negative numbers
		int aTag = a - a, bTag = b - a;
		int result = rng.nextInt((bTag + 1) - aTag) + aTag;
		return -result + b;
	}
	
	/**
	 * Generate a random decimal number from within a range.
	 * This method also supports negative numbers.
	 * 
	 * @param a - Minimum decimal
	 * @param b - Maximum decimal (included in the range)
	 * @return a random decimal number within the range.
	 */
	public static double generateDouble(double a, double b) {
		if (a == b) return a;
		if (b < a) { //swap
			double c = a;
			a = b;
			b = c;
		}
		
		//doublize
		a /= 1.0;
		b /= 1.0;
		
		int aTag = (int) a;
		int bTag = (int) b;
		
		
		double deltaA = a - aTag, deltaB = b - bTag;
		double extraA = 0, extraB = 0;
		
		if (deltaA != 0) {
			do extraA = rng.nextDouble();
			while (extraA < Math.abs(deltaA));
		}
		
		if (deltaB != 0) {
			do extraB = rng.nextDouble();
			while (extraB > Math.abs(deltaB));
		}
		
		int result = generate(aTag, bTag);
		return result + extraA + extraB; 
	}
	
	/**
	 * Complete random boolean statement.
	 * The user has no control whatsoever on the result.
	 * 
	 * @return 'true' or 'false' randomly.
	 */
	public static boolean unstableCondition() {
		return unstableCondition(generateDouble(0, 100));
	}
	
	/**
	 * Random boolean statement.
	 * @param percent - Percentage of getting a 'true'
	 * @return 'true' or 'false' randomly, based on the percentage.
	 */
	public static boolean unstableCondition(double percent) {
		if (percent <= 0) return false;
		else if (percent >= 100) return true;
		else return percent / 100 >= rng.nextDouble();
	}
	
	/**
	 * Generate a random number, within an epsilon percent range of a number.
	 * (Ex. num = 140, epsilon = 30(%); number is generated from 140 - 30% to 140 + 30%).
	 * 
	 * @param anchor - The number to generate another number around 
	 * @param epsilon - Percent from below and above the anchor to generate from
	 * @return a random number within the specified range.
	 */
	public static int generateEpsilonPercentage(double anchor, double epsilon) {
		return RNG.generate((int) Percentage.percentOfNum(100 - epsilon, anchor),
							(int) Percentage.percentOfNum(100 + epsilon, anchor));
	}
	
	/**
	 * Randomly select an object from an Object array.
	 * @param arr - The array to select from
	 * @return random object from the array.
	 */
	public static Object select(Object[] arr) { return arr[zeroToRange(arr.length)]; }
	
	/**
	 * Randomly select a generic type object from a list.
	 * @param list - The list to select from
	 * @return random generic type object from the list.
	 */
	public static <T> T select(List<T> list) { return list.get(zeroToRange(list.size())); }
	
	/**
	 * Randomly select an integer from an int array.
	 * @param arr - The array to select from
	 * @return random integer from the array.
	 */
	public static int select(int[] arr) { return arr[zeroToRange(arr.length)]; }
	
	/**
	 * Randomly select a decimal number from a double array.
	 * @param arr - The array to select from
	 * @return random decimal number from the array.
	 */
	public static double select(double[] arr) { return arr[zeroToRange(arr.length)]; }
	
	/**
	 * Randomly select a character from a char array.
	 * @param arr - The array to select from
	 * @return random character from the array.
	 */
	public static char select(char[] arr) { return arr[zeroToRange(arr.length)]; }
	
	/**
	 * Randomly select a floating point number from a float array.
	 * @param arr - The array to select from
	 * @return random floating point number from the array.
	 */
	public static float select(float[] arr) { return arr[zeroToRange(arr.length)]; }
	
	/**
	 * Generate a random number from 0 to a specified maximum.
	 * @param arrLength - The maximum integer to select from (not included in the range)
	 * @return a random number within 0 and the maximum range.
	 */
	private static int zeroToRange(int arrLength) { return generate(0, arrLength - 1); }
}
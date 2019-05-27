package javaNK.util.math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumeralHandler
{
	/**
	 * Round a decimal number to a fixed number of places after the point.
	 * @param value - The number to round
	 * @param places - Amount of places after the point to show
	 * @return a rounded deciaml number.
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bigDec = new BigDecimal(Double.toString(value));
	    bigDec = bigDec.setScale(places, RoundingMode.HALF_UP);
	    return bigDec.doubleValue();
	}
	
	/**
	 * Round an integer to a fixed number of digits.
	 * @param value - The number to round
	 * @param places - Amount of digits to show
	 * @return a rounded integer.
	 */
	public static int round(int value, int places) {
		return Integer.parseInt(("" + value).substring(0, places));
	}
	
	/**
	 * Count the amount of digits in a number.
	 * @param num - The number to count
	 * @return the amount of digits in the number.
	 */
	public static int countDigits(int num) {
		if (num == 0) return 1;
		
		int counter = 0;
		
		while (num > 0) {
			counter++;
			num /= 10;
		}
		
		return counter;
	}
}
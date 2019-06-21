package javaNK.util.math;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumeralHandler
{
	/**
	 * Round a decimal number to a fixed number of places after the point.
	 * 
	 * @param value - The number to round
	 * @param places - Amount of places after the point to show
	 * @return a rounded decimal number.
	 */
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	 
	    BigDecimal bigDec = new BigDecimal(Double.toString(value));
	    bigDec = bigDec.setScale(places, RoundingMode.HALF_UP);
	    return bigDec.doubleValue();
	}
	
	/**
	 * Round an integer to a fixed number of digits.
	 * 
	 * @param value - The number to round
	 * @param places - Amount of digits to show
	 * @return a rounded integer.
	 */
	public static int round(int value, int places) {
		return Integer.parseInt(("" + value).substring(0, places));
	}
	
	/**
	 * Count the amount of digits in a number.
	 * 
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
	
	/**
	 * Shift an integer number to the right, adding '0's to its left side.
	 * If the number has more digits than requested, digits from the right will be removed instead. 
	 * 
	 * @param number - Integer number to shift right
	 * @param spaces - The amount of digits it should possess
	 * @return a String object of the number, with the exact amount of digits specified.
	 */
	public static String shiftRight(int number, int spaces) {
		int digits = countDigits(number);
		String str = "" + number;
		
		if (digits == spaces) return str;
		else if (digits > spaces) return str.substring(0, spaces);
		else
			for (int i = 0; i < spaces - digits; i++)
				str = "0" + str;
		
		return str;
	}
}
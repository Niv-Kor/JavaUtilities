package javaNK.util.real_time;
import java.time.DayOfWeek;

/**
 * This class provides utilities regarding dates, in bulks of a week.
 * 
 * @author Niv Kor
 */
public class Week
{
	/**
	 * Get an array of 7 days, starting from today.
	 * 
	 * @param today - The first day of the requested week
	 * @return an array of the week.
	 */
	public static DayOfWeek[] getWeek(DayOfWeek today) {
		DayOfWeek[] week = new DayOfWeek[7];
		
		for (int i = 0, j = today.getValue(); i < 7; i++, j++) {
			week[i] = DayOfWeek.of(j);
			if (j == DayOfWeek.SATURDAY.getValue())
				j = DayOfWeek.SUNDAY.getValue();
		}
		
		return week;
	}
	
	/**
	 * Progress a week one day forward.
	 * WARNING: This method changes the original array.
	 * 
	 * @param week - Array of 7 days (not varified)
	 */
	public static void nextDay(DayOfWeek[] week) {
		for (int i = 0; i < week.length; i++) {
			if (week[i] != DayOfWeek.SATURDAY)
				week[i] = DayOfWeek.of(week[i].getValue() + 1);
			else
				week[i] = DayOfWeek.SUNDAY;
		}
	}
	
	/**
	 * Convert a day into a String, displaying the day's formal name,
	 * starting with a capital letter. 
	 * 
	 * @param day - The day to convert into a String object
	 * @return a String with the formal name of that day.
	 */
	public static String toString(DayOfWeek day) {
		return day.name().charAt(0) + day.name().substring(1, day.name().length()).toLowerCase();
	}
	
	/**
	 * Convert a whole week into a String, displaying each day's formal name,
	 * starting with a capital letter. 
	 * 
	 * @param week - The week to convert into a String object
	 * @return a String with each day's formal name
	 */
	public static String toString(DayOfWeek[] week) {
		String str = "[";
		
		for (int i = 0; i < week.length; i++) {
			str = str.concat(toString(week[i]));
			if (i < week.length - 1)
				str = str.concat(", ");
		}
		
		return str.concat("]");
	}
}
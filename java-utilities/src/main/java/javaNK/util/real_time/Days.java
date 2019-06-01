package javaNK.util.real_time;
import java.time.DayOfWeek;

public enum Days {
	SUNDAY(1),
	MONDAY(2),
	TUESDAY(3),
	WEDNESDAY(4),
	THURSDAY(5),
	FRIDAY(6),
	SATURDAY(7);
	
	private int index;
	
	private Days(int index) {
		this.index = index;
	}
	
	public static Days[] getWeek(int today) {
		return getWeek(getDay(today));
	}
	
	public static Days[] getWeek(Days today) {
		Days[] week = new Days[7];
		
		for (int i = 0, j = today.index; i < 7; i++, j++) {
			week[i] = getDay(j);
			if (j == SATURDAY.index) j = SUNDAY.index;
		}
		
		return week;
	}
	
	public static Days getDay(int index) {
		index--;
		while (index < 0) index += 7;
		while (index > 6) index -= 7;
		
		switch(index) {
			case 0: return SUNDAY;
			case 1: return MONDAY;
			case 2: return TUESDAY;
			case 3: return WEDNESDAY;
			case 4: return THURSDAY;
			case 5: return FRIDAY;
			case 6: return SATURDAY;
			default: {
				System.err.println("A day with index of " + index + " does not exist.");
				return null;
			}
		}
	}
	
	public static Days getDay(DayOfWeek day) {
		return valueOf(day.name());
	}
	
	public boolean isBefore(Days other) {
		return index < other.index;
	}
	
	public boolean isAfter(Days other) {
		return index > other.index;
	}
	
	public int getIndex() { return index; }
	public String formalName() { return name().charAt(0) + name().substring(1, name().length()).toLowerCase(); }
}
package javaNK.util.real_time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Date
{
	private int year, month, dayOfMonth, hour, minutes, seconds;
	private long nano;
	private Days day;
	private boolean displayHour;
	
	public Date(DayOfWeek day, int dayOfMonth, int month, int year, int hour, int minutes) {
		this.day = Days.getDay(day);
		this.dayOfMonth = dayOfMonth;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.minutes = minutes;
		this.seconds = 0;
		this.nano = 0;
		this.displayHour = true;
	}
	
	public Date(LocalDateTime dateTime) {
		this.day = Days.getDay(dateTime.getDayOfWeek());
		this.dayOfMonth = dateTime.getDayOfMonth();
		this.month = dateTime.getMonthValue();
		this.year = dateTime.getYear();
		this.hour = dateTime.getHour();
		this.minutes = dateTime.getMinute();
		this.seconds = dateTime.getSecond();
		this.nano = dateTime.getNano();
		this.displayHour = true;
	}
	
	public Date(Date other) {
		this.day = other.day;
		this.dayOfMonth = other.dayOfMonth;
		this.month = other.month;
		this.year = other.year;
		this.hour = other.hour;
		this.minutes = other.minutes;
		this.seconds = other.seconds;
		this.nano = other.nano;
		this.displayHour = other.displayHour;
	}
	
	public String toString() {
		String hourSection;
		
		if (displayHour) {
			String hrExcess = (hour < 10) ? "0" : "";
			String minExcess = (minutes < 10) ? "0" : "";
			hourSection = ", " + hrExcess + hour + ":" + minExcess + minutes;
		}
		else hourSection = "";
		
		String dayExcess = (dayOfMonth < 10) ? "0" : "";
		String monthExcess = (month < 10) ? "0" : "";
		
		return day.formalName() + ", " + dayExcess + dayOfMonth + "/" + monthExcess + month + "/" + year + hourSection;
	}
	
	public boolean equals(Date other) {
		return !isBefore(other) && !isAfter(other);
	}
	
	public boolean isBefore(Date other) {
		return year < other.year ||
			   month < other.month ||
			   dayOfMonth < other.dayOfMonth ||
			   day.isBefore(other.day) ||
			   hour < other.hour ||
			   minutes < other.minutes ||
			   seconds < other.seconds ||
			   nano < other.nano;
	}
	
	public boolean isAfter(Date other) {
		return year > other.year ||
			   month > other.month ||
			   dayOfMonth > other.dayOfMonth ||
			   day.isAfter(other.day) ||
			   hour > other.hour ||
			   minutes > other.minutes ||
			   seconds > other.seconds ||
			   nano > other.nano;
	}
	
	public static Date now() {
		return new Date(LocalDateTime.now());
	}
	
	public void incrementMinute() {
		if (++minutes > 59) {
			minutes = 0;
			if (++hour > 23) {
				hour = 0;
				day = Days.getDay(day.getIndex() + 1);
				
				switch(month) {
					case 1: if (++dayOfMonth > 31) dayOfMonth = 1; month++; break;
					case 2: {
						++dayOfMonth;
						if ((year % 4 == 0 && dayOfMonth > 29) || dayOfMonth > 28) {
							dayOfMonth = 1;
							month++;
						}
						
						break;
					}
					case 3: if (++dayOfMonth > 31) { dayOfMonth = 1; month++; } break;
					case 4: if (++dayOfMonth > 30) { dayOfMonth = 1; month++; } break;
					case 5: if (++dayOfMonth > 31) { dayOfMonth = 1; month++; } break;
					case 6: if (++dayOfMonth > 30) { dayOfMonth = 1; month++; } break;
					case 7: if (++dayOfMonth > 31) { dayOfMonth = 1; month++; } break;
					case 8: if (++dayOfMonth > 31) { dayOfMonth = 1; month++; } break;
					case 9: if (++dayOfMonth > 30) { dayOfMonth = 1; month++; } break;
					case 10: if (++dayOfMonth > 31) { dayOfMonth = 1; month++; } break;
					case 11: if (++dayOfMonth > 30) { dayOfMonth = 1; month++; } break;
					case 12: if (++dayOfMonth > 31) { dayOfMonth = 1; month = 1; year++; } break;
				}
			}
		}
	}
	
	public void decrementMinute() {
		if (--minutes < 0) {
			minutes = 59;
			if (--hour < 0) {
				hour = 23;
				day = Days.getDay(day.getIndex() - 1);
				if (--dayOfMonth < 1) {
					if (--month < 1) {
						month = 12;
						year--;
					}
					switch(month) {
						case 1: dayOfMonth = 31; break;
						case 2: {
							if (year % 4 == 0) dayOfMonth = 29;
							else dayOfMonth = 28;
							break;
						}
						case 3: dayOfMonth = 31; break;
						case 4: dayOfMonth = 30; break;
						case 5: dayOfMonth = 31; break;
						case 6: dayOfMonth = 30; break;
						case 7: dayOfMonth = 31; break;
						case 8: dayOfMonth = 31; break;
						case 9: dayOfMonth = 30; break;
						case 10: dayOfMonth = 31; break;
						case 11: dayOfMonth = 30; break;
						case 12: dayOfMonth = 31; break;
					}
				}
			}
		}
	}
	
	public void incrementMinute(int times) {
		for (int i = 0; i < times; i++) incrementMinute();
	}
	
	public void decrementMinute(int times) {
		for (int i = 0; i < times; i++) decrementMinute();
	}
	
	public void incrementHour() {
		incrementMinute(60);
	}
	
	public void decrementHour() {
		decrementMinute(60);
	}
	
	public void incrementHour(int times) {
		for (int i = 0; i < times; i++) incrementHour();
	}
	
	public void decrementHour(int times) {
		for (int i = 0; i < times; i++) decrementHour();
	}
	
	public void incrementDay() {
		incrementHour(24);
	}
	
	public void decrementDay() {
		decrementHour(24);
	}
	
	public void incrementDay(int times) {
		for (int i = 0; i < times; i++) incrementDay();
	}
	
	public void decrementDay(int times) {
		for (int i = 0; i < times; i++) decrementDay();
	}
	
	public void incrementMonth() {
		int whole = 0;
		
		switch(month) {
			case 1: whole = 31; break;
			case 2: {
				if (year % 4 == 0) whole = 29;
				else whole = 28;
				break;
			}
			case 3: whole = 31; break;
			case 4: whole = 30; break;
			case 5: whole = 31; break;
			case 6: whole = 30; break;
			case 7: whole = 31; break;
			case 8: whole = 31; break;
			case 9: whole = 30; break;
			case 10: whole = 31; break;
			case 11: whole = 30; break;
			case 12: whole = 31; break;
		}
		
		for (int i = 0; i < whole - dayOfMonth; i++) incrementDay();
	}
	
	public void decrementMonth() {
		int whole = 0;
		
		switch(month) {
			case 1: whole = 31; break;
			case 2: {
				if (year % 4 == 0) whole = 29;
				else whole = 28;
				break;
			}
			case 3: whole = 31; break;
			case 4: whole = 30; break;
			case 5: whole = 31; break;
			case 6: whole = 30; break;
			case 7: whole = 31; break;
			case 8: whole = 31; break;
			case 9: whole = 30; break;
			case 10: whole = 31; break;
			case 11: whole = 30; break;
			case 12: whole = 31; break;
		}
		
		for (int i = 0; i < dayOfMonth + whole; i++) decrementDay();
	}
	
	public void incrementMonth(int times) {
		for (int i = 0; i < times; i++) incrementMonth();
	}
	
	public void decrementMonth(int times) {
		for (int i = 0; i < times; i++) decrementMonth();
	}
	
	public void incrementYear() { setYear(year + 1); }
	public void decrementYear() { setYear(year - 1); }
	public void setMinutes(int m) { if (m >= 0 && m <= 59) minutes = m; }
	public void setHour(int h) { if (h >= 0 && h <= 23) hour = h; }
	public void setDay(Days d) { day = d; }
	public void setDayOfMonth(int d) { if (d >= 1 && d <= 31) dayOfMonth = d; }
	public void setMonth(int m) { if (m >= 1 && m <= 12) month = m; }
	public void setYear(int y) { if (y >= 1910 && y <= 9999) year = y; }
	public int getMinutes() { return minutes; }
	public int getHour() { return hour; }
	public Days getDay() { return day; }
	public int getDayOfMonth() { return dayOfMonth; }
	public int getMonth() { return month; }
	public int getYear() { return year; }
	public void displayHour(boolean flag) { displayHour = flag; }
	public boolean isDisplayingHour() { return displayHour; }
}
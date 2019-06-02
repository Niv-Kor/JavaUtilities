package javaNK.util.networking;
import java.io.IOException;
import java.time.DayOfWeek;

/**
 * An exception, thrown when no ports are free for connection.
 * 
 * @author Niv Kor
 */
public class PortsUnavailableException extends IOException
{
	private static final long serialVersionUID = -5366634765913032084L;
	private static final String MESSAGE = "There are no ports available";
	
	public PortsUnavailableException() {
		super(MESSAGE);
	}
	
	public static void main(String[] args) {
		System.out.println(DayOfWeek.FRIDAY.getValue());
	}
}
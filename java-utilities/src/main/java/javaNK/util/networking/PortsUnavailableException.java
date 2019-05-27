package javaNK.util.networking;
import java.io.IOException;

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
}
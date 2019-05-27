package java.util.networking;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.math.RNG;

/**
 * This class finds a free port to connect to,
 * and is compatible with all operating systems.
 * 
 * @author Niv Kor
 */
public class PortGenerator
{
	private static final int MIN_PORT = 1026;
	private static final int MAX_PORT = (int) Character.MAX_VALUE;
	
	/**
	 * Generate an available port number, ready for connection.
	 * @return a free port number.
	 */
	public static int nextPort() {
		DatagramSocket testSocket;
		int port;
		
		//test ports until one manages to connect
		while (true) {
			try {
				port = RNG.generate(MIN_PORT, MAX_PORT);
				testSocket = new DatagramSocket(port);
				testSocket.close();
				break;
			}
			catch(SocketException e) {}
		}
		
		return port;
	}
}
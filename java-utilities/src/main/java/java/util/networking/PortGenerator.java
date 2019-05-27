package java.util.networking;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.math.RNG;

/**
 * This class finds a free port to connect to,
 * and is compatible with all operating systems.
 * 
 * @author Niv Kor
 */
public class PortGenerator
{
	private static final int MIN_PORT = 1024;
	private static final int MAX_PORT = (int) Character.MAX_VALUE;
	
	private static Map<String, Integer> allocatedPorts = new HashMap<String, Integer>();
	
	/**
	 * Generate an available port number, ready for connection.
	 * @return a free port number.
	 */
	public static int nextPort() throws PortsUnavailableException {
		DatagramSocket testSocket;
		int port = 0;
		int portsAmount = (MAX_PORT - MIN_PORT) + allocatedPorts.size();
		Set<Integer> failedPorts = new HashSet<Integer>();
		
		//test ports until one manages to connect
		while (true) {
			try {
				do port = RNG.generate(MIN_PORT, MAX_PORT);
				while(failedPorts.contains(port) || allocatedPorts.containsValue(port));
				
				testSocket = new DatagramSocket(port);
				testSocket.close();
				break;
			}
			catch(SocketException e) {
				failedPorts.add(port);
				
				if (failedPorts.size() < portsAmount) continue;
				else throw new PortsUnavailableException();
			}
		}
		
		return port;
	}
	
	/**
	 * Allocate a port number for later use,
	 * so when generating a free port number, it wont be any of the allocated ones.
	 * 
	 * @param name - The name of the allocated port
	 * @param port - port number
	 */
	public static void allocate(String name, int port) {
		allocatedPorts.put(name, port);
	}
	
	/**
	 * @param name - The name of the pre-allocated port
	 * @return the allocated port.
	 */
	public static int getAllocated(String name) {
		return allocatedPorts.get(name);
	}
}
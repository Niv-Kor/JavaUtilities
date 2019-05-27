package javaNK.util.networking;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javaNK.util.math.Range;

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
	private static final Range<Integer> LEGAL_RANGE = new Range<Integer>(MIN_PORT, MAX_PORT);
	
	private static Map<String, Integer> allocatedPorts = new HashMap<String, Integer>();
	
	/**
	 * Generate an available port number, ready for connection.
	 * @return a free port number.
	 */
	public static int nextPort() throws PortsUnavailableException {
		int port = 0;
		int portsAmount = (MAX_PORT - MIN_PORT) + allocatedPorts.size();
		Set<Integer> failedPorts = new HashSet<Integer>();
		
		//test ports until one manages to connect
		while (true) {
			do port = (int) LEGAL_RANGE.generate();
			while(failedPorts.contains(port) || allocatedPorts.containsValue(port));
			
			if (test(port)) return port;
			else {
				failedPorts.add(port);
				
				if (failedPorts.size() < portsAmount) continue;
				else throw new PortsUnavailableException();
			}
		}
	}
	
	/**
	 * Test a connection to a port.
	 * @param port - The port to test
	 * @return true if the connection was successful.
	 */
	public static boolean test(int port) {
		DatagramSocket testSocket;
		
		try {
			testSocket = new DatagramSocket(port);
			testSocket.close();
			return true;
		}
		catch(SocketException e) { return false; }
	}
	
	/**
	 * Allocate a port number for later use,
	 * so when generating a free port number, it wont be any of the allocated ones.
	 * 
	 * @param name - The name of the allocated port
	 * @param port - port number
	 * @return true if the allocation was successful.
	 */
	public static boolean allocate(String name, int port) {
		if (LEGAL_RANGE.intersects(port) && test(port)) {
			allocatedPorts.put(name, port);
			return true;
		}
		else return false;
	}
	
	/**
	 * @param name - The name of the pre-allocated port
	 * @return the allocated port.
	 */
	public static int getAllocated(String name) {
		return allocatedPorts.get(name);
	}
}
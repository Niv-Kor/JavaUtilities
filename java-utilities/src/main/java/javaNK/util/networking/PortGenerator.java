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
	private static final int MIN_PORT = (int) Math.pow(2, 10);
	private static final int MAX_PORT = (int) Math.pow(2, 16);
	private static final Range<Integer> LEGAL_RANGE = new Range<Integer>(MIN_PORT, MAX_PORT);
	
	private static Map<String, Integer> allocatedPorts = new HashMap<String, Integer>();
	
	/**
	 * Generate an available port number, ready for connection.
	 * 
	 * @return a free port number.
	 */
	public static int nextPort() throws PortsUnavailableException {
		int port = 0;
		int portsAmount = (MAX_PORT - MIN_PORT) + allocatedPorts.size();
		Set<Integer> failedPorts = new HashSet<Integer>();
		
		//test ports until one manages to connect
		while (true) {
			do port = (int) LEGAL_RANGE.generate();
			while (failedPorts.contains(port) || allocatedPorts.containsValue(port));
			
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
	 * 
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
		catch (SocketException ex) { return false; }
	}
	
	/**
	 * Check if a port is legal (normally between 2^10 and 2^16).
	 * An allocated port will return an illegal result.
	 * 
	 * @param port - The port to check
	 * @return true if the port is legal for use.
	 */
	public static boolean isLegal(int port) {
		return LEGAL_RANGE.intersects(port);
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
		if (isLegal(port)) {
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
	
	/**
	 * Check if a port is already allocated.
	 * 
	 * @param port - The port to check
	 * @return true if the port is allocated.
	 */
	public static boolean isAllocated(int port) {
		return allocatedPorts.containsValue(port);
	}
	
	/**
	 * Free an allocated port.
	 * 
	 * @param name - The port to deallocate
	 */
	public static void deallocate(String name) {
		allocatedPorts.remove(name);
	}
	
	/**
	 * Free all allocated ports.
	 */
	public static void deallocateAll() {
		allocatedPorts.clear();
	}
}
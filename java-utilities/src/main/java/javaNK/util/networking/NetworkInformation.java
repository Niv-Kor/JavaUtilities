package javaNK.util.networking;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class stores information about a network, that's crucial for communication
 * between two machines. The network does not have to be the Internet network,
 * but could be rather any other private network as well. 
 * 
 * @author Niv Kor
 */
public class NetworkInformation
{
	private int localPort;
	private InetAddress address;
	
	/**
	 * @throws IOException when the local host address is unavailable.
	 */
	public NetworkInformation() throws IOException {
		this(PortGenerator.nextPort(), InetAddress.getLocalHost());
	}
	
	/**
	 * @param port - Port number
	 * @throws IOException when the local host address is unavailable.
	 */
	public NetworkInformation(int port) throws IOException {
		this(port, InetAddress.getLocalHost());
	}
	
	/**
	 * @param ip - IP address (such as '127.0.0.1')
	 * @throws IOException when the address is unavailable.
	 */
	public NetworkInformation(String ip) throws IOException {
		this(-1, ip);
	}
	
	/**
	 * @param address - Logical address
	 * @throws IOException when the address is unavailable.
	 */
	public NetworkInformation(InetAddress address) throws IOException {
		this(-1, address);
	}
	
	/**
	 * Construct a NetworkInformation object using a JSON message of type "network_information"
	 * that other classes in the package "networking" use.
	 * 
	 * @param msg - The "network_information" JSON message
	 * @throws IOException when the address is unavailable.
	 */
	public NetworkInformation(JSON msg) throws IOException {
		this(msg.getInt("port"), msg.getString("ip"));
	}
	
	/**
	 * @param port - Port number
	 * @param ip - IP address (such as '127.0.0.1')
	 * @throws IOException when the address is unavailable.
	 */
	public NetworkInformation(int port, String ip) throws IOException {
		this(port, InetAddress.getByName(ip));
	}
	
	/**
	 * @param port - Port number
	 * @param ip - IP address (such as '127.0.0.1')
	 * @throws IOException when the address is unavailable.
	 */
	public NetworkInformation(int port, InetAddress address) throws IOException {
		if (address == null) throw new IOException();
		
		this.address = address;
		this.localPort = (PortGenerator.isLegal(port)) ? port : PortGenerator.nextPort();
	}
	
	/**
	 * @return the local port.
	 */
	public int getLocalPort() { return localPort; }
	
	/**
	 * @return the IP address.
	 */
	public InetAddress getAddress() { return address; }
	
	/**
	 * @return the IP address' name.
	 */
	public String getIP() {
		String addressStr = address.toString();
		return addressStr.substring(addressStr.indexOf('/') + 1, addressStr.length());
	}
	
	/**
	 * @param p - The new port to use
	 * @throws SocketException when a problem occurs when trying to bind.
	 */
	public void setLocalPort(int port) {
		try { localPort = (PortGenerator.isLegal(port)) ? port : PortGenerator.nextPort(); }
		catch (PortsUnavailableException e) {}
	}
	
	/**
	 * @param a - The new address
	 */
	public void setAddress(InetAddress a) { address = a; }
	
	/**
	 * @param ip - The name of the new address
	 */
	public void setAddress(String ip) {
		try { address = InetAddress.getByName(ip); }
		catch (UnknownHostException e) {}
	}
	
	/**
	 * Compose a JSON message of type "network_information" that contains
	 * all the information needed about the local network.
	 * 
	 * @return a JSON message with the local networkInformation.
	 */
	public JSON composeJSON() {
		JSON networkInfoMsg = new JSON("network_information");
		networkInfoMsg.put("ip", getIP());
		networkInfoMsg.put("port", localPort);
		
		return networkInfoMsg;
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			NetworkInformation otherNetwork = (NetworkInformation) other;
			return localPort == otherNetwork.localPort && getIP().equals(otherNetwork.getIP());
		}
		catch (ClassCastException ex) { return false; }
	}
	
	@Override
	public String toString() {
		return "[IP Address: " + getIP() + ", Port: " + localPort + "]";
	}
}
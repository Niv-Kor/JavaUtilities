package javaNK.util.networking;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkInformation
{
	private int localPort;
	private InetAddress address;
	
	public NetworkInformation() throws IOException {
		this(PortGenerator.nextPort(), InetAddress.getLocalHost());
	}
	
	public NetworkInformation(Integer port) throws IOException {
		this(port, InetAddress.getLocalHost());
	}
	
	public NetworkInformation(String ip) throws IOException {
		this(-1, ip);
	}
	
	public NetworkInformation(InetAddress address) throws IOException {
		this(-1, address);
	}
	
	public NetworkInformation(Integer port, String ip) throws IOException {
		this(port, InetAddress.getByName(ip));
	}
	
	public NetworkInformation(JSON msg) throws IOException {
		this(msg.getInt("port"), msg.getString("ip"));
	}
	
	public NetworkInformation(Integer port, InetAddress address) throws IOException {
		this.address = address;
		this.localPort = (PortGenerator.isLegal(port)) ? port : PortGenerator.nextPort();
	}
	
	public int getLocalPort() { return localPort; }
	
	public InetAddress getAddress() { return address; }
	
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
	
	public void setAddress(InetAddress a) { address = a; }
	
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
		return "[Network IP: " + getIP() + ", Port: " + localPort + "]";
	}
}
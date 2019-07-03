package javaNK.util.networking;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkInformation
{
	private int localPort;
	private InetAddress address;
	
	public NetworkInformation() throws UnknownHostException, PortsUnavailableException {
		this(PortGenerator.nextPort(), InetAddress.getLocalHost());
	}
	
	public NetworkInformation(Integer port) throws UnknownHostException, PortsUnavailableException {
		this(port, InetAddress.getLocalHost());
	}
	
	public NetworkInformation(String ip) throws UnknownHostException, PortsUnavailableException {
		this(-1, ip);
	}
	
	public NetworkInformation(InetAddress address) throws UnknownHostException, PortsUnavailableException {
		this(-1, address);
	}
	
	public NetworkInformation(Integer port, String ip) throws UnknownHostException, PortsUnavailableException {
		this(port, InetAddress.getByName(ip));
	}
	
	public NetworkInformation(Integer port, InetAddress address) throws UnknownHostException, PortsUnavailableException {
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
}
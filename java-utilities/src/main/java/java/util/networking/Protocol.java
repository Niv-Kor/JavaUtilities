package java.util.networking;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.networking.JSON;

/**
 * A TCP protocol used for communicating with a server.
 * This class sends and receives JSON objects as messages.
 * 
 * @see java.util.networking.JSON
 * @author Niv Kor
 */
public class Protocol
{
	/**
	 * This class creates a mini-protocol that's seemlessly sending acks to the main protocol
	 * over and over again, in order to prevent its starvation.
	 * It assures that the main protocol will never wait an infinite amount of time for a single message,
	 * by regularly sending it meaningless acks that will be handled and thrown away. 
	 * 
	 * @author Niv Kor
	 */
	private class Waker extends Protocol
	{
		private static final double ACK_REPEAT = 0.2;
		
		private Timer timer;
		private TimerTask task;
		private boolean started;
		private JSON wakeUpMessage;
		
		/**
		 * @see constructor ServerProtocol(boolean initWaker)
		 * @param tiredProt - The main protocol to regularly wake
		 */
		public Waker(Protocol tiredProt) throws IOException {
			super(false);
			
			this.wakeUpMessage = new JSON("wakeup");
			
			//retrieve the tired protocol's port as the target port
			this.target = tiredProt.getPort();			
			
			this.timer = new Timer();
			this.task = new TimerTask() {
				@Override
				public void run() {
					try { send(wakeUpMessage); }
					catch(IOException e) { e.printStackTrace(); }
				}
			};
		}
		
		/**
		 * Start sending signals to the main protocol.
		 */
		public void start() {
			if (started) return;
			
			timer.schedule(task, 0, (int) (ACK_REPEAT * 1000));
			started = true;
		}
	}
	
	protected static List<HashMap<String, JSON>> buffers = new ArrayList<HashMap<String, JSON>>();
	protected InetAddress serverAddress;
	protected DatagramSocket socket;
	protected Integer port, target;
	protected Waker waker;
	
	/**
	 * @param initWaker - True to initialize a wake protocol that will prevent this protocol's starvation.
	 * 					  For a safe functioning of the protocol, 'true' is highly recommended.
	 * 
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public Protocol(boolean initWaker) throws IOException {
		this.serverAddress = InetAddress.getLocalHost();
		this.port = PortGenerator.nextPort();
		connect();
		
		if (initWaker) {
			Waker waker = new Waker(this);
			waker.start();
		}
	}
	
	/**
	 * @param port - The port this protocol will use
	 * @param targetPort - The port this protocol will communicate with
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public Protocol(Integer port, Integer targetPort) throws IOException {
		this(true);
		this.target = targetPort;
		disconnect();
		this.port = port;
		connect();
	}
	
	/**
	 * Create a socket and connect to the host.
	 * 
	 * @throws SocketException when the socket is already binded.
	 */
	protected void connect() throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	/**
	 * Close the socket.
	 */
	protected void disconnect() {
		socket.close();
	}
	
	/**
	 * Send a JSON message to the target port.
	 * 
	 * @param msg - The message to send
	 * @throws IOException when the target port is unavailable for sending messages to.
	 */
	public void send(JSON msg) throws IOException {
		byte[] data = msg.toString().getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, target);
		socket.send(packet);
	}
	
	/**
	 * Receive a JSON message from the target port.
	 * This is a dangerous method and can cause starvation if not handled carefully.
	 * Initiating a waker is recommended for such cases.
	 * 
	 * @return an array of the message parts, where the first part is the request string.
	 * @throws IOException when the target port is unavailable for receiving messages from.
	 */
	public JSON receive() throws IOException {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		String message = new String(packet.getData(), 0, packet.getLength());
		return new JSON(message);
	}
	
	/**
	 * Wait for a specific packet from the target port.
	 * In this method, whenever receiving a message that's incompatible with the request,
	 * it's not thrown away, but rather waiting in a buffer where it can be found later.
	 * If any of the requests get a compatible answer, that's enough to exit the method. 
	 * 
	 * @param keys - Array of all the requests from the target port
	 * @return any of the requests sent (whichever message is recieved first).
	 * @throws IOException when the target port is unavailable for receiving messages from.
	 */
	public JSON waitFor(String[] keys) throws IOException {
		//open local buffer
		HashMap<String, JSON> localBuffer = new HashMap<String, JSON>();
		buffers.add(localBuffer);
		return waitFor(keys, localBuffer);
	}
	
	/**
	 * @see waitFor(String[] keys)
	 * @param localBuffer - Sent to this private overloading method by the public version
	 */
	protected JSON waitFor(String[] keys, HashMap<String, JSON> localBuffer) throws IOException {
		JSON answer = receive();
		
		//received an answer - check if it's compatible with one of the requests
		for (String key : keys) {
			//the answer is compatible - return it
			if (answer.getType().equals(key)) {
				
				//spread the local buffer's content to all other buffers and delete it
				for (HashMap<String, JSON> otherBuffer : buffers)
					if (otherBuffer != localBuffer) otherBuffer.putAll(localBuffer);
				
				buffers.remove(localBuffer);
				return answer;
			}
			
			//the answer is useless - spread it to all other buffers
			for (HashMap<String, JSON> otherBuffer : buffers)
				if (otherBuffer != localBuffer && !otherBuffer.containsKey(answer.getType()))
					otherBuffer.put(answer.getType(), answer);
		}
		
		//check if local buffer has the compatible answer
		for (String pre : keys)
			if (localBuffer.containsKey(pre))
				return localBuffer.get(pre);
		
		return waitFor(keys, localBuffer);
	}
	
	/**
	 * Send a message to the target port and wait until a compatible answer is received.
	 * As long as the answer is not received, nag the target port and send it the request over and over again.
	 *  
	 * @param msg - The request from the target port
	 * @return an answer for the request.
	 * @throws IOException when the target port is unavailable.
	 */
	public JSON request(JSON msg) throws IOException {
		//open local buffer
		HashMap<String, JSON> localBuffer = new HashMap<String, JSON>();
		buffers.add(localBuffer);
		
		return stubbornRequest(msg, localBuffer);
	}
	
	/**
	 * @see request(String msg)
	 * @param localBuffer - Sent to this private overloading method by the public version
	 */
	protected JSON stubbornRequest(JSON responseTo, HashMap<String, JSON> localBuffer) throws IOException {
		String expectedType = responseTo.getType();
		
		send(responseTo);
		JSON answer = receive();
		
		//check that the answer is compatible with the request
		if (answer.getType().equals(expectedType)) {
			//spread the local buffer's content to all other buffers and delete it
			for (HashMap<String, JSON> otherBuffer : buffers)
				if (otherBuffer != localBuffer) otherBuffer.putAll(localBuffer);
			
			buffers.remove(localBuffer);
			return answer;
		}
		else {
			//the answer is useless - spread it to all other buffers
			for (HashMap<String, JSON> otherBuffer : buffers)
				if (otherBuffer != localBuffer && !otherBuffer.containsKey(answer.getType()))
					otherBuffer.put(answer.getType(), answer);
			
			//check if local buffer has the compatible answer
			if (localBuffer.containsKey(expectedType))
				return localBuffer.get(expectedType);
			
			//try again
			return stubbornRequest(responseTo, localBuffer);
		}
	}
	
	/**
	 * @return the port this protocol uses.
	 */
	public int getPort() { return port; }
	
	/**
	 * @return the target port this protocol communicates with.
	 */
	public int getTargetPort() { return target; }
}
package javaNK.util.networking;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import javaNK.util.debugging.Logger;
import javaNK.util.threads.ThreadUtility;

/**
 * A UDP protocol used for communicating with a server.
 * This class is thread-safe, and can be used by many threads at the same time.
 * It only sends and receives messages in the form of JSON object (also under this package). 
 * 
 * @see javaNK.util.networking.JSON
 * @author Niv Kor
 */
public class Protocol
{
	/**
	 * This class contains a request, composed by any thread that uses the protocol.
	 * When a thread calls the waitFor() or request() methods, it creates a request,
	 * containing the thread's ID, and a set of requests (message type keys).
	 * 
	 * @author Niv Kor
	 */
	private static class ThreadRequest
	{
		private long threadID;
		private Set<String> keys;
		private Queue<Entry<String, JSON>> answers;
		
		/**
		 * @param threadID - The ID of the thread that made the request
		 */
		public ThreadRequest(long threadID) {
			this.threadID = threadID;
			this.keys = new HashSet<String>();
			this.answers = new LinkedList<Entry<String, JSON>>();
		}
		
		/**
		 * @return the requesting thread's ID
		 */
		public long getThreadID() { return threadID; }
		
		/**
		 * @return a set of keys the thread requested
		 */
		public Set<String> getKeys() { return keys; }
		
		/**
		 * Add a key to the request.
		 * 
		 * @param key - The key to add
		 */
		public void addKey(String key) {
			if (!keys.contains(key)) keys.add(key);
		}
		
		/**
		 * Add a received answer to the thread's request
		 * 
		 * @param key - The answer message's type
		 * @param msg - The answer message itself
		 */
		public void putAnswer(String key, JSON msg) {
			if (!keys.contains(key)) return;
			else answers.add(new SimpleEntry<String, JSON>(key, msg));
		}
		
		/**
		 * Retrieve an answer for the request.
		 * 
		 * @return the answer, as it was received, or null if the answer is yet to arrive.
		 */
		public JSON retAnswer() {
			try {
				for (Entry<String, JSON> entry : answers)
					if (keys.contains(entry.getKey()))
						return entry.getValue();
			}
			catch(ConcurrentModificationException e) {}
			
			return null;
		}
		
		@Override
		public String toString() {
			return "ID: " + threadID + " " + answers.toString();
		}
	}
	
	/**
	 * This class is the exclusive component in Protocol that's responsible for receiving messages.
	 * Every message that's received here is immediately transfered to a PackageSorter object.
	 * This class runs on a separate thread, in order to achieve better performance.
	 * 
	 * @author Niv Kor
	 */
	private static class PackageProcessor implements Runnable
	{
		private PackageSorter sorter;
		private Protocol protocol;
		
		/**
		 * @param protocol - The main Protocol object
		 * @param sorter - The PackageSorter object that Protocol uses
		 */
		public PackageProcessor(Protocol protocol, PackageSorter sorter) {
			this.protocol = protocol;
			this.sorter = sorter;
		}
		
		@Override
		public void run() {
			while (true) {
				try { sorter.sortPackage(protocol.receive()); }
				catch(IOException e) {
					Logger.error("Processor error");
					e.printStackTrace();
					continue;
				}
				
				ThreadUtility.delay(8);
			}
		}
	}
	
	/**
	 * This class gets a message that was received to the protocol,
	 * and navigates it to the exact same thread that asked for it.
	 * 
	 * @author Niv Kor
	 */
	private static class PackageSorter implements Runnable
	{
		private Queue<JSON> messages;
		private Set<ThreadRequest> requests;
		
		/**
		 * @param protocol - The main protocol object
		 */
		public PackageSorter(Set<ThreadRequest> requests) {
			this.requests = requests;
			this.messages = new LinkedList<JSON>();
		}

		@Override
		public void run() {
			while (true) {
				if (!messages.isEmpty()) {
					JSON msg = messages.poll();
					
					//iterate over all thread requests
					for (ThreadRequest threadReq : requests)
						if (threadReq.getKeys().contains(msg.getType()))
							threadReq.putAnswer(msg.getType(), msg);
				}
				
				ThreadUtility.delay(8);
			}
		}
		
		/**
		 * Add a package that needs to be sorted to the packages queue.
		 * 
		 * @param message - The message to sort
		 */
		public void sortPackage(JSON message) {
			messages.add(message);
		}
	}
		
	protected volatile PackageSorter sorter;
	protected volatile PackageProcessor processor;
	protected volatile Set<ThreadRequest> requestBuffer;
	protected volatile Queue<Long> requestingOrder;
	protected volatile InetAddress serverAddress;
	protected volatile DatagramSocket socket;
	protected volatile Integer port, target;
	
	/**
	 * @throws IOException when connection cannot not be established.
	 */
	public Protocol() throws IOException {
		init(null, null);
	}
	
	/**
	 * @param port - The port this protocol will listen to
	 * @throws IOException when connection cannot not be established.
	 */
	public Protocol(Integer port) throws IOException {
		init(port, null);
	}
	
	/**
	 * @param port - The port this protocol will listen to
	 * @param targetPort - The port this protocol will communicate with
	 * @throws IOException when connection cannot not be established.
	 */
	public Protocol(Integer port, Integer targetPort) throws IOException {
		init(port, targetPort);
	}
	
	/**
	 * Initiate class members
	 * 
	 * @throws IOException
	 */
	protected void init(Integer port, Integer target) throws IOException {
		//ports
		this.serverAddress = InetAddress.getLocalHost();
		
		try { this.port = (port != null) ? port : PortGenerator.nextPort(); }
		catch(PortsUnavailableException e) { throw new IOException(); }
		
		this.target = (target != null) ? target : 0;
		
		connect();
		
		//threads requests buffer and order
		this.requestBuffer = new HashSet<ThreadRequest>();
		this.requestingOrder = new LinkedList<Long>();
		
		//package sorter
		this.sorter = new PackageSorter(requestBuffer);
		new Thread(sorter).start();
		
		//package processor
		this.processor = new PackageProcessor(this, sorter);
		new Thread(processor).start();
	}
	
	/**
	 * Wait for a specific packet from the target port.
	 * This method is blocked and will not return a value just until a compatible answer arrives. 
	 * If any of the requests get a compatible answer, that's enough for the method to exit and return it.
	 * 
	 * @param keys - Array of all the requests from the target port
	 * @return any of the requests sent (whichever message is received first).
	 * @throws IOException when the target port is unavailable for receiving messages from.
	 */
	public JSON waitFor(String[] keys) throws IOException {
		long threadID = Thread.currentThread().getId();
		openRequest(keys, threadID);
		return waitFor(threadID, false);
	}
	
	/**
	 * @see waitFor(String[] keys)
	 * @param threadID - The ID of the requesting thread
	 * @param ordered - True if the order of request answering is important.
	 * 					The ordered requests will be answered in the order they were created.
	 */
	protected JSON waitFor(long threadID, boolean ordered) throws IOException {
		ThreadRequest threadReq = getThread(threadID);
		JSON answer;
		
		while (true) {
			answer = threadReq.retAnswer();
			
			//found the answer
			if (answer != null) {
				//request is ordered - wait to its turn
				while (ordered) {
					if (requestingOrder.peek() == threadID) {
						requestingOrder.remove(threadID);
						requestBuffer.remove(threadReq);
						return answer;
					}
					else ThreadUtility.delay(20);
				}
				
				//request is not ordered - return it
				requestBuffer.remove(threadReq);
				return answer;
			}
			else ThreadUtility.delay(20);
		}
	}
	
	/**
	 * Send a message to the target port and wait until a compatible answer is received.
	 * Both the request and the answer messaged MUST be of the same type.
	 *  
	 * @param msg - The request to the target port
	 * @return an answer for the request.
	 * @throws IOException when the target port is unavailable.
	 */
	public JSON request(JSON msg) throws IOException {
		send(msg);
		
		long threadID = Thread.currentThread().getId();
		requestingOrder.add(threadID);
		String[] keys = { msg.getType() };
		openRequest(keys, threadID);
		return waitFor(threadID, true);
	}
	
	/**
	 * Open a new request for a thread.
	 * 
	 * @param req - The array of keys to request
	 * @param threadID - The thread's ID
	 */
	public void openRequest(String[] req, long threadID) {
		ThreadRequest threadReq = getThread(threadID);
		
		//if this thread had never made a request before, create an entry for it
		if (threadReq == null) {
			requestBuffer.add(new ThreadRequest(threadID));
			threadReq = getThread(threadID);
		}
		
		//insert new requests
		for (String key : req) threadReq.addKey(key);
	}
	
	private ThreadRequest getThread(long threadID) {
		try {
			for (ThreadRequest req : requestBuffer)
				if (req.getThreadID() == threadID) return req;
		}
		//multithreading problem, try again
		catch(ConcurrentModificationException e) { return getThread(threadID); }
		
		return null;
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
	 * @return the port this protocol uses.
	 */
	public int getPort() { return port; }
	
	/**
	 * @return the target port this protocol communicates with.
	 */
	public int getTargetPort() { return target; }
	
	/**
	 * @param p - The new port to use
	 * @throws SocketException 
	 */
	public void setPort(int p) throws SocketException {
		disconnect();
		port = p;
		connect();
	}
	
	/**
	 * Generate a port randomly.
	 * 
	 * @throws PortsUnavailableException when no port is available.
	 * @throws SocketException 
	 */
	public void setPort() throws PortsUnavailableException, SocketException {
		setPort(PortGenerator.nextPort());
	}
	
	/**
	 * @param p - The new target port to communicate with
	 */
	public void setTargetPort(int p) { target = p; }
}
package javaNK.util.networking;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javaNK.util.debugging.Logger;
import javaNK.util.threads.DiligentThread;
import javaNK.util.threads.ThreadUtility;

/**
 * A UDP protocol used for communicating with a server.
 * This class is thread-safe, and can be used by many threads at the same time.
 * It only sends and receives messages in the form of a JSON object (also under this package).
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
		private long threadID, timeout;
		private Set<String> keys;
		private Queue<Entry<String, JSON>> answers;
		private boolean timeoutRelevance;
		
		/**
		 * @param threadID - The ID of the thread that made the request
		 * @param timeoutSec - Amount of time until timeout
		 */
		public ThreadRequest(long threadID, double timeoutSec) {
			this.threadID = threadID;
			this.keys = new HashSet<String>();
			this.answers = new LinkedList<Entry<String, JSON>>();
			
			this.timeout = (long) (timeoutSec * 1000);
			this.timeoutRelevance = timeoutSec > 0;
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
		 * Add a received answer to the thread's request.
		 * If there's already an answer in the queue, poll it out of the queue and return it.
		 * 
		 * @param key - The answer message's type
		 * @param msg - The answer message itself
		 * @return a message that was already in the queue before adding a new one
		 */
		public JSON putAnswer(String key, JSON msg) {
			JSON returnValue = null;
			
			if (!keys.contains(key)) return null;
			else {
				if (!answers.isEmpty()) returnValue = answers.poll().getValue();
				answers.add(new SimpleEntry<String, JSON>(key, msg));
				return returnValue;
			}
		}
		
		/**
		 * Retrieve an answer for the request.
		 * 
		 * @return the answer, as it was received, or null if the answer is yet to arrive.
		 * @throws TimeoutException when the request has reached the predefined timeout.
		 */
		public JSON retAnswer() throws TimeoutException {
			try {
				for (Entry<String, JSON> entry : answers)
					if (keys.contains(entry.getKey()))
						return entry.getValue();
			}
			//data structure modification errors that might happen
			catch (ConcurrentModificationException | NullPointerException e) {}
			
			//count down to timeout
			if (timeoutRelevance) {
				timeout -= STUBBORN_MILLISEC_PULSE;
				if (timeout <= 0) throw new TimeoutException();
			}
			return null;
		}
		
		@Override
		public String toString() {
			return "[Thread ID: " + threadID + ", Received answers: " + answers.toString() + "]";
		}
	}
	
	/**
	 * This class is the exclusive component in Protocol that's responsible for receiving messages.
	 * Every message that's received here is immediately transfered to a PackageSorter object.
	 * This class runs on a separate thread, in order to achieve better performance.
	 * 
	 * @author Niv Kor
	 */
	private static class PackageProcessor extends DiligentThread
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
		protected void diligentFunction() throws Exception {
			try { sorter.sortPackage(protocol.receive()); }
			catch (IOException e) {
				if (running) { //real problem
					Logger.error("Processor error");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * This class gets a message that was received to the protocol,
	 * and navigates it to the exact same thread that asked for it.
	 * 
	 * @author Niv Kor
	 */
	private static class PackageSorter extends DiligentThread
	{
		private static class Archiver extends DiligentThread
		{
			private PackageSorter sorter;
			private Queue<JSON> missedMessages;
			
			public Archiver(PackageSorter sorter, double pulse) {
				super(pulse);
				this.missedMessages = new LinkedList<JSON>();
				this.sorter = sorter;
			}
			
			@Override
			protected void diligentFunction() throws Exception {
				//iterate over all missed messages and try to sort them again
				for (JSON missedMsg : missedMessages)
					sorter.sort(missedMsg);
				
				missedMessages.clear();
				pause(true);
			}
			
			/**
			 * Archive a message and try to send it again later.
			 * 
			 * @param msg - The message to archive
			 */
			public void archive(JSON msg) {
				missedMessages.add(msg);
				pause(false);
			}
		}
		
		private Archiver archiver;
		private Queue<JSON> messages;
		private Set<ThreadRequest> requests;
		private Protocol protocol;
		
		/**
		 * @param protocol - The main protocol object
		 * @param requests - Set of thread requests from protocol
		 */
		public PackageSorter(Protocol protocol, Set<ThreadRequest> requests) {
			super(0.008);
			this.protocol = protocol;
			this.requests = requests;
			this.messages = new LinkedList<JSON>();
			this.archiver = new Archiver(this, 2.5);
		}

		/**
		 * Add a package that needs to be sorted to the packages queue.
		 * 
		 * @param message - The message to sort
		 */
		public void sortPackage(JSON message) { messages.add(message); }
		
		@Override
		protected void diligentFunction() throws Exception {
			if (!messages.isEmpty()) {
				try {
					JSON msg = messages.poll();
					if (!sort(msg)) archiver.archive(msg);
				}
				catch (IOException e) {
					if (running) { //real problem
						Logger.error("Sorter error");
						e.printStackTrace();
					}
				}
			}
		}
		
		private boolean sort(JSON msg) throws IOException {
			boolean sorted = false;
			
			//received an ack that checks if this port is alive
			if (msg.getType().equals(ALIVE_ACK_TYPE + "ASK")) {
				JSON livingAnswer = new JSON(ALIVE_ACK_TYPE + "RESP");
				livingAnswer.put("alive", true);
				sorted = true;
				protocol.send(livingAnswer);
			}
			else {
				//iterate over all thread requests
				for (ThreadRequest threadReq : requests) {
					if (threadReq.getKeys().contains(msg.getType())) {
						JSON overridenMessage = threadReq.putAnswer(msg.getType(), msg);
						
						if (overridenMessage != null)
							archiver.archive(overridenMessage);
						
						sorted = true;
					}
				}
			}
			
			return sorted;
		}
		
		/**
		 * Clear messages queue.
		 */
		public void flush() { messages.clear(); }
		

		@Override
		public void start() {
			super.start();
			archiver.start();
		}
		
		@Override
		public void pause(boolean flag) {
			super.pause(flag);
			archiver.pause(flag);
		}
		
		public void kill() {
			super.kill();
			archiver.kill();
		}
	}
	
	private static final int STUBBORN_MILLISEC_PULSE = 20;
	private static final double LIVING_ACK_TIMEOUT_SEC = 10;
	private static final String SYSTEM_CALL = "SYSTEM-CALL->";
	private static final String ALIVE_ACK_TYPE = SYSTEM_CALL + "LIVING ACK->";
	
	protected volatile PackageSorter sorter;
	protected volatile PackageProcessor processor;
	protected volatile Set<ThreadRequest> requestBuffer;
	protected volatile Queue<Long> requestingOrder;
	protected volatile InetAddress serverAddress;
	protected volatile DatagramSocket socket;
	protected volatile Integer localPort, remotePort;
	protected volatile boolean dead;
	
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
		
		try { this.localPort = (port != null) ? port : PortGenerator.nextPort(); }
		catch (PortsUnavailableException e) { throw new IOException(); }
		
		this.remotePort = (target != null) ? target : 0;
		
		//threads requests buffer and order
		this.requestBuffer = new HashSet<ThreadRequest>();
		this.requestingOrder = new LinkedList<Long>();
		
		//package sorter
		this.sorter = new PackageSorter(this, requestBuffer);
		new Thread(sorter).start();
		
		//package processor
		this.processor = new PackageProcessor(this, sorter);
		new Thread(processor).start();
		
		connect();
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
		openRequest(keys, threadID, 0);
		
		try { return waitFor(threadID, false); }
		catch (TimeoutException e) { return null; } //should not occur because timeout is set to 0
	}
	
	/**
	 * @see waitFor(String[] keys)
	 * @param threadID - The ID of the requesting thread
	 * @param ordered - True if the order of request answering is important.
	 * 					The ordered requests will be answered in the order they were created.
	 */
	protected JSON waitFor(long threadID, boolean ordered) throws IOException, TimeoutException {
		ThreadRequest threadReq = getThread(threadID);
		JSON answer;
		
		while (!socket.isClosed()) {
			try { answer = threadReq.retAnswer(); }
			catch (TimeoutException e) {
				requestBuffer.remove(threadReq);
				throw new TimeoutException();
			}
			
			//found the answer
			if (answer != null) {
				//request is ordered - wait to its turn
				while (ordered && !socket.isClosed()) {
					if (requestingOrder.peek() == threadID) {
						requestingOrder.remove(threadID);
						requestBuffer.remove(threadReq);
						return answer;
					}
					else ThreadUtility.delay(STUBBORN_MILLISEC_PULSE);
				}
				
				//request is not ordered - return it
				requestBuffer.remove(threadReq);
				return answer;
			}
			else ThreadUtility.delay(STUBBORN_MILLISEC_PULSE);
		}
		
		//flushed
		return null;
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
		try { return request(msg, 0); }
		catch (TimeoutException e) { return null; } //should not occur because timeout is set to 0
	}
	
	public JSON request(JSON msg, double timeoutSec) throws IOException, TimeoutException {
		send(msg);
		
		long threadID = Thread.currentThread().getId();
		requestingOrder.add(threadID);
		String[] keys = { msg.getType() };
		openRequest(keys, threadID, timeoutSec);
		
		return waitFor(threadID, true);
	}
	
	/**
	 * Request an ack from the target port, acknowledging it's alive.
	 * 
	 * @return true if the target port is alive, or false if it's not responding
	 * @throws IOException if the socket is unavailable.
	 */
	public boolean requestLivingAck() throws IOException {
		return requestLivingAck(LIVING_ACK_TIMEOUT_SEC);
	}
	
	/**
	 * @see requestLivingAck()
	 * @param timeoutSec - The amount of time to wait (in seconds).
	 * 					   A timeout of 0 is interpreted as infinite.
	 */
	public boolean requestLivingAck(double timeoutSec) throws IOException {
		JSON sendAck = new JSON(ALIVE_ACK_TYPE + "ASK");
		send(sendAck);
		
		long threadID = Thread.currentThread().getId();
		String[] keys = { ALIVE_ACK_TYPE + "RESP" };
		openRequest(keys, threadID, timeoutSec);
		
		try {
			JSON receivedAck = waitFor(threadID, false);
			return receivedAck.getBoolean("alive");
		}
		catch (TimeoutException e) { return false; }
	}
	
	/**
	 * Open a new request for a thread.
	 * 
	 * @param req - The array of keys to request
	 * @param threadID - The thread's ID
	 * @param timeoutSec - Amount of time until timeout
	 */
	public void openRequest(String[] req, long threadID, double timeoutSec) {
		ThreadRequest threadReq = getThread(threadID);
		
		//if this thread had never made a request before, create an entry for it
		if (threadReq == null) {
			requestBuffer.add(new ThreadRequest(threadID, timeoutSec));
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
		catch (ConcurrentModificationException e) { return getThread(threadID); }
		
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
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, remotePort);
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
		byte[] data = new byte[2024];
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
		if (!dead) {
			socket = new DatagramSocket(localPort);
			processor.pause(false);
			sorter.pause(false);
		}
	}
	
	/**
	 * Break the receive block.
	 *  
	 * @throws SocketException when there are troubles reconnecting to the socket.
	 */
	protected void breakReceiveBlock() throws SocketException {
		
	}
	
	/**
	 * Close the protocol temporarily.
	 */
	protected void disconnect() {
		processor.pause(true);
		sorter.pause(true);
		socket.close();
	}
	
	/**
	 * Close the protocol permanently.
	 */
	public void close() {
		try { flush(); }
		catch (SocketException e) {}
		
		disconnect();
		processor.kill();
		sorter.kill();
		dead = true;
	}
	
	/**
	 * Clear all open requests.
	 * 
	 * @throws SocketException when something goes wrong with the reconnection.
	 */
	public void flush() throws SocketException {
		//shut down components
		processor.pause(true);
		sorter.pause(true);
		sorter.flush();
		
		//clear all buffers
		requestBuffer.clear();
		requestingOrder.clear();
		
		//reconnect
		String allocationStr = "protocl_" + toString() + "_" + LocalDateTime.now() + "_reconnection";
		PortGenerator.allocate(allocationStr, localPort);
		disconnect();
		connect();
		
		//turn components back on
		sorter.pause(false);
		processor.pause(false);
	}
	
	/**
	 * @return the port this protocol uses.
	 */
	public int getLocalPort() { return localPort; }
	
	/**
	 * @return the target port this protocol communicates with.
	 */
	public int getRemotePort() { return remotePort; }
	
	/**
	 * @param p - The new port to use
	 * @throws SocketException 
	 */
	public void setLocalPort(int p) throws SocketException {
		disconnect();
		localPort = p;
		connect();
	}
	
	/**
	 * Generate a port randomly.
	 * 
	 * @throws PortsUnavailableException when no port is available.
	 * @throws SocketException 
	 */
	public void setLocalPort() throws PortsUnavailableException, SocketException {
		setLocalPort(PortGenerator.nextPort());
	}
	
	/**
	 * @param p - The new target port to communicate with
	 */
	public void setRemotePort(int p) { remotePort = p; }
}
package javaNK.util.networking;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javaNK.util.debugging.Logger;

/**
 * This class collects all messages that are continuously received by a protocol,
 * and matches each message with a compatible, pre-defined algorithm.
 * That algorithm is then executed after the message's arrival.
 * 
 * @author Niv Kor
 */
public abstract class ResponseEngine implements Runnable
{
	/**
	 * This interface cooperates with ResponseEngine,
	 * in a way that it represents a case that should be responded.
	 * It provides the message type to respond to, and an algorithm that
	 * will be executed when the above message arrives.
	 *  
	 * @author Niv Kor
	 */
	public static interface ResponseCase
	{
		/**
		 * Get the type of the message this case responds to.
		 * It can only respond to one message type.
		 * 
		 * @return the type of the message to respond to.
		 */
		String getType();
		
		/**
		 * An algorithm to be excecuted when the correct message arrives.
		 * 
		 * @param msg - The message that triggered this method
		 * @throws Exception when something goes wrong with the algorithm.
		 */
		void respond(JSON msg) throws Exception;
	}
	
	/**
	 * This class is responsible for handling the messages that ResponseEngine collects.
	 * Whenever a message is sent to the executor, it searches the message's case
	 * and excecutes the algorithm that's definesd there.
	 * 
	 * @author Niv Kor
	 */
	protected static class ResponseExcecutor implements Runnable
	{
		protected volatile List<ResponseCase> services;
		protected volatile Queue<JSON> messages;
		
		/**
		 * @param messages - Queue of messages that had been received in ResponseEngine
		 * @param services - List of cases that were pre-defined in ResponseEngine's initCases()
		 */
		public ResponseExcecutor(Queue<JSON> messages, List<ResponseCase> services) {
			this.messages = messages;
			this.services = services;
		}
		
		@Override
		public void run() {
			while(true) {
				if (!messages.isEmpty()) {
					JSON msg = messages.poll();
					
					//check again, in case of a multithreading problem
					if (msg != null) {
						for (int i = 0; i < services.size(); i++) {
							ResponseCase service = services.get(i);
							
							if (msg.getType().equals(service.getType())) {
								try { service.respond(msg); }
								catch (Exception e) {}
							}
						}
					}
				}
				
				try { Thread.sleep(8); }
				catch(InterruptedException e) {}
			}
		}
	}
	
	protected Protocol protocol;
	protected ResponseExcecutor responder;
	protected List<String> caseKeys;
	protected volatile boolean running;
	protected volatile List<ResponseCase> services;
	protected volatile Queue<JSON> messages;
	
	/**
	 * Construct an object with a new protocol.
	 * 
	 * WARNING: this constructor creates a new protocol, which means that if a protocol
	 * with the argument port already exists, a BindException will be thrown.
	 * 
	 * @param port - The port to listen to
	 * @throws IOException when the protocol is unavailable.
	 */
	public ResponseEngine(int port) throws IOException {
		this(new Protocol(port));
	}
	
	/**
	 * Create an object with an existing protocol.
	 * 
	 * @param protocol - The protocol to use to receive messages
	 * @throws IOException when the protocol is unavailable.
	 */
	public ResponseEngine(Protocol protocol) throws IOException {
		this.protocol = protocol;
		this.caseKeys = new ArrayList<String>();
		this.services = new ArrayList<ResponseCase>();
		this.messages = new LinkedList<JSON>();
		this.responder = new ResponseExcecutor(messages, services);
		initCases();
		new Thread(responder).start();
	}
	
	@Override
	public void run() {
		String[] keyArr = caseKeys.toArray(new String[caseKeys.size()]);
		running = true;
		
		while (true) {
			while (running) {
				try { handle(protocol.waitFor(keyArr)); }
				catch(IOException e) {
					Logger.error("Server error");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Handle a message so the compatible algorithm can be executed.
	 * 
	 * @param msg - The message to send forward to the executor
	 */
	protected void handle(JSON msg) {
		messages.add(msg);
	}
	
	/**
	 * Add a response case that will be executed when a compatible message arrives.
	 * 
	 * @param resCase - The case to handle during the engine's run time
	 */
	protected void addCase(ResponseCase resCase) {
		services.add(resCase);
		caseKeys.add(resCase.getType());
	}
	
	/**
	 * Pause the engine.
	 * 
	 * @param flag - True to pause or false to resume
	 */
	public void pause(boolean flag) { running = !flag; }
	
	/**
	 * Initiate all of the engine's cases.
	 * Use at least one addCase(ResposeCase) in this method.
	 */
	protected abstract void initCases();
}
package javaNK.util.networking;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javaNK.util.debugging.Logger;
import javaNK.util.threads.DaemonThread;
import javaNK.util.threads.DiligentThread;

/**
 * This class collects all messages that are continuously received by a protocol,
 * and matches each message with a compatible, predefined algorithm.
 * That algorithm is then executed after the message's arrival.
 * 
 * @author Niv Kor
 */
public abstract class ResponseEngine extends DiligentThread
{
	/**
	 * This class is responsible for handling the messages that ResponseEngine collects.
	 * Whenever a message is sent to the executor, it searches the message's case
	 * and executes the algorithm that's defined there.
	 * 
	 * @author Niv Kor
	 */
	protected static class ResponseExcecutor extends DaemonThread<JSON>
	{
		protected volatile List<ResponseCase> services;
		protected volatile boolean running;
		
		/**
		 * @param messages - Queue of messages that had been received in ResponseEngine
		 * @param services - List of cases that were predefined in ResponseEngine's initCases()
		 */
		public ResponseExcecutor(List<ResponseCase> services) {
			this.services = services;
		}
		
		@Override
		protected void spoolingFunction(JSON node) throws Exception {
			for (int i = 0; i < services.size(); i++) {
				ResponseCase service = services.get(i);
				
				//find the correct response to perform
				if (node.getType().equals(service.getType()))
					service.respond(node);
			}
		}
	}
	
	protected static class NecroAnnouncer extends DiligentThread
	{
		protected Protocol protocol;
		protected ResponseEngine engine;
		protected volatile boolean running;
		
		/**
		 * @param engine - The main ResponseEngine object
		 */
		public NecroAnnouncer(ResponseEngine engine) {
			super(5);
			this.engine = engine;
			this.protocol = engine.getProtocol();
		}
		
		@Override
		protected void diligentFunction() throws Exception {
			if (!protocol.requestLivingAck() && running) engine.targetDied();
		}
	}
	
	protected Protocol protocol;
	protected ResponseExcecutor executor;
	protected NecroAnnouncer necro;
	protected String[] keyArr;
	protected List<String> caseKeys;
	protected List<ResponseCase> services;
	
	/**
	 * Construct an object with a new protocol.
	 * 
	 * WARNING: this constructor creates a new protocol, which means that if
	 * the argument network information's local port is already binded, a BindException will be thrown.
	 * 
	 * @param targetInformation - The network information of the target host
	 * @param checkDeath - True to regularly check if the target port is still alive
	 * @throws IOException when the protocol is unavailable.
	 */
	public ResponseEngine(NetworkInformation targetInformation, boolean checkDeath) throws IOException {
		this(new Protocol(targetInformation), checkDeath);
	}
	
	/**
	 * Create an object with an existing protocol.
	 * 
	 * @param protocol - The protocol to use to receive messages
	 * @param checkDeath - True to regularly check if the target port is still alive
	 * @throws IOException when the protocol is unavailable.
	 */
	public ResponseEngine(Protocol protocol, boolean checkDeath) throws IOException {
		this.protocol = protocol;
		this.caseKeys = new ArrayList<String>();
		this.services = new ArrayList<ResponseCase>();
		
		this.executor = new ResponseExcecutor(services);
		if (checkDeath) this.necro = new NecroAnnouncer(this);
		
		initCases();
	}
	
	/**
	 * Handle a message so the compatible algorithm can be executed.
	 * 
	 * @param msg - The message to send forward to the executor
	 */
	protected void handle(JSON msg) {
		executor.spool(msg);
	}
	
	/**
	 * Add a response case that will be executed when a compatible message arrives.
	 * 
	 * @param resCase - The case to handle during the engine's run time
	 */
	protected void addCase(ResponseCase resCase) {
		services.add(resCase);
		caseKeys.add(resCase.getType());
		keyArr = caseKeys.toArray(new String[caseKeys.size()]);
	}
	
	/**
	 * @return the protocol this engine uses.
	 */
	public Protocol getProtocol() { return protocol; }
	
	/**
	 * Initiate all of the engine's cases.
	 * Use at least one addCase(ResposeCase) in this method.
	 */
	protected abstract void initCases();
	
	/**
	 * Pause all running threads as a result of the target port's death.
	 */
	protected void targetDied() {
		executor.pause(true);
		if (necro != null) necro.pause(true);
		pause(true);
	}
	
	@Override
	protected void diligentFunction() throws Exception {
		try { handle(protocol.waitFor(keyArr)); }
		catch (IOException e) {
			Logger.error("Server error");
			e.printStackTrace();
		}
	}
	
	@Override
	public void start() {
		super.start();
		executor.start();
		if (necro != null) necro.start();
	}
	
	@Override
	public void pause(boolean flag) {
		super.pause(flag);
		
		if (flag) {
			try { protocol.flush(); }
			catch (IOException e) {}
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		executor.kill();
		if (necro != null) necro.kill();
	}
}
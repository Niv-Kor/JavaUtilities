package javaNK.util.networking;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javaNK.util.debugging.Logger;

public abstract class RespondEngine implements Runnable
{
	protected static class Responder implements Runnable
	{
		protected volatile List<RespondCase> services;
		protected volatile Queue<JSON> messages;
		
		public Responder(Queue<JSON> messages, List<RespondCase> services) {
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
							RespondCase service = services.get(i);
							
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
	protected Responder responder;
	protected List<String> caseKeys;
	protected volatile boolean running;
	protected volatile List<RespondCase> services;
	protected volatile Queue<JSON> messages;
	
	public RespondEngine(int port) throws IOException {
		this(new Protocol(port));
	}
	
	public RespondEngine(Protocol protocol) throws IOException {
		this.protocol = protocol;
		this.caseKeys = new ArrayList<String>();
		this.services = new ArrayList<RespondCase>();
		this.messages = new LinkedList<JSON>();
		this.responder = new Responder(messages, services);
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
	
	protected void handle(JSON msg) {
		messages.add(msg);
	}
	
	protected void addCase(RespondCase resCase) {
		services.add(resCase);
		caseKeys.add(resCase.getType());
	}
	
	public void pause(boolean flag) { running = !flag; }
	protected abstract void initCases();
}
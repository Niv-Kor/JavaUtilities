package javaNK.util.threads;

public abstract class DiligentThread extends Thread
{
	protected long rest;
	protected volatile boolean running;
	private volatile boolean dead;
	
	public DiligentThread() {
		this(0);
	}
	
	/**
	 * @param restSec - Amount of time between each running iteration
	 */
	public DiligentThread(double restSec) {
		setRestTime(restSec);
	}
	
	@Override
	public void run() {
		while (true) {
			if (running) {
				try { diligentFunction(); }
				catch (Exception e) {}
				
				try { Thread.sleep(rest); }
				catch (InterruptedException e1) { if (!running) return; }
			}
			else if (dead) return;
		}
	}
	
	/**
	 * @param sec - The new rest time (in seconds)
	 */
	public void setRestTime(double sec) { rest = (long) (sec * 1000); }
	
	/**
	 * @return the amount of rest time between iterations
	 */
	public double getRestTime() { return (double) (rest / 1000.0); }
	
	
	/**
	 * Pause or resume the thread.
	 * 
	 * @param flag - True to pause or false to resume
	 */
	public void pause(boolean flag) { running = !flag; }
	
	/**
	 * The function to call in every iteration of the thread.
	 * 
	 * @throws Exception when something goes wrong with the function.
	 */
	protected abstract void diligentFunction() throws Exception;
	
	@Override
	public void start() {
		if (!running) {
			super.start();
			pause(false);
		}
	}
	
	/**
	 * Close the thread permanently.
	 */
	public void kill() {
		pause(true);
		interrupt();
		dead = true;
	}
}
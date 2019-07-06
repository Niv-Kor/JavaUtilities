package javaNK.util.threads;

/**
 * A thread that perform an action only once, and then dies.
 * The action can be set to run in X seconds or immediately.
 * 
 * @author Niv Kor
 */
public abstract class QuickThread extends Thread implements Handleable
{
	protected long delay;
	protected volatile boolean dead, paused;
	
	public QuickThread() {
		this(0);
	}
	
	/**
	 * @param delay - Time to wait until execution (in seconds).
	 */
	public QuickThread(double delay) {
		this.delay = (long) (delay * 1000);
	}
	
	@Override
	public void run() {
		long timeWaited = ThreadUtility.delay(delay);
		
		if (!dead) {
			while (paused) {
				//this can be interrupted by pause(false)
				ThreadUtility.delay(8);
				
				//wait the remaining time
				if (!paused) ThreadUtility.delay(delay - timeWaited);
			}
			
			//now running
			try { quickFunction(); }
			catch (Exception e) { handleException(e); }
		}
		
		dead = true;
	}
	
	/**
	 * @return the delay time until the thread's execution (in seconds).
	 */
	public double getDelay() { return (double) (delay / 1000.0); }
	
	/**
	 * @param dly - The new delay (in seconds)
	 */
	public void setDelay(double dly) { delay = (long) (dly * 1000); }
	
	/**
	 * Cancel the upcoming execution.
	 */
	public void cancel() {
		dead = true;
		interrupt();
	}
	
	/**
	 * Pause or resume the thread.
	 * 
	 * @param flag - True to pause or false to resume
	 */
	public void pause(boolean flag) {
		paused = flag;
		interrupt();
	}
	
	/**
	 * @return true if the thread finished its job or was canceled.
	 */
	public boolean isDead() { return dead; }
	
	/**
	 * The function to call once in this thread.
	 * 
	 * @throws Exception when something goes wrong with the function.
	 */
	public abstract void quickFunction() throws Exception;
	
	@Override
	public void handleException(Exception e) {}
}
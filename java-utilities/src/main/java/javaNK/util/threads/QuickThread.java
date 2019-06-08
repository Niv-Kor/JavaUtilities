package javaNK.util.threads;

public abstract class QuickThread extends Thread
{
	protected long delay;
	protected volatile boolean canceled;
	
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
		ThreadUtility.delay(delay);
		if (!canceled) {
			try { quickFunction(); }
			catch (Exception e) { e.printStackTrace(); }
		}
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
		canceled = true;
		interrupt();
	}
	
	/**
	 * The function to call once in this thread.
	 * 
	 * @throws Exception when something goes wrong with the function.
	 */
	public abstract void quickFunction() throws Exception;
}
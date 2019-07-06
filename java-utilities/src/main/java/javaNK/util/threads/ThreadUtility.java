package javaNK.util.threads;

public class ThreadUtility
{
	/**
	 * Block a thread for a fixed amount of time.
	 * 
	 * @param millisec - The amount of time to block (in milliseconds)
	 * @return the time that the thread actually waited (could be interrupted).
	 */
	public static long delay(long millisec) {
		long nowMillis = System.currentTimeMillis();
		
		if (millisec > 0) {
			try { Thread.sleep(millisec); }
			catch (InterruptedException e) {
				//calculate the time that has been actually waited
				return System.currentTimeMillis() - nowMillis;
			}
		}
		
		return millisec;
	}
}
package javaNK.util.threads;

public class ThreadUtility
{
	public static void delay(long millisec) {
		if (millisec > 0) {
			try { Thread.sleep(millisec); }
			catch (InterruptedException e) {}
		}
	}
}
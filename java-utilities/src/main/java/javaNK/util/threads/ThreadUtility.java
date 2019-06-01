package javaNK.util.threads;

public class ThreadUtility
{
	public static void delay(long mili) {
		try { Thread.sleep(mili); }
		catch(InterruptedException e) {}
	}
}
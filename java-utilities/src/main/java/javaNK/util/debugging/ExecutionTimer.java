package javaNK.util.debugging;
import java.util.HashMap;
import java.util.Map;

public class ExecutionTimer
{
	private static class Timer
	{
		private long startTime, endTime, elapsedTime;
		private boolean started, running;
		
		public Timer() {
			this.elapsedTime = 0;
		}
		
		/**
		 * Start or continue running the timer.
		 */
		public void run() {
			if (started && running) return;
			
			startTime = System.currentTimeMillis();
			started = true;
			running = true;
		}
		
		/**
		 * Pause the timer if it's running.
		 */
		public void pause() {
			if (!running) return;
			
			elapsedTime = getElapsedTime();
			running = false;
		}
		
		/**
		 * @return the timer's result (in milliseconds) up to this moment.
		 */
		public long getElapsedTime() {
			endTime = System.currentTimeMillis();
			return elapsedTime + (endTime - startTime);
		}
	}
	
	private static Map<String, Timer> timers = new HashMap<String, Timer>();
	
	/**
	 * Start or continue running a timer.
	 * 
	 * @param name - The name of the timer
	 */
	public static void run(String name) {
		getTimer(name).run();
	}
	
	/**
	 * Pause a running timer.
	 * 
	 * @param name - The name of the timer
	 * @return true if the timer was paused successfully.
	 */
	public static boolean pause(String name) {
		if (timers.containsKey(name)) {
			getTimer(name).pause();
			return true;
		}
		else return false;
	}
	
	/**
	 * Stop a timer, remove it and print out its results.
	 * 
	 * @param name - The name of the timer
	 */
	public static void finish(String name) {
		if (pause(name)) {
			long elapsedTime = getTimer(name).getElapsedTime();
			Logger.print("[" + name + "]'s execution time is " + elapsedTime + " msec.");
			remove(name);
		}
	}
	
	/**
	 * Reset a timer's time back to 0.
	 * 
	 * @param name - The name of the timer
	 */
	public static void reset(String name) {
		remove(name);
		run(name);
	}
	
	/**
	 * Remove a timer from the memory.
	 * 
	 * @param name - The name of the timer
	 */
	public static void remove(String name) {
		timers.remove(name);
	}
	
	/**
	 * @param name - The name of the timer
	 * @return the timer object
	 */
	private static Timer getTimer(String name) {
		Timer timer = timers.get(name);
		
		if (timer == null) {
			timer = new Timer();
			timers.put(name, timer);
		}
		
		return timer;
	}
}
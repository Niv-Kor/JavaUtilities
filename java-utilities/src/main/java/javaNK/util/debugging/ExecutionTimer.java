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
		
		public void run() {
			if (started && running) return;
			
			startTime = System.currentTimeMillis();
			started = true;
			running = true;
		}
		
		public void pause() {
			if (!running) return;
			
			endTime = System.currentTimeMillis();
			elapsedTime += endTime - startTime;
			running = false;
		}
		
		public long getElapsedTime() { return elapsedTime; }
	}
	
	private static Map<String, Timer> timers = new HashMap<String, Timer>();
	
	public static void run(String name) {
		getTimer(name).run();
	}
	
	public static boolean pause(String name) {
		if (timers.containsKey(name)) {
			getTimer(name).pause();
			return true;
		}
		else return false;
	}
	
	public static void finish(String name) {
		if (pause(name)) {
			long elapsedTime = getTimer(name).getElapsedTime();
			System.out.println("[" + name + "]'s execution time is " + elapsedTime + " msec.");
			remove(name);
		}
	}
	
	public static void reset(String name) {
		remove(name);
		run(name);
	}
	
	public static void remove(String name) {
		timers.remove(name);
	}
	
	private static Timer getTimer(String name) {
		Timer timer = timers.get(name);
		
		if (timer == null) {
			timer = new Timer();
			timers.put(name, timer);
		}
		
		return timer;
	}
}
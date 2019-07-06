package javaNK.util.threads;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A thread that stores a queue of objects, and performs an action on the next dequequed object if it exists.
 * After the action executes, and as long as the node hasn't been saved for later use, it's freed for good.
 * 
 * @param <T> - The type of the objects in the queue
 * @author Niv Kor
 */
public abstract class DaemonThread<T> extends DiligentThread
{
	protected Queue<T> spoolQueue;
	
	public DaemonThread() {
		super(0.008);
		this.spoolQueue = new LinkedList<T>();
	}

	@Override
	protected void diligentFunction() throws Exception {
		if (!spoolQueue.isEmpty()) spoolingFunction(spoolQueue.poll());
	}
	
	/**
	 * Add an object to the queue.
	 * 
	 * @param node - The object to add
	 */
	public void spool(T node) { spoolQueue.add(node); }
	
	/**
	 * Clear the queue.
	 */
	public void flush() { spoolQueue.clear(); }
	
	/**
	 * The function to call for every object in the queue.
	 * 
	 * @param node - The last object that was dequeued
	 * @throws Exception when something goes wrong with the function.
	 */
	protected abstract void spoolingFunction(T node) throws Exception;
}
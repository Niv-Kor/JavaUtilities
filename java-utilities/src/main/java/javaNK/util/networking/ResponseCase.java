package javaNK.util.networking;	

/**
 * This interface cooperates with ResponseEngine,
 * in a way that it represents a case that should be responded.
 * It provides the message type to respond to, and an algorithm that
 * will be executed when the above message arrives.
 *  
 * @author Niv Kor
 */
public interface ResponseCase
{
	/**
	 * Get the type of the message this case responds to.
	 * It can only respond to one message type.
	 * 
	 * @return the type of the message to respond to.
	 */
	String getType();
	
	/**
	 * An algorithm to be executed when the correct message arrives.
	 * 
	 * @param msg - The message that triggered this method
	 * @throws Exception when something goes wrong with the algorithm.
	 */
	void respond(JSON msg) throws Exception;
}
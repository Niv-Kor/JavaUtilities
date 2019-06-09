package javaNK.util.debugging;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Scanner;
import javaNK.util.math.NumeralHandler;
import javaNK.util.networking.JSON;

public class Logger
{
	private static final char DASH = '>';
	private static final String POSTFIX = ": ";
	
	private static String prefix = "Logger";
	private static boolean addLine = true;
	private static InputStream inputSt = System.in;
	private static OutputStream outputSt = System.out;
	private static OutputStream errorSt = System.err;
	private static Scanner scanner = new Scanner(inputSt);
	
	/**
	 * Change the name of the logger, so all logs will be displayed as: "> [name]: [message]".
	 * Default logger name is "Logger".
	 * 
	 * @param name - The name to display
	 */
	public static void configName(String name) { prefix = new String(name); }
	
	/**
	 * Set the input stream of the logger.
	 * Default input stream is System.in.
	 * 
	 * @param stream - The input stream to use
	 */
	public static void configInputStream(InputStream stream) {
		inputSt = stream;
		scanner = new Scanner(stream);
	}
	
	/**
	 * Set the output stream of the logger.
	 * Default output stream is System.out.
	 * 
	 * @param stream - The output stream to use
	 */
	public static void configOutputStream(OutputStream stream) { outputSt = stream; }
	
	/**
	 * Set the error stream of the logger.
	 * Default error stream is System.err.
	 * 
	 * @param stream - The error stream to use
	 */
	public static void configErrorStream(OutputStream stream) { errorSt = stream; }
	
	/**
	 * Print a simple message to the output stream.
	 * 
	 * @param msg - The message to print
	 */
	public static void print(String msg) {
		print(outputSt, msg);
	}
	
	/**
	 * Print an error to the error stream.
	 * 
	 * @param msg - The error message to print
	 */
	public static void error(String msg) {
		print(errorSt, msg);
	}
	
	private static void print(OutputStream output, String msg) {
		try {
			if (addLine) output.write(("\n").getBytes());
			output.write((timeStamp() + " " + DASH + " " + prefix + POSTFIX + msg + "\n").getBytes());
		}
		catch (IOException e) {}
	}
	
	/**
	 * Print an error and its stack trace to the error stream.
	 * 
	 * @param msg - The exception to display
	 */
	public static void error(Exception e) {
		error(e.getMessage());
		e.printStackTrace();
	}
	
	/**
	 * print an error to the error stream, stating that a JSON message was unsuccessful.
	 * 
	 * @param msg - The JSON message that was received
	 * @param reason - The reason for the error
	 */
	public static void error(JSON msg, String reason) {
		String message = (msg != null) ? msg.getType() : "[empty message]";
		error("Unsuccessful command '" + message + "', " + reason + ".");
	}
	
	/**
	 * Create a new line in the output stream (recommended for the entry of input).
	 */
	public static void newLine() {
		try {
			outputSt.write((timeStamp() + " " + DASH + " ").getBytes());
			addLine = true;
		}
		catch (IOException e) {}
	}
	
	/**
	 * Input an integer value.
	 * 
	 * @return the entered value, or -1 if the value is not a legal integer.
	 */
	public static int inputInt() {
		newLine();
		int value;
		
		try { value = scanner.nextInt(); }
		catch (NumberFormatException e) { return -1; }
		
		addLine = false;
		return value;
	}
	
	/**
	 * Input a String value.
	 * 
	 * @return the entered value.
	 */
	public static String inputLine() {
		newLine();
		String value = scanner.next();
		addLine = false;
		return value;
	}
	
	/**
	 * Create a time stamp of the current hour:minutes:seconds:milliseconds.
	 */
	private static String timeStamp() {
		LocalDateTime now = LocalDateTime.now();
		int nano = NumeralHandler.round(now.getNano(), 3);
		return now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + ":" + nano;
	}
}
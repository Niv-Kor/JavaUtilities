package javaNK.util.debugging;
import java.time.LocalDateTime;
import java.util.Scanner;
import javaNK.util.math.NumeralHandler;
import javaNK.util.networking.JSON;

public class Logger
{
	private static final char DASH = '>';
	private static final String POSTFIX = ": ";
	
	private static String prefix = "Logger";
	private static Scanner scan = new Scanner(System.in);
	private static boolean needNewLine;
	
	/**
	 * Change the name of the logger, so all logs will be displayed as: "> [name]: [message]".
	 * @param name - The name to display
	 */
	public static void config(String name) {
		prefix = new String(name);
	}
	
	/**
	 * Print a simple message in the console.
	 * @param msg - The message to print
	 */
	public static void print(String msg) {
		//add a new line if needed (after waiting for input and not getting it)
		if (needNewLine) System.out.println();
		
		String period = (msg.charAt(msg.length() - 1) != '.') ? "." : "";
		System.out.println(timeStamp() + " " + DASH + " " + prefix + POSTFIX + msg + period);
	}
	
	/**
	 * Create a new line in the console (recommended for input).
	 */
	public static void newLine() {
		System.out.print(timeStamp() + " " + DASH + " ");
		needNewLine = true;
	}
	
	public static int inputInt() {
		newLine();
		int value = scan.nextInt();
		needNewLine = false;
		return value;
	}
	
	public static String inputLine() {
		newLine();
		String value = scan.nextLine();
		needNewLine = false;
		return value;
	}
	
	/**
	 * Print an error in the console.
	 * @param msg - The error message to print
	 */
	public static void error(String msg) {
		newLine();
		String period = (msg.charAt(msg.length() - 1) != '.') ? "." : "";
		System.err.println(prefix + POSTFIX + msg + period);
	}
	
	/**
	 * Print an error in the console.
	 * @param msg - The exception to display
	 */
	public static void error(Exception e) {
		error(e.getMessage());
	}
	
	/**
	 * print an error in the console, saying that a JSON message was unsuccessful.
	 * 
	 * @param msg - The JSON message that was received
	 * @param reason - The reason for the error
	 */
	public static void error(JSON msg, String reason) {
		String message = (msg != null) ? msg.getType() : "[empty message]";
		error("Unsuccessful command '" + message + "', " + reason + ".");
	}
	
	/*
	 * Create a time stamp of the current hour:minutes:seconds:milliseconds.
	 */
	private static String timeStamp() {
		LocalDateTime now = LocalDateTime.now();
		int nano = NumeralHandler.round(now.getNano(), 3);
		return now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + ":" + nano;
	}
}
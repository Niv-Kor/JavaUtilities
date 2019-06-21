package javaNK.util.IO;
import javaNK.util.math.Range;

public class StringVerifier
{
	public static boolean verifyEmail(String email) {
		int atIndex = email.indexOf('@');
		boolean legalAt = email.contains("@") && atIndex > 0 && atIndex == email.lastIndexOf('@');
		if (!legalAt) return false;
		
		String fromDomain = email.substring(atIndex + 1);
		int dotIndex = fromDomain.indexOf('.');
		boolean legalDot = fromDomain.contains(".") && dotIndex > 0 && dotIndex != fromDomain.length() - 1;
		if (!legalDot) return false;
		
		try {
			String user = email.substring(0, atIndex);
			String domainName = fromDomain.substring(0, fromDomain.indexOf('.'));
			String domain = fromDomain.substring(fromDomain.indexOf('.') + 1);
			
			String regex = "[a-zA-Z0-9_\\.\\-]";
			return verify(user, regex) && verify(domainName, regex) && verify(domain, regex);
		}
		catch (StringIndexOutOfBoundsException e) { return false; }
	}
	
	public static boolean verifyUsername(String username) {
		return !username.equals("") && verify(username);
	}
	
	public static boolean verifyPassword(String password, int min) {
		return verifyPassword(password, min, Integer.MAX_VALUE);
	}
	
	public static boolean verifyPassword(String password, int min, int max) {
		Range<Integer> range = new Range<Integer>(min, max);
		boolean inSize = range.intersects(password.length());
		return inSize && verify(password); 
	}
	
	public static boolean verify(String str) {
		return verify(str, "[a-zA-Z0-9]");
	}
	
	public static boolean verify(String str, String regex) {
		return !str.equals("") && str.equals(adjust(str, regex));
	}
	
	public static String adjust(String str, String regex) {
		String negative = "^" + regex.substring(1);
		return str.replaceAll(negative, "");
	}
}
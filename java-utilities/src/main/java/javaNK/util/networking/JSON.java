package javaNK.util.networking;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javaNK.util.debugging.Logger;

/**
 * This class modifies JSONObject objects, and eases their usability.
 * Every JSON message contains a "type" field, which is determined during construction.
 * 
 * @author Niv Kor
 */
public class JSON extends JSONObject
{
	private static final long serialVersionUID = 3472688265395105685L;
	
	/**
	 * This constructor has two roles - create a new JSON object, or convert a string to JSON object.
	 * 
	 * @param str - If constructing a new JSON object - this argument is the message type {"type:*"}.
	 * 				If converting a String to a JSON object - this argument is the string to convert.
	 */
	public JSON(String str) {
		if (str.charAt(0) == '{') convert(str);
		else initNew(str);
	}
	
	private JSON(String str, boolean decode) {
		str = str.replace("L*E*F*T", "{")
				 .replace("R*I*G*H*T", "}")
				 .replace("C*O*L*N", ":")
				 .replace("Q*U*T", "\"")
				 .replace("C*O*M*A", ",");
		
		convert(str);
	}
	
	/**
	 * Initiate a new JSON object.
	 * 
	 * @param title - The message type
	 */
	protected void initNew(String title) {
		put("type", title);
	}
	
	/**
	 * Convert a String to JSON object.
	 * 
	 * @param message - The String to convert
	 */
	protected void convert(String message) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			
			//split the rows to an array
			String[] rows = obj.toJSONString().replace("{", "")
											  .replace("}", "")
											  .replace("\"", "")
											  .split(",");
			
			//split each row to key and value and insert both
			for (int i = 0; i < rows.length; i++) {
				String[] keyVal = rows[i].split(":");
				put(keyVal[0], keyVal[1]);
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void merge(JSON other) {
		for (Object key : other.keySet())
			if (!key.equals("type")) put(key, other.get(key));
	}
	
	@Override
	public Object put(Object key, Object value) {
		if (value instanceof JSON) return put(key.toString(), (JSON) value);
		else return put(key.toString(), value);
	}
	
	/**
	 * Put a JSON object. 
	 * 
	 * @see JSONObject.put(Object, Object)
	 */
	private Object put(String key, JSON value) {
		return put(key, value.encode());
	}
	
	/**
	 * @return the type of the message.
	 */
	public String getType() {
		return getString("type");
	}
	
	/**
	 * @param title - The new type of the message
	 */
	public void setType(String title) {
		remove("type");
		put("type", title);
	}
	
	/**
	 * Get an integer value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as an int type.
	 */
	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	
	/**
	 * Get a double value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as a double type.
	 */
	public double getDouble(String key) {
		return Double.parseDouble(getString(key));
	}
	
	/**
	 * Get a char value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the first character of the String-casted value.
	 */
	public char getChar(String key) {
		return getString(key).charAt(0);
	}
	
	/**
	 * Get a String value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as a String object.
	 */
	public String getString(String key) {
		return (String) get(key).toString();
	}
	
	/**
	 * Get a JSON object as value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as a JSON object.
	 */
	public JSON getJSON(String key) {
		return new JSON(getString(key), true);
	}
	
	/**
	 * @param key - The key that's assigned to the desired value
	 * @return the value as an array of JSON objects.
	 */
	public JSON[] getJSONArray(String key) {
		String[] strArr = getStringArray(key);
		JSON[] jsonArr = new JSON[strArr.length];
		
		for (int i = 0; i < jsonArr.length; i++)
			jsonArr[i] = new JSON(strArr[i], true);
		
		return jsonArr;
	}
	
	/**
	 * Get an array of Strings from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as an array of String objects.
	 */
	public String[] getStringArray(String key) {
		String value = getString(key);
		if (value.equals("^")) return new String[] {};
		else return value.split("^");
	}
	
	/**
	 * Get a boolean value from the message.
	 * 
	 * @param key - The key that's assigned to the desired value
	 * @return the value as a boolean type.
	 */
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}
	
	/**
	 * Put an array of JSON objects.
	 * 
	 * @param key - The key of the field
	 * @param array - Array of JSON objects
	 */
	public void putJSONArray(String key, JSON[] array) {
		if (array == null) {
			put(key, "^");
			return;
		}
		
		String[] strArr = new String[array.length];
		
		for (int i = 0; i < strArr.length; i++)
			strArr[i] = array[i].encode();
		
		putArray(key, strArr);
	}
	
	/**
	 * Put an array of Strings.
	 * 
	 * @param key - The key of the field
	 * @param array - Array of String objects
	 */
	public void putArray(String key, String[] array) {
		if (array == null) {
			put(key, "^");
			return;
		}
		
		String arrayStr = "";
		for (int i = 0; i < array.length; i++) {
			arrayStr = arrayStr.concat(array[i]);
			if (i < array.length - 1) arrayStr = arrayStr.concat("^");
		}
		
		put(key, arrayStr);
	}
	
	/**
	 * Encode a nested JSON message, which is actually a String.
	 * In order to prevent the nested JSON message from participating in the
	 * String modifications the primary message is taking part in, encode and hide it.
	 * The private constructor JSON(String, boolean) knows how to decode a JSON String.
	 * 
	 * @return an encoded JSON message.
	 */
	private String encode() {
		String encoded = toJSONString().
						 replace(":", "C*O*L*N").
						 replace("{", "L*E*F*T").
						 replace("}", "R*I*G*H*T").
						 replace("\"", "Q*U*T").
						 replace(",", "C*O*M*A");
		
		return encoded;
	}
	
	@Override
	public String toString() {
		return toJSONString().replace("\\\"", "");
	}
	
	@Override
	public Object remove(Object key) {
		if (key instanceof String && key.equals("type")) return null;
		else return super.remove(key);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		if (key instanceof String && key.equals("type")) return false;
		else return super.remove(key, value);
	}
}
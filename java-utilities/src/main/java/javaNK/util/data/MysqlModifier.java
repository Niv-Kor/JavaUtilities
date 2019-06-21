package javaNK.util.data;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

/**
 * Connect to a MySQL database and perform any modifications to it -
 * all within the available permissions of the authenticated user.
 * 
 * @author Niv Kor
 */
public class MysqlModifier
{
	private static final String NO_CONNECTION_ESTABLISED = "Please establish a connection to the schema first.";
	
	private static Statement statement;
	
	/**
	 * Connect to a MySQL database.
	 * 
	 * @param host - The host of the data base.
	 * 				 If using local host put 'localhost:[port number]',
	 * 				 where the default port number is 3306.
	 * @param schema - Name of the schema to use
	 * @param username - The user name of the MySQL account (default is 'root')
	 * @param password - The password of the MySQL account (leave 'null' if there isn't a password)
	 * @return true if the connection was successful.
	 */
	public static boolean connect(String host, String schema, String username, String password) {
		String timezoneSettings = "?useLegacyDatetimeCode=false&serverTimezone=UTC";
		String Hostname = "jdbc:mysql://" + host + "/" + schema + timezoneSettings;
		Connection connection;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(Hostname, username, password);
			statement = connection.createStatement();
			return true;
		}
		catch(CommunicationsException e) {
			System.err.println("Could not connect to MySQL database due to network failure.\n"
							 + "Please check your internet connection and try again.");
		}
		catch (Exception e) {
			System.err.println("Could not connect to MySQL database.");
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Write to the data base.
	 * Actions that can be performed using this method:
	 * 'INSERT', 'UPDATE', 'DELETE', 'CREATE', 'DROP'
	 * 
	 * @param query - The query to execute
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static void write(String query) throws SQLException {
		checkConnection();
		statement.executeUpdate(query);
	}
	
	/**
	 * Get an Integer (int) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return an Integer (int) value.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static int readINT(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = statement.executeQuery(query);
		
		if (resultSet.next()) return resultSet.getInt(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Integer (int) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Integer (int) values.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static ArrayList<Integer> readAllINT(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) list.add(resultSet.getInt(column));

		return list;
	}
	
	/**
	 * Get a Decimal (double) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Decimal (double) value.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static double readDECIMAL(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = statement.executeQuery(query);
		
		if (resultSet.next()) return resultSet.getDouble(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Decimal (double) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Decimal (double) values.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static ArrayList<Double> readAllDECIMAL(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Double> list = new ArrayList<Double>();
		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) list.add(resultSet.getDouble(column));

		return list;
	}
	
	/**
	 * Get a Varchar (String) value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Varchar (String) value.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static String readVARCHAR(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = statement.executeQuery(query);
		
		if (resultSet.next()) return resultSet.getString(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Varchar (String) values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Varchar (String) values.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static ArrayList<String> readAllVARCHAR(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<String> list = new ArrayList<String>();
		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) list.add(resultSet.getString(column));
		
		return list;
	}
	
	/**
	 * Get a Boolean value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Boolean value.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static boolean readBOOLEAN(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = statement.executeQuery(query);
		
		if (resultSet.next()) return resultSet.getBoolean(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Boolean values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Boolean values.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static ArrayList<Boolean> readAllBOOLEAN(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Boolean> list = new ArrayList<Boolean>();
		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) list.add(resultSet.getBoolean(column));

		return list;
	}
	
	/**
	 * Get a Timestamp value from a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a Timestamp value.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static Timestamp readTIMESTAMP(String query, String column) throws SQLException {
		checkConnection();
		ResultSet resultSet = statement.executeQuery(query);
		
		if (resultSet.next()) return resultSet.getTimestamp(column);
		else throw new SQLException();
	}
	
	/**
	 * Get a list of all Date values from a column of a table.
	 * 
	 * @param query - The query to execute
	 * @param column - Name of the column
	 * @return a list of Date values.
	 * @throws SQLException when the query has errors, or the connection has not been established.
	 */
	public static ArrayList<Date> readAllDATE(String query, String column) throws SQLException {
		checkConnection();
		ArrayList<Date> list = new ArrayList<Date>();
		
		ResultSet resultSet = statement.executeQuery(query);
		while (resultSet.next()) list.add(resultSet.getDate(column));

		return list;
	}
	
	/**
	 * Build a simple structural query.
	 * 
	 * @param select - SELECT [column(s) argument]
	 * @param from - FROM [table argument]
	 * @param where - WHERE [1 column argument]
	 * @param equals - = [1 value argument]
	 * @return the query as a String object.
	 */
	public static String buildQuery(String select, String from, String where, Object equals) {
		String apostrophe = (equals instanceof String) ? "'" : "";
		
		return "SELECT " + select + " "
			 + "FROM " + from + " "
			 + "WHERE " + where + " = " + apostrophe + equals + apostrophe;
	}
	
	/**
	 * @throws SQLException when no connection to the data base has been established.
	 */
	private static void checkConnection() throws SQLException {
		if (statement == null) {
			System.err.println(NO_CONNECTION_ESTABLISED);
			throw new SQLException();
		}
	}
}
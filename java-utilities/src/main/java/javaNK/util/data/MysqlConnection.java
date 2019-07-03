package javaNK.util.data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javaNK.util.debugging.Logger;

public class MysqlConnection
{
	private Connection connection;
	
	/**
	 * @param host - The host of the data base.
	 * 				 If using local host put 'localhost:[port number]',
	 * 				 where the default port number is 3306.
	 * @param schema - Name of the schema to use
	 * @param username - The user name of the MySQL account (default is 'root')
	 * @param password - The password of the MySQL account (leave 'null' if there isn't a password)
	 * @throws SQLException when the connection could not be established.
	 */
	public MysqlConnection(String host, String schema, String username, String password) throws SQLException {
		String timezoneSettings = "?useLegacyDatetimeCode=false&serverTimezone=UTC";
		String Hostname = "jdbc:mysql://" + host + "/" + schema + timezoneSettings;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.connection = DriverManager.getConnection(Hostname, username, password);
			return;
		}
		catch(CommunicationsException e) {
			Logger.error("Could not connect to MySQL database due to network failure.\n"
					   + "Please check your internet connection and try again.");
		}
		catch (Exception e) {
			Logger.error("Could not connect to MySQL database.");
			e.printStackTrace();
		}
		
		throw new SQLException();
	}
	
	/**
	 * @return a Statement object, providing the ability to make I/O operations on the data base. 
	 */
	public Statement getStatement() {
		try { return connection.createStatement(); }
		catch (Exception e) { return null; }
	}
}
package javaNK.util.data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public enum DataType
{
	INTEGER(Integer.class, false),
	DECIMAL(Double.class, false),
	VARCHAR(String.class, true),
	BOOLEAN(Boolean.class, false),
	TIMESTAMP(Timestamp.class, true);
	
	private String className;
	private boolean isString;
	
	private DataType(Class<?> cls, boolean isString) {
		this.className = className(cls);
		this.isString = isString;
	}
	
	public Object getValue(String query, String column) throws SQLException {
		Object returnedObj;
		
		switch (className) {
			case "Integer": returnedObj = MysqlModifier.readINT(query, column); break;
			case "Double": returnedObj = MysqlModifier.readDECIMAL(query, column); break;
			case "String": returnedObj = MysqlModifier.readVARCHAR(query, column); break;
			case "Boolean": returnedObj = MysqlModifier.readBOOLEAN(query, column); break;
			case "Timestamp": returnedObj = MysqlModifier.readTIMESTAMP(query, column); break;
			default: throw new SQLException();
		}
		
		return returnedObj;
	}
	
	public Object getValue(ResultSet resultSet, String column) throws SQLException {
		Object obj = null;
		
		try {
			switch (className) {
				case "Integer": obj = resultSet.getInt(column); break;
				case "Double": obj = resultSet.getDouble(column); break;
				case "String": obj = resultSet.getString(column); break;
				case "Boolean": obj = resultSet.getBoolean(column); break;
				case "Timestamp": obj = resultSet.getTimestamp(column); break;
			}
		}
		catch (SQLException e) {}
		
		return obj;
	}
	
	public List<Object> getAllValues(String query, String column) {
		List<Object> list = new ArrayList<Object>();
		
		try {
			switch (className) {
				case "Integer": list.addAll(MysqlModifier.readAllINT(query, column)); break;
				case "Double": list.addAll(MysqlModifier.readAllDECIMAL(query, column)); break;
				case "String": list.addAll(MysqlModifier.readAllVARCHAR(query, column)); break;
				case "Boolean": list.addAll(MysqlModifier.readAllBOOLEAN(query, column)); break;
				case "Timestamp": list.addAll(MysqlModifier.readAllTIMESTAMP(query, column)); break;
				default: throw new SQLException();
			}
		}
		catch (SQLException e) {}
		
		return list;
	}
	
	public static DataType convert(Class<?> cls) {
		String className = className(cls).toUpperCase();
		
		switch (className) {
			case "STRING": return VARCHAR;
			case "DOUBLE": return DECIMAL;
			default: return valueOf(className);
		}
	}
	
	public boolean usedAsString() { return isString; }
	
	private static String className(Class<?> cls) {
		String fullName = cls.getTypeName();
		return fullName.substring(fullName.lastIndexOf('.') + 1, fullName.length());
	}
}
package javaNK.util.data;

public class MysqlColumn
{
	private String name;
	private DataType dataType;
	
	/**
	 * @param name - Header name of the column (as typed in the database)
	 * @param dataType - The column's data type
	 */
	public MysqlColumn(String name, DataType dataType) {
		this.name = name;
		this.dataType = dataType;
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			MysqlColumn otherColumn = (MysqlColumn) other;
			return name.equals(otherColumn.getName()) && dataType == otherColumn.getDataType();
		}
		catch (ClassCastException e) { return false; }
	}
	
	/**
	 * @return the column's header.
	 */
	public String getName() { return name; }
	
	/**
	 * @return the column's data type.
	 */
	public DataType getDataType() { return dataType; }
}
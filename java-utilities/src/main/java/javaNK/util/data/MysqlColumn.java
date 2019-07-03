package javaNK.util.data;

public class MysqlColumn
{
	private String name;
	private DataType dataType;
	
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
	
	public String getName() { return name; }
	
	public DataType getDataType() { return dataType; }
}
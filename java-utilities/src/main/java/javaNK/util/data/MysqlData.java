package javaNK.util.data;

public class MysqlData
{
	private MysqlColumn column;
	private Object value;
	
	/**
	 * @param column - The column that contains the data
	 */
	public MysqlData(MysqlColumn column) {
		this.column = column;
		this.value = null;
	}
	
	/**
	 * Get the value as it's suppose to be written inside a query.
	 * Apostrophes will be added when needed. 
	 * 
	 * @return the value of the field as a String object, fit for any query.
	 */
	public String getQueryValue() {
		if (value == null) return "NULL";
		
		String apostrophe = "'";
		String valueStr;
		
		if (column.getDataType().usedAsString())
			valueStr = apostrophe + value.toString() + apostrophe;
		else
			valueStr = value.toString();
		
		return valueStr;
	}
	
	/**
	 * @return the value of the cell
	 */
	public Object getValue() { return value; }
	
	/**
	 * @param valObj - The new value of the cell
	 */
	public void setValue(Object valObj) { value = valObj; }
	
	/**
	 * @return the column that contains the data
	 */
	public MysqlColumn getColumn() { return column; }
}
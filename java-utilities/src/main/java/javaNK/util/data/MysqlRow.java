package javaNK.util.data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javaNK.util.debugging.Logger;

public abstract class MysqlRow
{
	private List<MysqlData> keyFields, liquidFields;
	private String tableName;
	
	/**
	 * @param tableName - The name of the table that contains the row
	 */
	public MysqlRow(String tableName) {
		this.tableName = new String(tableName);
		this.keyFields = new ArrayList<MysqlData>();
		this.liquidFields = new ArrayList<MysqlData>();
		addFields();
	}
	
	/**
	 * Save all changes of the data in the data base.
	 * The data will be either updated or inserted as new.
	 * 
	 * @return true if the data was saved successfully.
	 */
	public boolean save() {
		String query;
		MysqlData tempField;
		
		if (isInDatabase()) {
			query = "UPDATE IGNORE " + tableName + " SET ";
			
			for (int i = 0; i < liquidFields.size(); i++) {
				tempField = liquidFields.get(i);
				query = query.concat(tempField.getColumn().getName() + " = " + tempField.getQueryValue() + " ");
				
				if (i < liquidFields.size() - 1) query = query.concat(", ");
				else query = query.concat(keyCondition());
			}
		}
		else {
			query = "INSERT IGNORE INTO " + tableName + "(";
			int fieldsAmount;
			
			/*
			 * Parenthesis section
			 */
			fieldsAmount = keyFields.size() + liquidFields.size();
			
			//key fields
			for (MysqlData field : keyFields) {
				fieldsAmount--;
				query = query.concat(field.getColumn().getName());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") VALUES (");
			}
			
			//liquid fields
			for (MysqlData field : liquidFields) {
				fieldsAmount--;
				query = query.concat(field.getColumn().getName());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") VALUES (");
			}
			
			/*
			 * Values section
			 */
			fieldsAmount = keyFields.size() + liquidFields.size();
			
			//key fields
			for (MysqlData field : keyFields) {
				fieldsAmount--;
				query = query.concat(field.getQueryValue());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") ");
			}
			
			//liquid fields
			for (MysqlData field : liquidFields) {
				fieldsAmount--;
				query = query.concat(field.getQueryValue());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") ");
			}
		}
		
		try {
			MysqlModifier.write(query);
			return true;
		}
		catch (SQLException e) {
			Logger.error("The query:\n" + query + "\nwent wrong."
					   + "Copy and check it in any MySQL platform for errors.");
			
			return false;
		}
	}
	
	/**
	 * Delete this row of data from the data base.
	 * 
	 * @return true if the data was deleted successfully.
	 */
	public boolean delete() {
		String query = "DELETE FROM " + tableName + " " + keyCondition();
		try {
			MysqlModifier.write(query);
			return true;
		}
		catch (SQLException e) { return false; }
	}
	
	/**
	 * Verify that this row (its key values, to be exact) already exists in the data base.
	 * 
	 * @return true if this row exists in the data base.
	 */
	protected boolean isInDatabase() {
		try {
			String query = "SELECT EXISTS ( "
					  	 + "	SELECT * "
					  	 + "	FROM " + tableName + " "
					  	 + keyCondition() + ") AS key_exists";
			
			return MysqlModifier.readBOOLEAN(query, "key_exists");
		}
		catch (SQLException | NullPointerException e) { return false; }
	}
	
	/**
	 * @return a query that selects all of the columns for this row.
	 */
	protected String selectAllQuery() {
		return "SELECT * FROM " + tableName + " " + keyCondition();
	}
	
	/**
	 * @return the condition that specifies this row's key values (the 'WHERE' segment).
	 */
	protected String keyCondition() {
		if (keyFields.isEmpty()) return "";
		else {
			String condition = "WHERE ";
			MysqlData tempField;
			
			for (int i = 0; i < keyFields.size(); i++) {
				tempField = keyFields.get(i);
				condition = condition.concat(tempField.getColumn().getName() + " = " + tempField.getQueryValue());
				
				if (i < keyFields.size() - 1) condition = condition.concat(" AND ");
			}
			
			return condition;
		}
	}
	
	/**
	 * @param fieldColumn - The column of the field
	 * @return - A data from the row, contained in the specified field
	 */
	protected Object getField(MysqlColumn fieldColumn) {
		List<MysqlData> allFields = new ArrayList<MysqlData>(keyFields);
		allFields.addAll(liquidFields);
		
		for (MysqlData field : allFields) {
			if (field.getColumn().equals(fieldColumn))
				return field.getValue();
		}
		
		return null;
	}
	
	/**
	 * @param fieldColumn - The column to change the field of
	 * @param value - The new value to set for the field
	 */
	protected void setField(MysqlColumn fieldColumn, Object value) {
		List<MysqlData> allFields = new ArrayList<MysqlData>(keyFields);
		allFields.addAll(liquidFields);
		
		for (MysqlData field : allFields) {
			if (field.getColumn().equals(fieldColumn)) {
				field.setValue(value);
				break;
			}
		}
	}

	/**
	 * @param column - The column that contains the field
	 */
	protected void addKeyField(MysqlColumn column, Object value) {
		addField(keyFields, column, value);
	}
	
	/**
	 * @param field - non-primary key data field
	 */
	protected void addLiquidField(MysqlColumn column, Object value) {
		addField(liquidFields, column, value);
	}
	
	private void addField(List<MysqlData> list, MysqlColumn column, Object value) {
		MysqlData field = new MysqlData(column);
		field.setValue(value);
		list.add(field);
	}
	
	/**
	 * Use the methods addKeyField(DataField) and addLiquidField(DataField)
	 * to add all of the relevant fields in this method.
	 * 
	 * Key fields will be used to identify this row in the data base table.
	 * These fields will not be changed by using this class.
	 * 
	 * Liquid fields are the fields that can be changed and play with.
	 */
	protected abstract void addFields();
}
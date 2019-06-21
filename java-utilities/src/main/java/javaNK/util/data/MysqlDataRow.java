package javaNK.util.data;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javaNK.util.debugging.Logger;

public abstract class MysqlDataRow
{
	protected static abstract class DataField
	{
		Object classMember;
		
		public DataField() {
			this.classMember = classMember();
		}
		
		/**
		 * Get the value as it's suppose to be written inside a query.
		 * Apostrophes will be added when needed. 
		 * 
		 * @return the value of the field as a String object, fit for any query.
		 */
		public String value() {
			Object value = classMember();
			String apostrophe = "'";
			String valueStr;
			
			if (classMember() instanceof String || classMember instanceof Timestamp)
				valueStr = apostrophe + value.toString() + apostrophe;
			else
				valueStr = value.toString();
			
			return valueStr;
		}
		
		/**
		 * @return the name of the field as written in the data base.
		 */
		public abstract String mysqlName();
		
		/**
		 * Get the actual variable that's a mutable and modifiable object.
		 * When calling the save() method, it's using the class members' values.
		 * 
		 * WARNING: You must provide an Object in order for the class to do it's job properly.
		 * 			If the data member is of primitive type, use wrapping instead!  
		 * 
		 * @return the variable that represents the data in the data base row.
		 */
		protected abstract Object classMember();
	}
	
	private List<DataField> keyFields, liquidFields;
	
	public MysqlDataRow() {
		this.keyFields = new ArrayList<DataField>();
		this.liquidFields = new ArrayList<DataField>();
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
		DataField tempField;
		
		if (isInDatabase()) {
			query = "UPDATE " + tableName() + " SET ";
			
			for (int i = 0; i < liquidFields.size(); i++) {
				tempField = liquidFields.get(i);
				query = query.concat(tempField.mysqlName() + " = " + tempField.value() + " ");
				
				if (i < liquidFields.size() - 1) query = query.concat(", ");
				else query = query.concat(keyCondition());
			}
		}
		else {
			query = "INSERT INTO " + tableName() + "(";
			int fieldsAmount;
			
			/*
			 * Parenthesis section
			 */
			fieldsAmount = keyFields.size() + liquidFields.size();
			
			//key fields
			for (DataField field : keyFields) {
				fieldsAmount--;
				query = query.concat(field.mysqlName());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") VALUES (");
			}
			
			//liquid fields
			for (DataField field : liquidFields) {
				fieldsAmount--;
				query = query.concat(field.mysqlName());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") VALUES (");
			}
			
			/*
			 * Values section
			 */
			fieldsAmount = keyFields.size() + liquidFields.size();
			
			//key fields
			for (DataField field : keyFields) {
				fieldsAmount--;
				query = query.concat(field.value());
				
				if (fieldsAmount > 0) query = query.concat(", ");
				else query = query.concat(") ");
			}
			
			//liquid fields
			for (DataField field : liquidFields) {
				fieldsAmount--;
				query = query.concat(field.value());
				
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
		String query = "DELETE FROM " + tableName() + " " + keyCondition();
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
					  	 + "	FROM " + tableName() + " "
					  	 + keyCondition() + ") AS key_exists";
			
			return MysqlModifier.readBOOLEAN(query, "key_exists");
		}
		catch (SQLException | NullPointerException e) { return false; }
	}
	
	/**
	 * @return a query that selects all of the columns for this row.
	 */
	protected String selectAllQuery() {
		return "SELECT * FROM " + tableName() + " " + keyCondition();
	}
	
	/**
	 * @return the condition that specifies this row's key values (the 'WHERE' segment).
	 */
	protected String keyCondition() {
		if (keyFields.isEmpty()) return "";
		else {
			String condition = "WHERE ";
			DataField tempField;
			
			for (int i = 0; i < keyFields.size(); i++) {
				tempField = keyFields.get(i);
				condition = condition.concat(tempField.mysqlName() + " = " + tempField.value());
				
				if (i < keyFields.size() - 1) condition = condition.concat(" AND ");
			}
			
			return condition;
		}
	}
	
	/**
	 * @return the name of the data base table this row belongs to.
	 */
	protected abstract String tableName();
	
	/**
	 * @param field - Primary key data field
	 */
	protected void addKeyField(DataField field) {
		keyFields.add(field);
	}
	
	/**
	 * @param field - non-primary key data field
	 */
	protected void addLiquidField(DataField field) {
		liquidFields.add(field);
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
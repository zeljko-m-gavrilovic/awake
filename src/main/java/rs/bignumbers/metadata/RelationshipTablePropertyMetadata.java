package rs.bignumbers.metadata;

import rs.bignumbers.annotations.FetchType;

public class RelationshipTablePropertyMetadata extends RelationshipForeignKeyPropertyMetadata {

	private String tableName;
	

	public RelationshipTablePropertyMetadata(String propertyName, String columnName, Class javaType, FetchType fetch,
			boolean responsible, String otherSidePropertyName, String otherSideColumnName, Class clazz, String tableName) {
		super(propertyName, columnName, javaType, fetch, responsible, otherSidePropertyName, otherSideColumnName, clazz);
		this.tableName = tableName;
		
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}

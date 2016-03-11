package rs.bignumbers.metadata;

import rs.bignumbers.annotations.FetchType;

public class RelationshipPropertyMetadata extends PropertyMetadata {
	private FetchType fetch;
	private boolean responsible;
	
	String columnName;
	String tableName;
	String otherSideColumnName;
	
	public RelationshipPropertyMetadata(String propertyName, String columnName, Class javaType, FetchType fetch, boolean responsible, String tableName, String otherSideColumnName) {
		this.propertyName = propertyName;
		this.columnName = columnName;
		this.javaType = javaType;
		this.fetch = fetch;
		this.responsible = responsible;
		this.tableName = tableName;
		this.otherSideColumnName = otherSideColumnName;
	}
	
	
	public FetchType getFetch() {
		return fetch;
	}
	public void setFetch(FetchType fetch) {
		this.fetch = fetch;
	}
	public boolean isResponsible() {
		return responsible;
	}
	public void setResponsible(boolean responsible) {
		this.responsible = responsible;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getOtherSideColumnName() {
		return otherSideColumnName;
	}
	public void setOtherSideColumnName(String otherSideColumnName) {
		this.otherSideColumnName = otherSideColumnName;
	}
	
}

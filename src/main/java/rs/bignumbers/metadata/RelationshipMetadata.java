package rs.bignumbers.metadata;

import rs.bignumbers.annotations.FetchType;

public class RelationshipMetadata extends PropertyMetadata {
	private FetchType fetch;
	private boolean responsible;
	private String columnName;
	private String otherSidePropertyName;
	private String otherSideColumnName;
	private Class entityClazz;
	private String tableName;

	public RelationshipMetadata(String propertyName, String columnName, Class javaType,
			FetchType fetch, boolean responsible, String otherSidePropertyName, String otherSideColumnName, Class entityClazz, String tableName) {
		this.propertyName = propertyName;
		this.columnName = columnName;
		this.javaType = javaType;
		this.fetch = fetch;
		this.responsible = responsible;
		this.otherSidePropertyName = otherSidePropertyName;
		this.otherSideColumnName = otherSideColumnName;
		this.entityClazz = entityClazz;
		this.tableName = tableName;
	}

	public boolean isJoinTableRelationship() {
		return getTableName().length() > 0;
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

	public String getOtherSidePropertyName() {
		return otherSidePropertyName;
	}

	public void setOtherSidePropertyName(String otherSidePropertyName) {
		this.otherSidePropertyName = otherSidePropertyName;
	}
	
	public String getOtherSideColumnName() {
		return otherSideColumnName;
	}

	public void setOtherSideColumnName(String otherSideColumnName) {
		this.otherSideColumnName = otherSideColumnName;
	}

	public Class getEntityClazz() {
		return entityClazz;
	}

	public void setEntityClazz(Class entityClazz) {
		this.entityClazz = entityClazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
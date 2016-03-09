package rs.bignumbers.metadata;

public class PropertyMetadata {

	private String propertyName;
	private String columnName;
	private Class javaType;
	private boolean foreignKey;

	public PropertyMetadata() {
	}

	public PropertyMetadata(String propertyName, String columnName, Class javaType, boolean foreignKey) {
		this.propertyName = propertyName;
		this.columnName = columnName;
		this.javaType = javaType;
		this.foreignKey = foreignKey;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Class getJavaType() {
		return javaType;
	}

	public void setJavaType(Class javaType) {
		this.javaType = javaType;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}
}
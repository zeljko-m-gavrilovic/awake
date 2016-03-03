package rs.bignumbers.util;

public class PropertyMetadata {

	private String propertyName;
	private String columnName;
	private Class javaType;

	public PropertyMetadata() {
	}

	public PropertyMetadata(String propertyName, String columnName, Class javaType) {
		this.propertyName = propertyName;
		this.columnName = columnName;
		this.javaType = javaType;
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

}

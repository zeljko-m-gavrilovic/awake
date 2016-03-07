package rs.bignumbers.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMetadata {
	Class clazz;
	String tableName;
	Map<String, PropertyMetadata> propertiesMetadata;

	public EntityMetadata() {
		propertiesMetadata = new HashMap<String, PropertyMetadata>();
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, PropertyMetadata> getPropertiesMetadata() {
		return propertiesMetadata;
	}

	public List<String> getColumns(boolean idAllowed) {
		List<String> columns = getPropertiesMetadata().values().stream().filter(pd -> {
			boolean idColumn = "id".equalsIgnoreCase(pd.getColumnName());
			return (idColumn && idAllowed) || (!idColumn);
		}).map(pd -> pd.getColumnName()).collect(Collectors.toList());
		return columns;
	}

	public void addPropertyMetadata(String prop, PropertyMetadata pm) {
		propertiesMetadata.put(prop, pm);
	}
}
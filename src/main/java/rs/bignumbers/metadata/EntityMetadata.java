package rs.bignumbers.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import rs.bignumbers.annotations.FetchType;

public class EntityMetadata {
	Class clazz;
	String tableName;
	Map<String, PropertyMetadata> propertiesMetadata;
	/*
	 * Map<String, RelationshipPropertyMetadata> relationshipPropertiesMetadata;
	 */

	public EntityMetadata() {
		propertiesMetadata = new HashMap<String, PropertyMetadata>();
		/*
		 * relationshipPropertiesMetadata = new HashMap<String,
		 * RelationshipPropertyMetadata>();
		 */
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

	/*
	 * public Map<String, RelationshipPropertyMetadata>
	 * getRelationshipPropertiesMetadata() { return
	 * relationshipPropertiesMetadata; }
	 */

	public List<PropertyMetadata> getResponsibleProperties() {
		List<PropertyMetadata> responsibleProperties = getPropertiesMetadata().values().stream().filter(pm -> {
			if ((pm instanceof RelationshipPropertyMetadata)) {
				RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) pm;
				return rpm.isResponsible();
			} else {
				return true;
			}
		}).collect(Collectors.toList());
		return responsibleProperties;
	}
	
	public Set<String> getResponsibleColumns() {
		return getResponsibleProperties().stream().map(pm -> pm.getColumnName()).collect(Collectors.toSet());
	}

	public List<PropertyMetadata> getLazyProperties() {
		List<PropertyMetadata> lazyProperties = getPropertiesMetadata().values().stream()
				.filter(pm -> pm instanceof RelationshipPropertyMetadata)
				.filter(pm -> FetchType.Lazy.equals(((RelationshipPropertyMetadata) pm).getFetch()))
				.collect(Collectors.toList());
		return lazyProperties;
	}

	public List<PropertyMetadata> getEagerProperties() {
		List<PropertyMetadata> eagerProperties = getPropertiesMetadata().values().stream()
				.filter(pm -> pm instanceof RelationshipPropertyMetadata)
				.filter(pm -> FetchType.Eager.equals(((RelationshipPropertyMetadata) pm).getFetch()))
				.collect(Collectors.toList());
		return eagerProperties;
	}

	public void addPropertyMetadata(String prop, PropertyMetadata pm) {
		propertiesMetadata.put(prop, pm);
	}

	/*
	 * public void addRelationshipPropertyMetadata(String prop,
	 * RelationshipPropertyMetadata rpm) {
	 * relationshipPropertiesMetadata.put(prop, rpm); }
	 */
}
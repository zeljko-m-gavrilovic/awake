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

	public EntityMetadata() {
		propertiesMetadata = new HashMap<String, PropertyMetadata>();
	}

	public List<PropertyMetadata> getResponsibleProperties() {
		List<PropertyMetadata> responsibleProperties = getPropertiesMetadata().values().stream().filter(pm -> {
			if ((pm instanceof RelationshipMetadata)) {
				RelationshipMetadata rpm = (RelationshipMetadata) pm;
				return rpm.isResponsible();
			} else {
				return true;
			}
		}).collect(Collectors.toList());
		return responsibleProperties;
	}

	public List<PropertyMetadata> getResponsibleJoinTableProperties() {
		List<PropertyMetadata> responsibleProperties = getPropertiesMetadata().values().stream()
				.filter(pm -> pm instanceof RelationshipMetadata)
				.filter(rm -> ((RelationshipMetadata) rm).isJoinTableRelationship())
				.filter(rm -> ((RelationshipMetadata) rm).isResponsible()).collect(Collectors.toList());
		return responsibleProperties;
	}

	public Set<String> getResponsibleColumns() {
		return getResponsibleProperties().stream().map(pm -> pm.getColumnName()).collect(Collectors.toSet());
	}

	public List<PropertyMetadata> getLazyRelationships() {
		List<PropertyMetadata> lazyRelationships = getPropertiesMetadata().values().stream()
				.filter(pm -> pm instanceof RelationshipMetadata)
				.filter(rm -> FetchType.Lazy.equals(((RelationshipMetadata) rm).getFetch()))
				.collect(Collectors.toList());
		return lazyRelationships;
	}

	public List<PropertyMetadata> getEagerRelationships() {
		List<PropertyMetadata> eagerRelationships = getPropertiesMetadata().values().stream()
				.filter(pm -> pm instanceof RelationshipMetadata)
				.filter(rm -> FetchType.Eager.equals(((RelationshipMetadata) rm).getFetch()))
				.collect(Collectors.toList());
		return eagerRelationships;
	}

	public void addPropertyMetadata(String prop, PropertyMetadata pm) {
		propertiesMetadata.put(prop, pm);
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

}
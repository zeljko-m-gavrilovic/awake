package rs.bignumbers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;

public class Configuration {
	private final Logger logger = LoggerFactory.getLogger(Configuration.this.getClass());
	
	private List<Class> entities;
	private Map<String, EntityMetadata> entityMetadatas;
	private MetadataExtractor metadataExtractor;
	
	public Configuration(List<Class> entities, MetadataExtractor metadataExtractor) {
		this.entities = entities;
		this.entityMetadatas = new HashMap<String, EntityMetadata>();
		this.metadataExtractor = metadataExtractor;
		this.extactMetadata();
	}
	
	private void extactMetadata() {
		for (Class clazz : entities) {
			EntityMetadata em = metadataExtractor.extractMetadataForClass(clazz);
			entityMetadatas.put(clazz.getName(), em);
		}
	}
	
	public EntityMetadata getEntityMetadata(Class clazz) {
		if (clazz.getName().contains("$$EnhancerByCGLIB$$")) {
			clazz = clazz.getSuperclass();
		}
		EntityMetadata entityMetadata = entityMetadatas.get(clazz.getName());
		if(entityMetadata == null) {
			logger.error("Entity metadata can't be found fot the class {}. Please check if you set the appropriate annotation or if you register class as an entity", clazz);
		}
		return entityMetadata;
	}

	public List<Class> getEntities() {
		return entities;
	}
	public Map<String, EntityMetadata> getEntityMetadatas() {
		return entityMetadatas;
	}
}
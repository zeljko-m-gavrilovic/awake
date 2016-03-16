package rs.bignumbers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;

public class Configuration {
	private final Logger logger = LoggerFactory.getLogger(Configuration.this.getClass());
	
	private Map<String, EntityMetadata> entityMetadatas;
	
	public Configuration(MetadataExtractor metadataExtractor) {
		this.entityMetadatas = new HashMap<String, EntityMetadata>();
		this.entityMetadatas = metadataExtractor.getEntityMetadata();
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

	public Map<String, EntityMetadata> getEntityMetadatas() {
		return entityMetadatas;
	}
}
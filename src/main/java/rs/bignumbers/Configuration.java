package rs.bignumbers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.bignumbers.metadata.AnnotationMetadataExtractor;
import rs.bignumbers.metadata.EntityMetadata;

public class Configuration {
	private final Logger logger = LoggerFactory.getLogger(Configuration.this.getClass());

	private Map<String, EntityMetadata> entityMetadatas;

	public Configuration(AnnotationMetadataExtractor metadataExtractor) {
		this.entityMetadatas = metadataExtractor.extractMetadata();
	}

	public EntityMetadata getEntityMetadata(Class clazz) {
		if (clazz.getName().contains("$$EnhancerByCGLIB$$")) {
			clazz = clazz.getSuperclass();
		}
		EntityMetadata entityMetadata = entityMetadatas.get(clazz.getName());
		if (entityMetadata == null) {
			logger.error(
					"Entity metadata for the class {} can't be found in the configuration. "
					+ "Please check if you set the appropriate annotation on the class or if you register the class as an entity in the configuration",
					clazz);
		}
		return entityMetadata;
	}
}
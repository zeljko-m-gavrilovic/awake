package rs.bignumbers.metadata;

import java.util.Map;

public interface MetadataExtractor {
	public Map<String, EntityMetadata> getEntityMetadata();
}
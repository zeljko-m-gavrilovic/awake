package rs.bignumbers;

import org.junit.Assert;
import org.junit.Test;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.properties.model.Man;

public class TestMetadataExtractor {

	@Test
	public void testAnnotationBasedMetadataExtractor() {
		AnnotationBasedMetadataExtractor me = new AnnotationBasedMetadataExtractor();
		EntityMetadata m = me.extractMetadataForClass(Man.class);
		Assert.assertEquals(Man.class, m.getClazz());
		Assert.assertEquals(Man.class.getSimpleName().toLowerCase(), m.getTableName().toLowerCase());
		Assert.assertNotNull(m.getPropertiesMetadata());
		Assert.assertEquals(4, m.getPropertiesMetadata().entrySet().size());
		Assert.assertEquals(m.getPropertiesMetadata().get("firstName").getColumnName(), "first_name");
		Assert.assertFalse(m.getPropertiesMetadata().containsKey("lastName")); // ignore=true
		Assert.assertEquals(m.getPropertiesMetadata().get("age").getColumnName(), "age");
		Assert.assertFalse(m.getPropertiesMetadata().containsKey("place"));
		Assert.assertEquals(m.getPropertiesMetadata().get("girlName").getColumnName(), "girlName");
	}
}
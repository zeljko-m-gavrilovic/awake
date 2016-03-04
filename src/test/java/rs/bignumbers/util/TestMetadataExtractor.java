package rs.bignumbers.util;

import org.junit.Assert;
import org.junit.Test;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AwakeApplication.class)
public class TestMetadataExtractor {

	@Test
	public void testExtractOneClass() {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata m = me.extractMetadataForClass(Man.class);
		Assert.assertEquals(Man.class, m.getClazz());
		Assert.assertEquals(Man.class.getSimpleName().toLowerCase(), m.getTableName().toLowerCase());
		Assert.assertNotNull(m.getPropertiesMetadata());
		Assert.assertEquals(3, m.getPropertiesMetadata().entrySet().size());
		Assert.assertEquals(m.getPropertiesMetadata().get("firstName").getColumnName(), "first_name");
		Assert.assertFalse(m.getPropertiesMetadata().containsKey("lastName")); // ignore=true
		Assert.assertEquals(m.getPropertiesMetadata().get("age").getColumnName(), "age");
		Assert.assertFalse(m.getPropertiesMetadata().containsKey("place"));
		Assert.assertEquals(m.getPropertiesMetadata().get("girlName").getColumnName(), "girlName");
	}
}
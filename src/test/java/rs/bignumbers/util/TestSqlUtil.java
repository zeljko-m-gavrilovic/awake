package rs.bignumbers.util;

import org.junit.Assert;
import org.junit.Test;

public class TestSqlUtil {

	@Test
	public void testInsert() {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(Man.class);
		
		SqlUtil sqlUtil = new SqlUtil();
		String sqlInsert = sqlUtil.insert(em);
		Assert.assertEquals("INSERT INTO man(girlName, first_name, age) VALUES(:girlName, :first_name, :age)", sqlInsert);
	}
	
	@Test
	public void testUpdate() {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(Man.class);
		
		SqlUtil sqlUtil = new SqlUtil();
		String sqlUpdate = sqlUtil.update(em, new String[] {"girlName", "firstName", "age"}, "id");
		Assert.assertEquals("UPDATE man SET girlName = :girlName, first_name = :first_name, age = :age WHERE id = :id", sqlUpdate);
	}
}

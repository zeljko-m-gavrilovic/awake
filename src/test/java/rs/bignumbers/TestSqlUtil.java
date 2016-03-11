package rs.bignumbers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.properties.model.Man;
import rs.bignumbers.util.SqlUtil;

public class TestSqlUtil {

	private EntityMetadata em;
	private SqlUtil sqlUtil;
	
	@Before
	public void setUp() {
		AnnotationBasedMetadataExtractor me = new AnnotationBasedMetadataExtractor();
		em = me.extractMetadataForClass(Man.class);
		sqlUtil = new SqlUtil();
	}
	
	
	@Test
	public void testInsert() {
		String sqlInsert = sqlUtil.insert(em);
		Assert.assertEquals("INSERT INTO man(girlName, first_name, age) VALUES(:girlName, :first_name, :age)",
				sqlInsert);
	}

	@Test
	public void testUpdate() {
		Set<String> properties = new HashSet<String>();
		properties.add("girlName");
		properties.add("firstName");
		properties.add("age");
		String sqlUpdate = sqlUtil.update(em, properties, "id");
		Assert.assertEquals("UPDATE man SET girlName = :girlName, first_name = :first_name, age = :age WHERE id = :id",
				sqlUpdate);
	}
	
	@Test
	public void testQuery() {
		Map<String, Object> whereKeys = new HashMap<String, Object>();
		whereKeys.put("firstName", "Zeljko");
		whereKeys.put("age", 35);
		String sqlQuery = sqlUtil.query(em, whereKeys.keySet());
		Assert.assertEquals("SELECT * FROM man WHERE first_name = :first_name AND age = :age", sqlQuery);
	}

	@Test
	public void testDelete() {
		String sqlDelete = sqlUtil.delete(em);
		Assert.assertEquals("DELETE FROM man WHERE id = :id", sqlDelete);
	}
}
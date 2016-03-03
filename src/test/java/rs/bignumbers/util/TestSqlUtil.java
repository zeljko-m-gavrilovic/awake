package rs.bignumbers.util;

import org.junit.Assert;
import org.junit.Test;

public class TestSqlUtil {

	@Test
	public void testInsert() {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata m = me.extractMetadataForClass(Man.class);
		
		SqlUtil sqlUtil = new SqlUtil();
		String sqlInsert = sqlUtil.generateInsert(m);
		Assert.assertEquals("INSERT INTO Man(girlName,firstName,lastName,age) VALUES(?,?,?,?)", sqlInsert);
	}
}

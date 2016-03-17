package rs.bignumbers.transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rs.bignumbers.util.SqlUtil;

public class TestSqlUtil {

	private SqlUtil sqlUtil;

	@Before
	public void setUp() {
		sqlUtil = new SqlUtil();
	}

	@Test
	public void testInsert() {
		Set<String> columns = new HashSet<String>();
		columns.add("girlName");
		columns.add("first_name");
		columns.add("age");
		String sqlInsert = sqlUtil.insert("man", columns);
		Assert.assertEquals("INSERT INTO man(girlName, first_name, age) VALUES(:girlName, :first_name, :age)",
				sqlInsert);
	}

	@Test
	public void testUpdate() {
		Set<String> columns = new HashSet<String>();
		columns.add("girlName");
		columns.add("first_name");
		columns.add("age");
		String sqlUpdate = sqlUtil.update("man", columns, "id");
		Assert.assertEquals("UPDATE man SET girlName = :girlName, first_name = :first_name, age = :age WHERE id = :id",
				sqlUpdate);
	}

	@Test
	public void testQuery() {
		Map<String, Object> whereColumns = new HashMap<String, Object>();
		whereColumns.put("first_name", "Zeljko");
		whereColumns.put("age", 35);
		String sqlQuery = sqlUtil.query("man", whereColumns.keySet());
		Assert.assertEquals("SELECT * FROM man WHERE first_name = :first_name AND age = :age", sqlQuery);
	}

	@Test
	public void testDelete() {
		String sqlDelete = sqlUtil.delete("man");
		Assert.assertEquals("DELETE FROM man WHERE id = :id", sqlDelete);
	}
}
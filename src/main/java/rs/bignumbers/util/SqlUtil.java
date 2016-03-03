package rs.bignumbers.util;

import java.util.ArrayList;
import java.util.List;

public class SqlUtil {

	public String generateInsert(EntityMetadata m) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(m.getTableName()).append("(");
		for (String c : m.getPropertiesMetadata().keySet()) {
			sql.append(c).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));

		sql.append(") VALUES(");
		for (int i = 0; i < m.getPropertiesMetadata().size(); i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));

		sql.append(")");
		return sql.toString();
	}
	
	public String generateQuery(EntityMetadata m) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * INTO ").append(m.getTableName()).append("(");
		for (String c : m.getPropertiesMetadata().keySet()) {
			sql.append(c).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));

		sql.append(") VALUES(");
		for (int i = 0; i < m.getPropertiesMetadata().size(); i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));

		sql.append(")");
		return sql.toString();
	}
	
	public static void main(String[] args) {
		List l = new ArrayList();
		l.add("abc");
		l.add(new Integer(3));
		
		for(Object e : l) {
			System.out.println("e is " + e);
		}
	}
}
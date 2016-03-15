package rs.bignumbers.util;

import java.util.Set;

public class SqlUtil {

	public String insert(String tableName, Set<String> columnNames) {
		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ");
		insert.append(tableName);
		insert.append("(");

		StringBuilder update = new StringBuilder();
		update.append(") VALUES(");

		boolean first = true;
		for (String columnName : columnNames) {
			if ("id".equalsIgnoreCase(columnName)) {
				continue;
			}
			if (!first) {
				insert.append(", ");
				update.append(", ");
			}
			first = false;
			insert.append(columnName);
			update.append(":");
			update.append(columnName);
		}
		insert.append(update);
		insert.append(")");
		return insert.toString();
	}

	public String update(String tableName, Set<String> columnNames, String... whereColumns) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
		sqlBuilder.append(tableName);
		sqlBuilder.append(" SET ");
		boolean first = true;
		for (String columnName : columnNames) {
			if ("id".equalsIgnoreCase(columnName)) {
				continue;
			}
			if (!first) {
				sqlBuilder.append(", ");
			}
			first = false;
			sqlBuilder.append(columnName);
			sqlBuilder.append(" = :");
			sqlBuilder.append(columnName);
		}

		first = true;
		for (String columnName : whereColumns) {
			if (first) {
				sqlBuilder.append(" WHERE ");
			} else {
				sqlBuilder.append(" AND ");
			}
			first = false;
			sqlBuilder.append(columnName);
			sqlBuilder.append(" = :");
			sqlBuilder.append(columnName);
		}
		return sqlBuilder.toString();
	}

	public String delete(String tableName) {
		String sql = "DELETE FROM " + tableName + " WHERE id = :id";
		return sql;
	}

	public String query(String tableName, Set<String> whereColumns) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ");
		sql.append(tableName);

		boolean first = true;
		for (String columnName : whereColumns) {
			if (first) {
				sql.append(" WHERE ");
				first = false;
			} else {
				sql.append(" AND ");
			}
			sql.append(columnName);
			sql.append(" = :");
			sql.append(columnName);
		}
		return sql.toString();
	}
	
	public String queryIn(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ");
		sql.append(tableName);
		sql.append("WHERE id in (:ids)");

		return sql.toString();
	}
}
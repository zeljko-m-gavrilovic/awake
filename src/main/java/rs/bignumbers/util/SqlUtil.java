package rs.bignumbers.util;

import java.util.Set;

import rs.bignumbers.metadata.EntityMetadata;

public class SqlUtil {

	public String insert(EntityMetadata m) {
		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ");
		insert.append(m.getTableName());
		insert.append("(");
		
		StringBuilder update = new StringBuilder();
		update.append(") VALUES(");
		
		boolean first = true;
		for (String c : m.getColumns(false)) {
			if(!first) {
				insert.append(", ");
				update.append(", ");
			}
			first = false;
			insert.append(c);
			//update.append(c);
            update.append(":");
            update.append(c);
		}
		insert.append(update);
		insert.append(")");
		return insert.toString();
	}
	
	public String update(EntityMetadata em, Set<String> properties,  String... whereKeys) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(em.getTableName());
        sqlBuilder.append(" SET ");
        boolean first = true;
        for (String propertyName : properties) {
            String columnName = em.getPropertiesMetadata().get(propertyName).getColumnName(); 
        	if (!first) {
                sqlBuilder.append(", ");
            }
            first = false;
            sqlBuilder.append(columnName);
            sqlBuilder.append(" = :");
            sqlBuilder.append(columnName);
        }


        first = true;
        for (String key : whereKeys) {
            if (first) {
                sqlBuilder.append(" WHERE ");
            } else {
                sqlBuilder.append(" AND ");
            }
            first = false;
            sqlBuilder.append(key);
            sqlBuilder.append(" = :");
            sqlBuilder.append(key);
        }
        return sqlBuilder.toString();
    }
	
	public String delete(EntityMetadata em) {
		String sql = "DELETE FROM " + em.getTableName() + " WHERE id = :id";
		return sql;
	}
	
	public String query(EntityMetadata em, Set<String> whereKeys) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ");
		sql.append(em.getTableName());

		boolean first = true;
        for (String key : whereKeys) {
            if (first) {
                sql.append(" WHERE ");
                first = false;
            } else {
                sql.append(" AND ");
            }
            String columnName = em.getPropertiesMetadata().get(key).getColumnName();
            sql.append(columnName);
            sql.append(" = :");
            sql.append(columnName);
        }
        return sql.toString();
	}
}
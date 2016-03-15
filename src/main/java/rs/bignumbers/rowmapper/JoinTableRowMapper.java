package rs.bignumbers.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class JoinTableRowMapper implements RowMapper<Long> {

	private String columnName;
	
	public JoinTableRowMapper(String columnName) {
		this.columnName = columnName;
	}
	
	@Override
	public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Long(rs.getLong(columnName));
	}
}
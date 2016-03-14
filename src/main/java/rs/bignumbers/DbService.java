package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class DbService {

	private JdbcTemplate jdbcTemplate;

	public DbService(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Long insert(String sql, Map<String, Object> columns) {
		MapSqlParameterSource parametersMap = new MapSqlParameterSource(columns);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parametersMap, keyHolder);
		Long pk = keyHolder.getKey().longValue();
		return pk;

	}

	public void update(String sql, Map<String, Object> columns)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		MapSqlParameterSource parameters = new MapSqlParameterSource(columns);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parameters);
	}

	public void delete(String sql, Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parameters);
	}

	public <T> T findOne(String sql, Long id, RowMapper<T> rowMapper) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("id", id);
		
		List<T> all = findList(sql, where, rowMapper);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> findList(String sql, Map<String, Object> whereColumns, RowMapper<T> rowMapper) {
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return npJdbcTemplate.query(sql, whereColumns, rowMapper);
	}
}
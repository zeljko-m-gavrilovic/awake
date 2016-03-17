package rs.bignumbers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class DbService {
	private final Logger logger = LoggerFactory.getLogger(DbService.this.getClass());

	private JdbcTemplate jdbcTemplate;

	public DbService(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Long insert(String sql, Map<String, Object> columns) {
		MapSqlParameterSource parametersMap = new MapSqlParameterSource(columns);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		logger.debug("sql insert: {} with parameters: {}", sql, columns.toString());
		npJdbcTemplate.update(sql, parametersMap, keyHolder);
		Long pk = keyHolder.getKey().longValue();
		return pk;

	}

	public void update(String sql, Map<String, Object> columns) {
		MapSqlParameterSource parameters = new MapSqlParameterSource(columns);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		logger.debug("sql update: {} with parameters: {}", sql, columns.toString());
		npJdbcTemplate.update(sql, parameters);
	}

	public void delete(String sql, Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		logger.debug("sql delete: {} with id: {}", sql, id);
		npJdbcTemplate.update(sql, parameters);
	}

	public void delete(String sql, Map<String, Object> whereColumns) {
		MapSqlParameterSource parameters = new MapSqlParameterSource(whereColumns);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		logger.debug("sql delete: {} with parameters: {}", sql, whereColumns.toString());
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
		logger.debug("sql query one: {} with id: {}", sql, id);
		return one;
	}

	public <T> List<T> findList(String sql, Map<String, Object> whereColumns, RowMapper<T> rowMapper) {
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		logger.debug("sql query list: {} with parameters: {}", sql, whereColumns.toString());
		return npJdbcTemplate.query(sql, whereColumns, rowMapper);
	}
}
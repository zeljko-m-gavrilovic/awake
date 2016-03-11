package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipPropertyMetadata;
import rs.bignumbers.util.ProxyRegister;

public class DbService {

	private JdbcTemplate jdbcTemplate;

	public DbService(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Long insert(String sql, Map<String, Object> parameters) {
		MapSqlParameterSource parametersMap = new MapSqlParameterSource(parameters);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parametersMap, keyHolder);
		Long pk = keyHolder.getKey().longValue();
		return pk;

	}

	public void update(String sql, Map<String, Object> dirtyProperties)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		MapSqlParameterSource parameters = new MapSqlParameterSource(dirtyProperties);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parameters);
	}

	public void delete(String sql, Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parameters);
	}

	public <T> T findOne(EntityMetadata em, String sql, Long id) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("id", id);
		
		List<T> all = findList(em, sql, where);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> findList(EntityMetadata em, String sql, Map<String, Object> whereParams) {
		if (whereParams == null) {
			whereParams = new HashMap<String, Object>();
		}
		MapSqlParameterSource parameters = new MapSqlParameterSource(whereParams);
		
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return npJdbcTemplate.query(sql, parameters.getValues(), new RowMapper<T>() {

			public T mapRow(ResultSet rs, int arg1) throws SQLException {

				DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
				T proxy = (T) ProxyFactory.newProxyInstance(em.getClazz(), interceptor);
				interceptor.setTarget(proxy);

				for (String propertyName : em.getPropertiesMetadata().keySet()) {
					PropertyMetadata pm = em.getPropertiesMetadata().get(propertyName);
					Object value = null;
					if (pm.getJavaType().isAssignableFrom(String.class)) {
						value = rs.getString(pm.getColumnName());
					}
					if (pm.getJavaType().isAssignableFrom(Integer.class)) {
						value = rs.getInt(pm.getColumnName());
					}
					if (pm.getJavaType().isAssignableFrom(Long.class)) {
						value = rs.getLong(pm.getColumnName());
					}
					try {
						PropertyUtils.setProperty(proxy, propertyName, value);
					} catch (Exception e) {
						throw new RuntimeException("Property utils exception can't set property " + propertyName
								+ " of the proxy instance class " + em.getClazz().getSimpleName());
					}
				}
				
				ProxyRegister.addInterceptor(proxy.getClass().getName() + "/" + rs.getLong("id"),
						interceptor);
				interceptor.setTrack(true);
				return proxy;
			}

		});
	}
}
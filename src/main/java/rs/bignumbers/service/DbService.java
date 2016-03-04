package rs.bignumbers.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import rs.bignumbers.DirtyValueInterceptor;
import rs.bignumbers.util.EntityMetadata;
import rs.bignumbers.util.MetadataExtractor;
import rs.bignumbers.util.PropertyMetadata;
import rs.bignumbers.util.ProxyFactory;
import rs.bignumbers.util.ProxyPool;
import rs.bignumbers.util.SqlUtil;

@Service
public class DbService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Long insert(Object o) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(o.getClass());
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		insert.withTableName(em.getTableName());
		String[] columns = em.getColumns().toArray(new String[em.getColumns().size()]);
		insert.usingColumns(columns);
		insert.usingGeneratedKeyColumns("id");

		try {
			Long pk = insert.executeAndReturnKey(new BeanPropertySqlParameterSource(o)).longValue();
			PropertyUtils.setProperty(o, "id", pk);
			return pk;
		} catch (Exception e) {
			throw new RuntimeException(
					"can't map properties for object " + o.toString() + ". Reason: " + e.getMessage());
		}
	}

	public void insertNoKeyReturned(Object o) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(o.getClass());
		SqlUtil su = new SqlUtil();
		String sql = su.insert(em);
		SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(o);
		jdbcTemplate.update(sql, parameterSource);
	}

	public void update(Object o, String[] updatedProperties) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(o.getClass());
		SqlUtil su = new SqlUtil();
		String sql = su.update(em, updatedProperties, "id");
		SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(o);

		jdbcTemplate.update(sql, parameterSource);
	}

	public void delete(Class clazz, String id) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(clazz);
		SqlUtil su = new SqlUtil();
		String sql = su.delete(em);

		jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
	}

	public <T> T getOne(Class<T> clazz, Long id) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(clazz);
		SqlUtil su = new SqlUtil();

		MapSqlParameterSource where = new MapSqlParameterSource();
		where.addValue("id", id);
		String sql = su.query(em, where.getValues().keySet());

		List<T> all = getAll(sql, where, clazz);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> getAll(String sql, MapSqlParameterSource parameters, Class<T> clazz) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(clazz);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return npJdbcTemplate.query(sql, parameters.getValues(), new RowMapper<T>() {

			public T mapRow(ResultSet rs, int arg1) throws SQLException {

				DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
				T proxy = ProxyFactory.newProxyInstance(clazz, interceptor);
				interceptor.setTarget(proxy);

				try {
					PropertyUtils.setProperty(proxy, "id", rs.getLong("id"));
					ProxyPool.addInterceptor(clazz.getName() + "/" + rs.getLong("id"), interceptor);
				} catch (Exception e) {
					throw new RuntimeException("Property utils exception can't set property id"
							+ " of the proxy instance class " + clazz.getSimpleName());
				}

				for (String propertyName : em.getPropertiesMetadata().keySet()) {
					PropertyMetadata pm = em.getPropertiesMetadata().get(propertyName);
					Object value = null;
					if (pm.getJavaType().isAssignableFrom(String.class)) {
						value = rs.getString(pm.getColumnName());
					}
					if (pm.getJavaType().isAssignableFrom(Integer.class)) {
						value = rs.getInt(pm.getColumnName());
					}
					try {
						PropertyUtils.setProperty(proxy, propertyName, value);
					} catch (Exception e) {
						throw new RuntimeException("Property utils exception can't set property " + propertyName
								+ " of the proxy instance class " + clazz.getSimpleName());
					}
				}
				return proxy;
			}

		});
	}
}
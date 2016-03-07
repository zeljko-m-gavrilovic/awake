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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.util.ProxyRegister;
import rs.bignumbers.util.SqlUtil;

//@Service
public class DbService {

	//@Autowired
	private JdbcTemplate jdbcTemplate;
	private MetadataExtractor me;
	private SqlUtil su;

	public DbService(DataSource dataSource) {
		me = new MetadataExtractor();
		su = new SqlUtil();
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//@Autowired
	/*public void setDataSource(DataSource dataSource) {
       this.jdbcTemplate = new JdbcTemplate(dataSource);
    }*/
	 
	public Long insert(Object o, EntityMetadata em) {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		insert.withTableName(em.getTableName());
		List<String> columns = em.getColumns(false);
		String[] columnsArray = columns.toArray(new String[columns.size()]);
		insert.usingColumns(columnsArray);
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

	public void insertNoKeyReturned(Object o, EntityMetadata em) {
		String sql = su.insert(em);
		SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(o);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, parameterSource);
	}

	public void update(Object o, Map<String, Object> updatedProperties, EntityMetadata em) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String sql = su.update(em, updatedProperties.keySet(), "id");
		
		MapSqlParameterSource updatedPropertiesSqlMap = new MapSqlParameterSource(updatedProperties);
		updatedPropertiesSqlMap.addValue("id", PropertyUtils.getProperty(o, "id"));
		for(String property : updatedProperties.keySet()) {
			String columnName = em.getPropertiesMetadata().get(property).getColumnName();
			updatedPropertiesSqlMap.addValue(columnName, updatedProperties.get(property));
		}
		
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, updatedPropertiesSqlMap);
	}

	public void delete(EntityMetadata em, Long id) {
		String sql = su.delete(em);
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		npJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
	}

	public <T> T findOne(EntityMetadata em, Long id) {
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("id", id);

		List<T> all = findList(em, where);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> findList(EntityMetadata em, Map<String, Object> whereParams) {
		String sqlQuery = su.query(em, whereParams.keySet());
		if(whereParams == null) {
			whereParams = new HashMap<String, Object>();
		}
		MapSqlParameterSource whereParameters = new MapSqlParameterSource();
		for(String propertyName: whereParams.keySet()) {
			String columnName = em.getPropertiesMetadata().get(propertyName).getColumnName();
			whereParameters.addValue(columnName, whereParams.get(propertyName));
		}
		NamedParameterJdbcTemplate npJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		return npJdbcTemplate.query(sqlQuery, whereParameters.getValues(), new RowMapper<T>() {

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
				ProxyRegister.addInterceptor(/*em.getClazz()*/proxy.getClass().getName() + "/" + rs.getLong("id"), interceptor);
				interceptor.setTrack(true);
				return proxy;
			}

		});
	}
}
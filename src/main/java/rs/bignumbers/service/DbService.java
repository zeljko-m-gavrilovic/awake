package rs.bignumbers.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import rs.bignumbers.DirtyValueInterceptor;
import rs.bignumbers.model.Person;
import rs.bignumbers.util.EntityMetadata;
import rs.bignumbers.util.MetadataExtractor;
import rs.bignumbers.util.PropertyMetadata;
import rs.bignumbers.util.ProxyFactory;
import rs.bignumbers.util.SqlUtil;

@Service
public class DbService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Long insert(Object o) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(o.getClass());
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		insert.withTableName(em.getTableName()).usingGeneratedKeyColumns("id");
		
		final Map<String, Object> parameters = new HashMap<>();
		try {
			for (String propertyName : em.getPropertiesMetadata().keySet()) {
				PropertyMetadata pm = em.getPropertiesMetadata().get(propertyName);
				parameters.put(pm.getColumnName(), PropertyUtils.getProperty(o, pm.getPropertyName()));
			}
			Long pk = insert.executeAndReturnKey(parameters).longValue();
			PropertyUtils.setProperty(o, "id", pk);
			return pk;
		} catch (Exception e) {
			throw new RuntimeException("can't map properties for object " + o.toString() + ". Reason: " + e.getMessage());
		}
		
		/*SqlUtil sqlUtil = new SqlUtil();
		String sql = sqlUtil.generateInsert(m);
		return jdbcTemplate.update(sql, m.getColumns().values());*/
	}

/*	public int insertPerson(Person person) {
		String sql = "INSERT INTO person(first_name, last_name, age, place) VALUES(?,?,?,?)";
		return jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getAge(),
				person.getPlace());
	}

	public int updatePerson(Person person) {
		String sql = "UPDATE person SET first_name=?, last_name=?, age=?, place=?";
		return jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getAge(),
				person.getPlace());
	}

	public int deletePerson(Long id) {
		String sql = "DELETE person WHERE id=?";
		return jdbcTemplate.update(sql, id);
	}
*/

	public Person getOne(Long id) {
		Person one = null;
		List<Person> all = getAll(" WHERE id = " + String.valueOf(id), Person.class);
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> getAll(String where, Class<T> clazz) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(clazz);

		return jdbcTemplate.query("SELECT * FROM " + em.getTableName() + where, new RowMapper<T>() {

			public T mapRow(ResultSet rs, int arg1) throws SQLException {

				DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
				T proxy = ProxyFactory.newProxyInstance(clazz, interceptor);

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
				/*
				 * p.setAge(rs.getInt("age"));
				 * p.setFirstName(rs.getString("first_name"));
				 * p.setLastName(rs.getString("last_name"));
				 * p.setPlace(rs.getString("place"));
				 */
				return proxy;
			}

		});
	}
}

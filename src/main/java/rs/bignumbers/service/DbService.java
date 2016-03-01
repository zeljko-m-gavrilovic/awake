package rs.bignumbers.service;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import rs.bignumbers.annotations.DbColumn;
import rs.bignumbers.annotations.DbTable;
import rs.bignumbers.model.Person;

@Service
public class DbService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(Object o) {
		if (o.getClass().isAnnotationPresent(DbTable.class)) {
			DbTable tableAnnotation = o.getClass().getAnnotation(DbTable.class);
			String table = tableAnnotation.name();
			if (table.length() == 0) {
				table = o.getClass().getSimpleName();
			}
			List<String> columns = new ArrayList<String>();
			for (Field f : o.getClass().getFields()) {
				if (f.isAnnotationPresent(DbColumn.class)) {
					DbColumn columnAnnotation = f.getAnnotation(DbColumn.class);
					String columnName = columnAnnotation.name();
					if (columnName.length() == 0) {
						columnName = f.getName();
					}
				}
			}
			String sql = "INSERT INTO person(first_name, last_name, age, place) VALUES(?,?,?,?)";
			return jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getAge(),
					person.getPlace());
		}
	}

	public int insertPerson(Person person) {
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

	public Person getOnePerson(Long id) {
		Person one = null;
		List<Person> all = getAllPerson("WHERE id =" + String.valueOf(id));
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public List<Person> getAllPerson(String where) {
		return jdbcTemplate.query("SELECT * FROM person " + where, new RowMapper<Person>() {

			public Person mapRow(ResultSet rs, int arg1) throws SQLException {
				Person p = new Person();
				p.setAge(rs.getInt("age"));
				p.setFirstName(rs.getString("first_name"));
				p.setLastName(rs.getString("last_name"));
				p.setPlace(rs.getString("place"));
				return p;
			}

		});
	}
}

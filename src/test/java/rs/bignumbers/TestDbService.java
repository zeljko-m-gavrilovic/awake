package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;
import rs.bignumbers.properties.model.Person;
import rs.bignumbers.util.SqlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestDbService {

	private DbService dbService;

	@Autowired
	private DataSource dataSource;
	private MetadataExtractor metadataExtractor;
	private SqlUtil sqlUtil;

	@Before
	public void setUp() {
		this.metadataExtractor = new MetadataExtractor();
		this.dbService = new DbService(dataSource);
		sqlUtil = new SqlUtil();
	}

	@Test
	public void testQueryOne() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata entityMetadata = metadataExtractor.extractMetadataForClass(Person.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.put(columnName, PropertyUtils.getProperty(p, propertyName));
		}

		String sql = sqlUtil.insert(entityMetadata);
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereKeys = new HashSet<String>();
		whereKeys.add("id");
		String querySql = sqlUtil.query(entityMetadata, whereKeys);
		Person dbPerson = dbService.findOne(entityMetadata, querySql, pk);
		Assert.assertNotNull(dbPerson);

		String deleteQuery = sqlUtil.delete(entityMetadata);
		dbService.delete(deleteQuery, pk);
	}

	@Test
	public void testQueryList() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata entityMetadata = metadataExtractor.extractMetadataForClass(Person.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.put(columnName, PropertyUtils.getProperty(p, propertyName));
		}

		String sql = sqlUtil.insert(entityMetadata);
		Long pk1 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk1);

		Long pk2 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk2);

		Long pk3 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk3);

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("firstName", "Zeljko");
		String querySql = sqlUtil.query(entityMetadata, where.keySet());
		
		Map<String, Object> params = new HashMap<String, Object>();
		for (String propertyName : where.keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			params.put(columnName, where.get(propertyName));
		}
		List<Person> persons = dbService.findList(entityMetadata, querySql, params);
		Assert.assertNotNull(persons);
		Assert.assertEquals(3, persons.size());

		String deleteQuery = sqlUtil.delete(entityMetadata);
		dbService.delete(deleteQuery, pk1);
		dbService.delete(deleteQuery, pk2);
		dbService.delete(deleteQuery, pk3);
	}

	@Test
	public void testInsert() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata entityMetadata = metadataExtractor.extractMetadataForClass(Person.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.put(columnName, PropertyUtils.getProperty(p, propertyName));
		}

		String sql = sqlUtil.insert(entityMetadata);
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereKeys = new HashSet<String>();
		whereKeys.add("id");
		String querySql = sqlUtil.query(entityMetadata, whereKeys);
		Person dbPerson = dbService.findOne(entityMetadata, querySql, pk);
		Assert.assertNotNull(dbPerson);

		String deleteQuery = sqlUtil.delete(entityMetadata);
		dbService.delete(deleteQuery, pk);
	}

	@Test
	public void testUpdate() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata entityMetadata = metadataExtractor.extractMetadataForClass(Person.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.put(columnName, PropertyUtils.getProperty(p, propertyName));
		}

		String sql = sqlUtil.insert(entityMetadata);
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereKeys = new HashSet<String>();
		whereKeys.add("id");
		String querySql = sqlUtil.query(entityMetadata, whereKeys);
		Person dbPerson = dbService.findOne(entityMetadata, querySql, pk);
		Assert.assertNotNull(dbPerson);

		String firstNameUpdated = "updated name";
		dbPerson.setFirstName(firstNameUpdated);
		Map<String, Object> updatedProperties = new HashMap<String, Object>();
		updatedProperties.put("firstName", firstNameUpdated);
		
		String updateSql = sqlUtil.update(entityMetadata, updatedProperties.keySet(), "id");
		
		Map<String, Object> params = new HashMap<String, Object>();
		for (String propertyName : updatedProperties.keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			params.put(columnName, updatedProperties.get(propertyName));
		}
		params.put("id", pk);
		dbService.update(updateSql, params);
		dbPerson = dbService.findOne(entityMetadata, querySql, pk);
		Assert.assertNotNull(dbPerson);
		Assert.assertEquals(firstNameUpdated, dbPerson.getFirstName());

		String deleteQuery = sqlUtil.delete(entityMetadata);
		dbService.delete(deleteQuery, pk);
	}

	@Test
	public void testDelete() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata entityMetadata = metadataExtractor.extractMetadataForClass(Person.class);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.put(columnName, PropertyUtils.getProperty(p, propertyName));
		}

		String sql = sqlUtil.insert(entityMetadata);
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		String deleteQuery = sqlUtil.delete(entityMetadata);
		dbService.delete(deleteQuery, pk);

		Set<String> whereKeys = new HashSet<String>();
		whereKeys.add("id");
		String querySql = sqlUtil.query(entityMetadata, whereKeys);
		Person dbPerson = dbService.findOne(entityMetadata, querySql, pk);
		Assert.assertNull(dbPerson);
	}
}
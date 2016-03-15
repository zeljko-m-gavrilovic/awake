package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.properties.model.Person;
import rs.bignumbers.rowmapper.EntityMetadataRowMapper;
import rs.bignumbers.util.ProxyRegister;
import rs.bignumbers.util.SqlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestDbService {

	private DbService dbService;

	@Autowired
	private DataSource dataSource;
	private AnnotationBasedMetadataExtractor metadataExtractor;
	private SqlUtil sqlUtil;
	private ProxyRegister proxyRegister;
	private ProxyFactory proxyFactory;

	@Before
	public void setUp() {
		this.metadataExtractor = new AnnotationBasedMetadataExtractor();
		this.dbService = new DbService(dataSource);
		this.sqlUtil = new SqlUtil();
		this.proxyRegister = new ProxyRegister();
		this.proxyFactory = new ProxyFactory();
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

		String sql = sqlUtil.insert(entityMetadata.getTableName(), entityMetadata.getResponsibleColumns());
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereColumns = new HashSet<String>();
		whereColumns.add("id");
		String querySql = sqlUtil.query(entityMetadata.getTableName(), whereColumns);
		EntityMetadataRowMapper<Person> rowMapper = new EntityMetadataRowMapper<>(entityMetadata, null, proxyFactory, proxyRegister);
		Person dbPerson = dbService.findOne(querySql, pk, rowMapper);
		Assert.assertNotNull(dbPerson);

		String deleteQuery = sqlUtil.delete(entityMetadata.getTableName());
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

		String sql = sqlUtil.insert(entityMetadata.getTableName(), entityMetadata.getResponsibleColumns());
		Long pk1 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk1);

		Long pk2 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk2);

		Long pk3 = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk3);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("first_name", "Zeljko");

		String querySql = sqlUtil.query(entityMetadata.getTableName(), params.keySet());
		EntityMetadataRowMapper<Person> rowMapper = new EntityMetadataRowMapper<>(entityMetadata, null, proxyFactory, proxyRegister);
		List<Person> persons = dbService.findList(querySql, params, rowMapper);
		Assert.assertNotNull(persons);
		Assert.assertEquals(3, persons.size());

		String deleteQuery = sqlUtil.delete(entityMetadata.getTableName());
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

		String sql = sqlUtil.insert(entityMetadata.getTableName(), entityMetadata.getResponsibleColumns());
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereColumns = new HashSet<String>();
		whereColumns.add("id");
		String querySql = sqlUtil.query(entityMetadata.getTableName(), whereColumns);
		EntityMetadataRowMapper<Person> rowMapper = new EntityMetadataRowMapper<>(entityMetadata, null, proxyFactory, proxyRegister);
		Person dbPerson = dbService.findOne(querySql, pk, rowMapper);
		Assert.assertNotNull(dbPerson);

		String deleteQuery = sqlUtil.delete(entityMetadata.getTableName());
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

		String sql = sqlUtil.insert(entityMetadata.getTableName(), entityMetadata.getResponsibleColumns());
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		Set<String> whereColumns = new HashSet<String>();
		whereColumns.add("id");
		String querySql = sqlUtil.query(entityMetadata.getTableName(), whereColumns);
		EntityMetadataRowMapper<Person> rowMapper = new EntityMetadataRowMapper<>(entityMetadata, null, proxyFactory, proxyRegister);
		Person dbPerson = dbService.findOne(querySql, pk, rowMapper);
		Assert.assertNotNull(dbPerson);

		String firstNameUpdated = "updated name";
		dbPerson.setFirstName(firstNameUpdated);
		Map<String, Object> updatedProperties = new HashMap<String, Object>();
		updatedProperties.put("firstName", firstNameUpdated);

		Set<String> dirtyColumns = updatedProperties.keySet().stream()
				.map(dirtyProperty -> entityMetadata.getPropertiesMetadata().get(dirtyProperty).getColumnName())
				.collect(Collectors.toSet());
		String updateSql = sqlUtil.update(entityMetadata.getTableName(), dirtyColumns, "id");

		Map<String, Object> params = new HashMap<String, Object>();
		for (String propertyName : updatedProperties.keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			params.put(columnName, updatedProperties.get(propertyName));
		}
		params.put("id", pk);
		dbService.update(updateSql, params);
		dbPerson = dbService.findOne(querySql, pk, rowMapper);
		Assert.assertNotNull(dbPerson);
		Assert.assertEquals(firstNameUpdated, dbPerson.getFirstName());

		String deleteQuery = sqlUtil.delete(entityMetadata.getTableName());
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

		String sql = sqlUtil.insert(entityMetadata.getTableName(), entityMetadata.getResponsibleColumns());
		Long pk = dbService.insert(sql, parameters);
		Assert.assertNotNull(pk);

		String deleteQuery = sqlUtil.delete(entityMetadata.getTableName());
		dbService.delete(deleteQuery, pk);

		Set<String> whereColumns = new HashSet<String>();
		whereColumns.add("id");
		String querySql = sqlUtil.query(entityMetadata.getTableName(), whereColumns);
		EntityMetadataRowMapper<Person> rowMapper = new EntityMetadataRowMapper<>(entityMetadata, null, proxyFactory, proxyRegister);
		Person dbPerson = dbService.findOne(querySql, pk, rowMapper);
		Assert.assertNull(dbPerson);
	}
}
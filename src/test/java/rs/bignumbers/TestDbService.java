package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;
import rs.bignumbers.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestDbService {

	private DbService dbService;

	@Autowired
	private DataSource dataSource;
	private MetadataExtractor metadataExtractor;

	@Before
	public void setUp() {
		this.metadataExtractor = new MetadataExtractor();
		this.dbService = new DbService(dataSource);
	}

	@Test
	public void testQueryOne() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata em = metadataExtractor.extractMetadataForClass(Person.class);
		Long pk = dbService.insert(p, em);
		Assert.assertNotNull(pk);

		Person person = dbService.findOne(em, pk);
		Assert.assertNotNull(person);

		dbService.delete(em, pk);
	}

	@Test
	public void testQueryList() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata em = metadataExtractor.extractMetadataForClass(Person.class);
		Long pk1 = dbService.insert(p, em);
		Assert.assertNotNull(pk1);

		Long pk2 = dbService.insert(p, em);
		Assert.assertNotNull(pk2);

		Long pk3 = dbService.insert(p, em);
		Assert.assertNotNull(pk3);

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("firstName", "Zeljko");
		List<Person> persons = dbService.findList(em, where);
		Assert.assertNotNull(persons);
		Assert.assertEquals(3, persons.size());

		dbService.delete(em, pk1);
		dbService.delete(em, pk2);
		dbService.delete(em, pk3);
	}

	@Test
	public void testInsert() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata em = metadataExtractor.extractMetadataForClass(Person.class);
		Long pk = dbService.insert(p, em);
		Assert.assertNotNull(pk);

		Person dbPerson = dbService.findOne(em, pk);
		Assert.assertNotNull(dbPerson);

		dbService.delete(em, pk);
	}

	@Test
	public void testUpdate() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata em = metadataExtractor.extractMetadataForClass(Person.class);
		Long pk = dbService.insert(p, em);
		Assert.assertNotNull(pk);

		Person dbPerson = dbService.findOne(em, pk);
		Assert.assertNotNull(dbPerson);

		String firstNameUpdated = "updated name";
		dbPerson.setFirstName(firstNameUpdated);
		Map<String, Object> updatedProperties = new HashMap<String, Object>();
		updatedProperties.put("firstName", firstNameUpdated);
		dbService.update(dbPerson, updatedProperties, em);

		dbPerson = dbService.findOne(em, pk);
		Assert.assertNotNull(dbPerson);

		Assert.assertEquals(firstNameUpdated, dbPerson.getFirstName());

		dbService.delete(em, pk);
	}

	@Test
	public void testDelete() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");

		EntityMetadata em = metadataExtractor.extractMetadataForClass(Person.class);
		Long pk = dbService.insert(p, em);
		Assert.assertNotNull(pk);

		dbService.delete(em, pk);

		Person dbPerson = dbService.findOne(em, pk);
		Assert.assertNull(dbPerson);
	}
}
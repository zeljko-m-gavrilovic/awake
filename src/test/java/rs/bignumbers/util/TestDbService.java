package rs.bignumbers.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rs.bignumbers.AwakeApplication;
import rs.bignumbers.model.Person;
import rs.bignumbers.service.DbService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AwakeApplication.class)
public class TestDbService {

	@Autowired
	private DbService dbService;
	
	@Test
	public void testQuery() {
		Person person = dbService.getOne(Person.class, Long.valueOf(1));
		Assert.assertNotNull(person);
	}
	
	@Test
	public void testInsert() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		
		Long pk = dbService.insert(p);
		Assert.assertNotNull(pk);
		
		Person dbPerson = dbService.getOne(Person.class, pk);
		Assert.assertNotNull(dbPerson);
	}
	
	@Test
	public void testUpdate() {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		
		Long pk = dbService.insert(p);
		Assert.assertNotNull(pk);
		
		Person dbPerson = dbService.getOne(Person.class, pk);
		Assert.assertNotNull(dbPerson);
		
		String firstNameUpdated = "updated name";
		dbPerson.setFirstName(firstNameUpdated);
		dbService.update(dbPerson, new String[] {"firstName"});
		
		dbPerson = dbService.getOne(Person.class, pk);
		Assert.assertNotNull(dbPerson);
		
		Assert.assertEquals(firstNameUpdated, dbPerson.getFirstName());
	}
}
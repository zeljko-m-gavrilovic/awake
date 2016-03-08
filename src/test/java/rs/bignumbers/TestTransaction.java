package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rs.bignumbers.model.Man;
import rs.bignumbers.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestTransaction {

	@Autowired
	private DataSource dataSource;
	
	private Transaction transaction;
	
	@Before
	public void setUp() {
		List<Class> entities = new ArrayList<Class>();
		entities.add(Person.class);
		entities.add(Man.class);
		transaction = new Transaction(entities, dataSource);
	}
	
	@Test
	public void testInsert() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		transaction.insert(p);
		transaction.delete(p);
	}
	
	
	@Test
	public void testUpdate() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		transaction.insert(p);
		
		Person dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		
		int ageUpdated = 37;
		dbOne.setAge(ageUpdated);
		/*String placeUpdated = "Belgrade";
		dbOne.setPlace(placeUpdated);*/
		
		transaction.update(dbOne);
		dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		Assert.assertEquals(Integer.valueOf(37), dbOne.getAge());
		//Assert.assertEquals(placeUpdated, dbOne.getPlace());
		
		transaction.delete(p);
	}
	
	@Test
	public void testDelete() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		transaction.insert(p);
		
		Person dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		
		transaction.delete(p);
	}
}
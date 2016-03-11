package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import rs.bignumbers.properties.model.Man;
import rs.bignumbers.properties.model.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestTransactionDetached {

	@Autowired
	private DataSource dataSource;
	
	private Transaction transaction;
	
	@Autowired
	private PlatformTransactionManager txManager;
	
	@Before
	public void setUp() {
		List<Class> entities = new ArrayList<Class>();
		entities.add(Person.class);
		entities.add(Man.class);
		transaction = new Transaction(entities, dataSource, txManager, true);
	}
	
	@Test
	public void testInsert() throws Exception {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		
		transaction.insert(p);
		Assert.assertEquals(1, transaction.getStatements().size());
		Assert.assertEquals(StatementType.Insert, transaction.getStatements().get(0).getStatementType());
		//System.out.println(transaction.getStatements().get(0));
		
		Person dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNull(dbOne);
		
		transaction.commit();
		
		dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		
		transaction.delete(p);
		transaction.commit();
	}
	
	@Test
	public void testUpdate() throws Exception {
		Person p = new Person();
		p.setFirstName("Zeljko");
		p.setLastName("Gavrilovic");
		p.setAge(35);
		p.setPlace("Bg");
		
		transaction.insert(p);
		Assert.assertEquals(1, transaction.getStatements().size());
		Assert.assertEquals(StatementType.Insert, transaction.getStatements().get(0).getStatementType());
		
		int ageUpdated = 37;
		p.setAge(ageUpdated);
		
		transaction.update(p);
		Assert.assertEquals(2, transaction.getStatements().size());
		Assert.assertEquals(StatementType.Insert, transaction.getStatements().get(0).getStatementType());
		Assert.assertEquals(StatementType.Update, transaction.getStatements().get(1).getStatementType());
		
		/*System.out.println(transaction.getStatements().get(0));
		System.out.println(transaction.getStatements().get(1));*/
		
		transaction.commit();
		
		Person dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		//Assert.assertEquals(Integer.valueOf(37), dbOne.getAge());
		
		transaction.delete(p);
		transaction.commit();
	}
	
	/*@Test
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
	
	@Test(expected=RuntimeException.class)
	@Transactional
	public void testTransaction() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person p = new Person();
		p.setFirstName("Zeljko2");
		p.setLastName("Gavrilovic2");
		p.setAge(36);
		p.setPlace("Bg2");
		transaction.insert(p);
		throw new RuntimeException("Intentional exception, transaction must be rollbacked");
	}
	
	@Test
	@Transactional
	public void testTransactionSession() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
		
		transaction.update(dbOne);
		dbOne = transaction.findOne(Person.class, p.getId());
		Assert.assertNotNull(dbOne);
		Assert.assertEquals(Integer.valueOf(37), dbOne.getAge());
		
		transaction.delete(p);
	}
	*/
}
package rs.bignumbers.relations;

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
import org.springframework.transaction.PlatformTransactionManager;

import rs.bignumbers.Configuration;
import rs.bignumbers.Transaction;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.properties.model.Female;
import rs.bignumbers.properties.model.Male;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestOneToOne {

	@Autowired
	private DataSource dataSource;

	private Transaction transaction;

	@Autowired
	private PlatformTransactionManager txManager;

	private Configuration configuration;

	@Before
	public void setUp() {
		List<Class> entities = new ArrayList<Class>();
		entities.add(Male.class);
		entities.add(Female.class);
		this.configuration = new Configuration(entities, new AnnotationBasedMetadataExtractor());
		transaction = new Transaction(configuration, dataSource, txManager, false);
	}

	@Test
	public void testInsert() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Female female = new Female();
		female.setFirstName("F");
		female.setLastName("M");
		female.setAge(35);
		transaction.insert(female);
		/*
		 * Assert.assertEquals(1, transaction.getStatements().size());
		 * Assert.assertEquals(StatementType.Insert,
		 * transaction.getStatements().get(0).getStatementType());
		 */

		Assert.assertNotNull(female);
		Assert.assertNotNull(female.getId());
		/*
		 * Assert.assertEquals(2, transaction.getStatements().size());
		 * Assert.assertEquals(StatementType.Insert,
		 * transaction.getStatements().get(1).getStatementType());
		 */

		Male male = new Male();
		male.setFirstName("Zeljko");
		male.setLastName("Gavrilovic");
		male.setAge(35);
		male.setFemale(female);
		transaction.insert(male);

		Assert.assertNotNull(male);
		Assert.assertNotNull(male.getId());

		/*
		 * System.out.println(transaction.getStatements().get(0));
		 * System.out.println(transaction.getStatements().get(1));
		 */
		
		Male maleDb = transaction.findOne(Male.class, male.getId());
		Assert.assertNotNull(maleDb);
		Assert.assertNotNull(maleDb.getId());
		Assert.assertNotNull(maleDb.getFemale());
		Assert.assertNotNull(maleDb.getFemale().getId());
		Assert.assertNotNull(maleDb.getFemale().getFirstName());
		
		Female femaleDb = transaction.findOne(Female.class, female.getId());
		Assert.assertNotNull(femaleDb);
		Assert.assertNotNull(femaleDb.getId());
		Assert.assertNotNull(femaleDb.getMale());
		Assert.assertNotNull(femaleDb.getMale().getId());
		Assert.assertNotNull(femaleDb.getMale().getFirstName());

		transaction.delete(male);
		transaction.delete(female);
	}

	/*
	 * @Test public void testFindOne() throws IllegalAccessException,
	 * InvocationTargetException, NoSuchMethodException { Person p = new
	 * Person(); p.setFirstName("Zeljko"); p.setLastName("Gavrilovic");
	 * p.setAge(35); p.setPlace("Bg"); transaction.insert(p);
	 * 
	 * Person dbOne = transaction.findOne(Person.class, p.getId());
	 * Assert.assertNotNull(dbOne);
	 * 
	 * transaction.delete(dbOne); }
	 * 
	 * 
	 * @Test public void testFindList() throws IllegalAccessException,
	 * InvocationTargetException, NoSuchMethodException { Person p1 = new
	 * Person(); p1.setFirstName("Zeljko"); p1.setLastName("Gavrilovic");
	 * p1.setAge(35); p1.setPlace("Bg");
	 * 
	 * Long pk1 = transaction.insert(p1); Assert.assertNotNull(pk1);
	 * 
	 * 
	 * Person p2 = new Person(); p2.setFirstName("Zeljko");
	 * p2.setLastName("Gavrilovic"); p2.setAge(35); p2.setPlace("Bg"); Long pk2
	 * = transaction.insert(p2); Assert.assertNotNull(pk2);
	 * 
	 * Person p3 = new Person(); p3.setFirstName("Zeljko");
	 * p3.setLastName("Gavrilovic"); p3.setAge(35); p3.setPlace("Bg"); Long pk3
	 * = transaction.insert(p3); Assert.assertNotNull(pk3);
	 * 
	 * Map<String, Object> where = new HashMap<String, Object>();
	 * where.put("firstName", "Zeljko"); List<Person> persons =
	 * transaction.findList(Person.class, where); Assert.assertNotNull(persons);
	 * Assert.assertEquals(3, persons.size());
	 * 
	 * transaction.delete(p1); transaction.delete(p2); transaction.delete(p3); }
	 * 
	 * @Test public void testUpdate() throws IllegalAccessException,
	 * InvocationTargetException, NoSuchMethodException { Person p = new
	 * Person(); p.setFirstName("Zeljko"); p.setLastName("Gavrilovic");
	 * p.setAge(35); p.setPlace("Bg"); transaction.insert(p);
	 * 
	 * Person dbOne = transaction.findOne(Person.class, p.getId());
	 * Assert.assertNotNull(dbOne);
	 * 
	 * int ageUpdated = 37; dbOne.setAge(ageUpdated);
	 * 
	 * transaction.update(dbOne); dbOne = transaction.findOne(Person.class,
	 * p.getId()); Assert.assertNotNull(dbOne);
	 * Assert.assertEquals(Integer.valueOf(37), dbOne.getAge());
	 * 
	 * transaction.delete(p); }
	 * 
	 * @Test public void testDelete() throws IllegalAccessException,
	 * InvocationTargetException, NoSuchMethodException { Person p = new
	 * Person(); p.setFirstName("Zeljko"); p.setLastName("Gavrilovic");
	 * p.setAge(35); p.setPlace("Bg"); transaction.insert(p);
	 * 
	 * Person dbOne = transaction.findOne(Person.class, p.getId());
	 * Assert.assertNotNull(dbOne);
	 * 
	 * transaction.delete(p); }
	 * 
	 * @Test(expected=RuntimeException.class)
	 * 
	 * @Transactional public void testTransaction() throws
	 * IllegalAccessException, InvocationTargetException, NoSuchMethodException
	 * { Person p = new Person(); p.setFirstName("Zeljko2");
	 * p.setLastName("Gavrilovic2"); p.setAge(36); p.setPlace("Bg2");
	 * transaction.insert(p); throw new RuntimeException(
	 * "Intentional exception, transaction must be rollbacked"); }
	 * 
	 * @Test
	 * 
	 * @Transactional public void testTransactionSession() throws
	 * IllegalAccessException, InvocationTargetException, NoSuchMethodException
	 * { Person p = new Person(); p.setFirstName("Zeljko");
	 * p.setLastName("Gavrilovic"); p.setAge(35); p.setPlace("Bg");
	 * transaction.insert(p);
	 * 
	 * Person dbOne = transaction.findOne(Person.class, p.getId());
	 * Assert.assertNotNull(dbOne);
	 * 
	 * int ageUpdated = 37; dbOne.setAge(ageUpdated);
	 * 
	 * transaction.update(dbOne); dbOne = transaction.findOne(Person.class,
	 * p.getId()); Assert.assertNotNull(dbOne);
	 * Assert.assertEquals(Integer.valueOf(37), dbOne.getAge());
	 * 
	 * transaction.delete(p); }
	 */
}

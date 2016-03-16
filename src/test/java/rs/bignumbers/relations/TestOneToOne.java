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
import rs.bignumbers.metadata.AnnotationMetadataExtractor;
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
		AnnotationMetadataExtractor metadataExtractor = new AnnotationMetadataExtractor(entities);
		this.configuration = new Configuration(metadataExtractor);
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
}

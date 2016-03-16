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
import rs.bignumbers.relations.model.House;
import rs.bignumbers.relations.model.Owner;
import rs.bignumbers.relations.model.Window;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestManyToMany {

	@Autowired
	private DataSource dataSource;

	private Transaction transaction;

	@Autowired
	private PlatformTransactionManager txManager;

	private Configuration configuration;

	@Before
	public void setUp() {
		List<Class> entities = new ArrayList<Class>();
		entities.add(House.class);
		entities.add(Owner.class);
		this.configuration = new Configuration(entities, new AnnotationBasedMetadataExtractor());
		transaction = new Transaction(configuration, dataSource, txManager, false);
	}

	@Test
	public void testInsert() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		House house = new House();
		house.setHouseNo("220/11");
		
		Owner major = new Owner();
		major.setAmount(80.0);
		
		Owner minor = new Owner();
		minor.setAmount(20.0);
		
		house.getOwners().add(major);
		house.getOwners().add(minor);
		
		transaction.insert(major);
		transaction.insert(minor);
		transaction.insert(house);
		
		house = transaction.findOne(House.class, house.getId());
		Assert.assertNotNull(house);
		Assert.assertNotNull(house.getId());
		Assert.assertNotNull(house.getOwners());
		Assert.assertEquals(2, house.getOwners().size());
		
		/*transaction.delete(house);
		transaction.delete(major);
		transaction.delete(minor);*/
	}
}
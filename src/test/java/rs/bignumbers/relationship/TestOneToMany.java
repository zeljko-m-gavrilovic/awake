package rs.bignumbers.relationship;

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
import rs.bignumbers.relationship.model.House;
import rs.bignumbers.relationship.model.Owner;
import rs.bignumbers.relationship.model.Window;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/application-context.xml")
public class TestOneToMany {

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
		entities.add(Window.class);
		entities.add(Owner.class);
		AnnotationMetadataExtractor metadataExtractor = new AnnotationMetadataExtractor(entities);
		this.configuration = new Configuration(metadataExtractor);
		transaction = new Transaction(configuration, dataSource, txManager, false);
	}

	@Test
	public void testInsert() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		House house = new House();
		house.setHouseNo("220/11");
		transaction.insert(house);
		
		Window windowSmall = new Window();
		windowSmall.setSize("small");
		windowSmall.setHouse(house);
		transaction.insert(windowSmall);
		
		Window windowLarge = new Window();
		windowLarge.setSize("large");
		windowLarge.setHouse(house);
		transaction.insert(windowLarge);
		
		house = transaction.findOne(House.class, house.getId());
		Assert.assertNotNull(house);
		Assert.assertNotNull(house.getId());
		Assert.assertNotNull(house.getWindows());
		Assert.assertEquals(2, house.getWindows().size());
		
		transaction.delete(house);
		transaction.delete(windowSmall);
		transaction.delete(windowLarge);
	}
}
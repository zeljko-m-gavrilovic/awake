package rs.bignumbers.transaction;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.EntityInterceptor;
import rs.bignumbers.metadata.AnnotationMetadataExtractor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.transaction.model.Person;

public class TestEntityInterceptor {
	
	@Test
	public void testDirtyProperties() {
		ProxyFactory proxyFactory = new ProxyFactory();
		List<Class> entities = new ArrayList<Class>();
		entities.add(Person.class);
		AnnotationMetadataExtractor me = new AnnotationMetadataExtractor(entities);
		
		EntityMetadata em = me.extractMetadataForClass(Person.class);
		EntityInterceptor interceptor = new EntityInterceptor(em, null);
		Person proxy = proxyFactory.newProxyInstance(Person.class, interceptor);

		proxy.setFirstName("Z");
		proxy.setAge(36);
		Assert.assertEquals(0, interceptor.getDirtyProperties().size());
		
		interceptor.setDirtyPropertiesTrack(true);
		proxy.setFirstName("Z");
		proxy.setAge(36);
		Assert.assertEquals(2, interceptor.getDirtyProperties().size());
	}
}
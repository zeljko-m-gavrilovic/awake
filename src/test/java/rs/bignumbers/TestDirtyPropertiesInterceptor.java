package rs.bignumbers;

import org.junit.Test;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.properties.model.Person;

public class TestDirtyPropertiesInterceptor {
	
	@Test
	public void testDirtyProperties() {
		ProxyFactory proxyFactory = new ProxyFactory();
		AnnotationBasedMetadataExtractor me = new AnnotationBasedMetadataExtractor();
		
		EntityMetadata em = me.extractMetadataForClass(Person.class);
		DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
		Person proxy = proxyFactory.newProxyInstance(Person.class, interceptor);
		proxy.setFirstName("Zeljko");
		proxy.setFirstName("Mika");
		proxy.setLastName("Gavrilovic");
		proxy.setLastName("G");
		System.out.println(interceptor.getDirtyProperties().size());
	}
}
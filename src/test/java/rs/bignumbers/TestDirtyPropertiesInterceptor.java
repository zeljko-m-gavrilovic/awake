package rs.bignumbers;

import org.junit.Test;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;
import rs.bignumbers.model.Person;

public class TestDirtyPropertiesInterceptor {
	
	@Test
	public void testDirtyProperties() {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(Person.class);
		DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
		Person proxy = ProxyFactory.newProxyInstance(Person.class, interceptor);
		proxy.setFirstName("Zeljko");
		proxy.setFirstName("Mika");
		proxy.setLastName("Gavrilovic");
		proxy.setLastName("G");
		System.out.println(interceptor.getDirtyProperties().size());
	}
}
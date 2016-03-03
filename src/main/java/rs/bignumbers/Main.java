package rs.bignumbers;

import rs.bignumbers.model.Person;
import rs.bignumbers.util.EntityMetadata;
import rs.bignumbers.util.MetadataExtractor;
import rs.bignumbers.util.ProxyFactory;

public class Main {
	public static void main(String[] args) {
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(Person.class);
		DirtyValueInterceptor interceptor = new DirtyValueInterceptor(em);
		Person proxy = ProxyFactory.newProxyInstance(Person.class, interceptor);
		proxy.setFirstName("Zeljko");
		proxy.setFirstName("Mika");
		proxy.setLastName("Gavrilovic");
		proxy.setLastName("G");
		System.out.println(interceptor.getMap().toString());
	}
}
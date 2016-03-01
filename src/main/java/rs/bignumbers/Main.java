package rs.bignumbers;

import rs.bignumbers.model.Person;

public class Main {
	public static void main(String[] args) {
		DirtyValueInterceptor interceptor = new DirtyValueInterceptor();
		Person proxy = ProxyFactory.newProxyInstance(Person.class, interceptor);
		proxy.setFirstName("Zeljko");
		proxy.setFirstName("Mika");
		proxy.setLastName("Gavrilovic");
		proxy.setLastName("G");
		System.out.println(interceptor.getMap().toString());
	}
}
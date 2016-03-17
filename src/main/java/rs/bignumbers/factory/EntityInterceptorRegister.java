package rs.bignumbers.factory;

import java.util.HashMap;
import java.util.Map;

import rs.bignumbers.interceptor.EntityInterceptor;

public class EntityInterceptorRegister {

	private Map<String, EntityInterceptor> interceptors;

	public EntityInterceptorRegister() {
		interceptors = new HashMap<String, EntityInterceptor>();
	}

	public void addInterceptor(String identificator, EntityInterceptor dirtyValueInterceptor) {
		interceptors.put(identificator, dirtyValueInterceptor);
	}

	public EntityInterceptor getInterceptor(String identificator) {
		return interceptors.get(identificator);
	}
}

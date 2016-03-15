package rs.bignumbers.util;

import java.util.HashMap;
import java.util.Map;

import rs.bignumbers.interceptor.EntityInterceptor;

public class ProxyRegister {

	private Map<String, EntityInterceptor> interceptors;

	public ProxyRegister() {
		interceptors = new HashMap<String, EntityInterceptor>();
	}

	public void addInterceptor(String identificator, EntityInterceptor dirtyValueInterceptor) {
		interceptors.put(identificator, dirtyValueInterceptor);
	}

	public EntityInterceptor getInterceptor(String identificator) {
		return interceptors.get(identificator);
	}
}

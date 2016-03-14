package rs.bignumbers.util;

import java.util.HashMap;
import java.util.Map;

import rs.bignumbers.interceptor.DirtyValueInterceptor;

public class ProxyRegister {

	private Map<String, DirtyValueInterceptor> interceptors;

	public ProxyRegister() {
		interceptors = new HashMap<String, DirtyValueInterceptor>();
	}

	public void addInterceptor(String identificator, DirtyValueInterceptor dirtyValueInterceptor) {
		interceptors.put(identificator, dirtyValueInterceptor);
	}

	public DirtyValueInterceptor getInterceptor(String identificator) {
		return interceptors.get(identificator);
	}
}

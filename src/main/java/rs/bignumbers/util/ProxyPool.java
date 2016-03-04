package rs.bignumbers.util;

import java.util.HashMap;
import java.util.Map;

import rs.bignumbers.DirtyValueInterceptor;

public class ProxyPool {

	private static Map<String, DirtyValueInterceptor> interceptors = new HashMap<String, DirtyValueInterceptor>();
	
	public static void addInterceptor(String identificator, DirtyValueInterceptor dirtyValueInterceptor) {
		interceptors.put(identificator, dirtyValueInterceptor);
	}
	
	public static DirtyValueInterceptor getInterceptor(String identificator) {
		return interceptors.get(identificator);
	}
}

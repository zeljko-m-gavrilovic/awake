package rs.bignumbers.util;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyFactory {

	@SuppressWarnings("unchecked")
	public static <T> T newProxyInstance(Class<T> c, MethodInterceptor interceptor) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(c);
		enhancer.setCallback(interceptor);
		T proxy = (T) enhancer.create();
		return  proxy;
	}
}
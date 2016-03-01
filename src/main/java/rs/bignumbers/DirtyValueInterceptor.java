package rs.bignumbers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class DirtyValueInterceptor implements MethodInterceptor {
	Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean cond = method.getName().startsWith("set");
		if (cond) {
			String name = method.getName().substring(3);
			name = name.replaceFirst(String.valueOf(name.charAt(0)), String.valueOf(Character.toLowerCase(name.charAt(0))));
			map.put(name, args[0]);
			return "Hello cglib!";
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}

	public Map<String, Object> getMap() {
		return map;
	}
	
}

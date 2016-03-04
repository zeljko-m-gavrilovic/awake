package rs.bignumbers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import rs.bignumbers.util.EntityMetadata;

public class DirtyValueInterceptor implements MethodInterceptor {

	private EntityMetadata em;
	private Object target; 

	public DirtyValueInterceptor(EntityMetadata em) {
		this.em = em;
	}

	Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean callToSetter = method.getName().startsWith("set");
		if (callToSetter) {
			String propertyName = method.getName().substring(3);
			propertyName = propertyName.replaceFirst(String.valueOf(propertyName.charAt(0)),
					String.valueOf(Character.toLowerCase(propertyName.charAt(0))));
			if (em.getPropertiesMetadata().containsKey(propertyName)) {
				map.put(propertyName, args[0]);
			}
		}
		return proxy.invokeSuper(obj, args);

	}

	public Map<String, Object> getMap() {
		return map;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
}
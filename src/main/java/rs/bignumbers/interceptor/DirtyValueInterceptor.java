package rs.bignumbers.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import rs.bignumbers.metadata.EntityMetadata;

public class DirtyValueInterceptor implements MethodInterceptor {

	private EntityMetadata em;
	private Object target; 
	private Map<String, Object> dirtyProperties;
	private boolean track;
	
	public DirtyValueInterceptor(EntityMetadata em) {
		this.em = em;
		dirtyProperties = new HashMap<String, Object>();
		track = false;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean callToSetter = method.getName().startsWith("set");
		if (callToSetter) {
			String propertyName = method.getName().substring(3);
			propertyName = propertyName.replaceFirst(String.valueOf(propertyName.charAt(0)),
					String.valueOf(Character.toLowerCase(propertyName.charAt(0))));
			if (em.getPropertiesMetadata().containsKey(propertyName) && track) {
				dirtyProperties.put(propertyName, args[0]);
			}
		}
		return proxy.invokeSuper(obj, args);

	}

	public Map<String, Object> getDirtyProperties() {
		return dirtyProperties;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
	
	public boolean hasDirtyProperties() {
		return dirtyProperties.keySet().size() > 0;
	}

	public boolean isTrack() {
		return track;
	}

	public void setTrack(boolean track) {
		this.track = track;
	}
	
	
}
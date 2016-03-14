package rs.bignumbers.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.relation.RelationNotification;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipPropertyMetadata;

public class DirtyValueInterceptor implements MethodInterceptor {

	private EntityMetadata entityMetadata;
	private Object target;
	private Map<String, Object> dirtyProperties;
	private boolean track;

	public DirtyValueInterceptor(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
		dirtyProperties = new HashMap<String, Object>();
		track = false;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		boolean callToGetter = method.getName().startsWith("get");
		boolean callToSetter = method.getName().startsWith("set");
		if (callToGetter || callToSetter) {
			String propertyName = method.getName().substring(3);
			propertyName = propertyName.replaceFirst(String.valueOf(propertyName.charAt(0)),
					String.valueOf(Character.toLowerCase(propertyName.charAt(0))));
			boolean propertyExist = entityMetadata.getPropertiesMetadata().containsKey(propertyName);
			
			if(callToSetter) {
				if (propertyExist && track) {
					dirtyProperties.put(propertyName, args[0]);
				}
			}
			
			if(callToGetter) {
				if (propertyExist) {
					PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
					boolean propertyRelationship = RelationshipPropertyMetadata.class.isAssignableFrom(propertyMetadata.getClass());
					if(propertyRelationship) {
						RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) propertyMetadata;
						if(FetchType.Lazy.equals(rpm.getFetch()) && proxy.invokeSuper(obj, args) == null) {
							System.out.println("take it");
						}
					}
				}
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
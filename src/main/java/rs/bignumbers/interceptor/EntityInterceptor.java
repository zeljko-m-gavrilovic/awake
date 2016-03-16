package rs.bignumbers.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import rs.bignumbers.Transaction;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipForeignKeyPropertyMetadata;

public class EntityInterceptor implements MethodInterceptor {

	private EntityMetadata entityMetadata;
	// private Object target;
	private Map<String, Object> dirtyProperties;
	private Map<String, Boolean> lazyProperties;
	private boolean dirtyPropertiesTrack;
	private Transaction transaction;

	public EntityInterceptor(EntityMetadata entityMetadata, Transaction transaction) {
		this.entityMetadata = entityMetadata;
		this.dirtyProperties = new HashMap<String, Object>();
		this.dirtyPropertiesTrack = false;
		this.lazyProperties = new HashMap<String, Boolean>();
		this.transaction = transaction;
		entityMetadata.getLazyProperties().stream().map(pm -> pm.getPropertyName())
				.forEach(propertyName -> lazyProperties.put(propertyName, Boolean.FALSE));
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

			if (callToSetter && propertyExist && dirtyPropertiesTrack) {
				dirtyProperties.put(propertyName, args[0]);
			}

			if (callToGetter && propertyExist) {
				boolean lazyLoadingNecessary = lazyProperties.containsKey(propertyName)
						&& Boolean.FALSE.equals(lazyProperties.get(propertyName));
				if (lazyLoadingNecessary) {
					PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
					Object nestedObject = proxy.invokeSuper(obj, args);
					Object loadedRelationship = transaction.loadRelationship(obj, nestedObject, propertyMetadata);
					PropertyUtils.setProperty(obj, propertyName, loadedRelationship);
					lazyProperties.put(propertyName, Boolean.TRUE);
				}
			}
		}
		return proxy.invokeSuper(obj, args);

	}

	public Map<String, Object> getDirtyProperties() {
		return dirtyProperties;
	}

	/*
	 * public Object getTarget() { return target; }
	 * 
	 * public void setTarget(Object target) { this.target = target; }
	 */

	public boolean hasDirtyProperties() {
		return dirtyProperties.keySet().size() > 0;
	}

	public boolean isDirtyPropertiesTrack() {
		return dirtyPropertiesTrack;
	}

	public void setDirtyPropertiesTrack(boolean track) {
		this.dirtyPropertiesTrack = track;
	}

}
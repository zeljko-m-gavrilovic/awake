package rs.bignumbers.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import rs.bignumbers.Transaction;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;

public class EntityInterceptor implements MethodInterceptor {

	private final Logger logger = LoggerFactory.getLogger(EntityInterceptor.this.getClass());

	private EntityMetadata entityMetadata;
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
		entityMetadata.getLazyRelationships().stream().map(pm -> pm.getPropertyName())
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
				logger.trace("dirty value setfor entity {}, {}={}", entityMetadata.getClazz().getName(), propertyName,
						args[0]);
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
					logger.debug("lazy property {} for entity {} loaded", propertyName,
							entityMetadata.getClazz().getName());
				}
			}
		}
		return proxy.invokeSuper(obj, args);

	}

	public Map<String, Object> getDirtyProperties() {
		return dirtyProperties;
	}

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
package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.RowMapper;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipPropertyMetadata;
import rs.bignumbers.util.ProxyRegister;

class EntityMetadataRowMapper<T> implements RowMapper<T> {

	private EntityMetadata entityMetadata;
	private ProxyFactory proxyFactory;
	private ProxyRegister proxyRegister;
	
	public EntityMetadataRowMapper(EntityMetadata entityMetadata, ProxyFactory proxyFactory, ProxyRegister proxyRegister) {
		this.entityMetadata = entityMetadata;
		this.proxyFactory = proxyFactory;
		this.proxyRegister = proxyRegister;
	}
	
	public T mapRow(ResultSet rs, int arg1) throws SQLException {

		DirtyValueInterceptor interceptor = new DirtyValueInterceptor(entityMetadata);
		T proxy = (T) proxyFactory.newProxyInstance(entityMetadata.getClazz(), interceptor);
		interceptor.setTarget(proxy);

		for (PropertyMetadata pm : entityMetadata.getResponsibleProperties()) {
			Object value = null;
			if(RelationshipPropertyMetadata.class.isAssignableFrom(pm.getClass())) {
				Long id = rs.getLong(pm.getColumnName());
				try {
					value = pm.getJavaType().newInstance();
					PropertyUtils.setProperty(value, "id", id);
					//PropertyUtils.setProperty(proxy, pm.getPropertyName(), value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (String.class.isAssignableFrom(pm.getJavaType())) {
				value = rs.getString(pm.getColumnName());
			}
			if (Integer.class.isAssignableFrom(pm.getJavaType())) {
				value = rs.getInt(pm.getColumnName());
			}
			if (Long.class.isAssignableFrom(pm.getJavaType())) {
				value = rs.getLong(pm.getColumnName());
			}
			if (Date.class.isAssignableFrom(pm.getJavaType())) {
				value = rs.getDate(pm.getColumnName());
			}
			if (Boolean.class.isAssignableFrom(pm.getJavaType())) {
				value = rs.getBoolean(pm.getColumnName());
			}
			
			try {
				PropertyUtils.setProperty(proxy, pm.getPropertyName(), value);
			} catch (Exception e) {
				throw new RuntimeException("Property utils exception can't set property " + pm.getPropertyName()
						+ " of the proxy instance class " + entityMetadata.getClazz().getSimpleName());
			}
		}
		
		proxyRegister.addInterceptor(proxy.getClass().getName() + "/" + rs.getLong("id"),
				interceptor);
		interceptor.setTrack(true);
		return proxy;
	}
}
package rs.bignumbers.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.RowMapper;

import rs.bignumbers.Transaction;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.EntityInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipMetadata;
import rs.bignumbers.util.ProxyRegister;

public class EntityMetadataRowMapper<T> implements RowMapper<T> {

	private EntityMetadata entityMetadata;
	private Transaction transaction;
	private ProxyFactory proxyFactory;
	private ProxyRegister proxyRegister;

	public EntityMetadataRowMapper(EntityMetadata entityMetadata, Transaction transaction, ProxyFactory proxyFactory,
			ProxyRegister proxyRegister) {
		this.entityMetadata = entityMetadata;
		this.transaction = transaction;
		this.proxyFactory = proxyFactory;
		this.proxyRegister = proxyRegister;

	}

	public T mapRow(ResultSet rs, int arg1) throws SQLException {
		EntityInterceptor entityInterceptor = new EntityInterceptor(entityMetadata, transaction);
		T proxy = (T) proxyFactory.newProxyInstance(entityMetadata.getClazz(), entityInterceptor);

		for (PropertyMetadata pm : entityMetadata.getResponsibleProperties()) {
			Object value = null;
			if (RelationshipMetadata.class.isAssignableFrom(pm.getClass())) {
				RelationshipMetadata rpm = (RelationshipMetadata) pm;
				try {
					if (rpm.isResponsible() && !Collection.class.isAssignableFrom(rpm.getJavaType())) {
						Long id = rs.getLong(pm.getColumnName());
						value = pm.getJavaType().newInstance();
						PropertyUtils.setProperty(value, "id", id);
					}
					if (FetchType.Eager.equals(rpm.getFetch())) {
						value = transaction.loadRelationship(proxy, value, rpm);
					}
				} catch (Throwable e) {
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
		proxyRegister.addInterceptor(proxy.getClass().getName() + "/" + rs.getLong("id"), entityInterceptor);
		entityInterceptor.setDirtyPropertiesTrack(true);
		return (T) proxy;
	}
}
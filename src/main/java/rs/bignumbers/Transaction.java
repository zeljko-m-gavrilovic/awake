package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import rs.bignumbers.factory.ProxyFactory;
import rs.bignumbers.interceptor.EntityInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipMetadata;
import rs.bignumbers.rowmapper.EntityMetadataRowMapper;
import rs.bignumbers.rowmapper.JoinTableRowMapper;
import rs.bignumbers.util.ProxyRegister;
import rs.bignumbers.util.SqlUtil;

public class Transaction {

	private final Logger logger = LoggerFactory.getLogger(Transaction.this.getClass());

	private DbService dbService;
	private ProxyRegister proxyRegister;
	private SqlUtil sqlUtil;
	private ProxyFactory proxyFactory;

	private List<Statement> statements;
	private PlatformTransactionManager txManager;
	private Configuration configuration;
	private boolean detached;

	public Transaction(Configuration configuration, DataSource datasource, PlatformTransactionManager txManager,
			boolean detached) {
		this.configuration = configuration;
		this.dbService = new DbService(datasource);
		this.txManager = txManager;
		this.detached = detached;

		this.sqlUtil = new SqlUtil();
		this.statements = new ArrayList<Statement>();
		this.proxyRegister = new ProxyRegister();
		this.proxyFactory = new ProxyFactory();
	}

	public <T> T findOne(Class<T> clazz, Long id) {
		Map<String, Object> whereParameters = new HashMap<String, Object>();
		whereParameters.put("id", id);
		T one = null;
		if(!detached) {
			List<T> all = findList(clazz, whereParameters);
			
			if (all != null && !all.isEmpty()) {
				one = all.get(0);
				return one;
			}
		} else {
			EntityMetadata entityMetadata = configuration.getEntityMetadata(clazz);
			EntityInterceptor entityInterceptor = new EntityInterceptor(entityMetadata, this);
			T proxy = (T) proxyFactory.newProxyInstance(entityMetadata.getClazz(), entityInterceptor);
			try {
				PropertyUtils.setProperty(proxy, "id", id);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			proxyRegister.addInterceptor(proxy.getClass().getName() + "/" + whereParameters.get("id"), entityInterceptor);
			entityInterceptor.setDirtyPropertiesTrack(true);
			return (T) proxy;
		}
		return null;
	}

	public <T> T findOne(Class<T> clazz, Map<String, Object> whereParameters) {
		// Map<String, Object> whereParameters = new HashMap<String, Object>();
		// whereParameters.put("id", id);
		List<T> all = findList(clazz, whereParameters);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
	}

	public <T> List<T> findList(Class<T> clazz, Map<String, Object> whereParameters) {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(clazz);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		for (String propertyName : whereParameters.keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.addValue(columnName, whereParameters.get(propertyName));
		}

		String sql = sqlUtil.query(entityMetadata.getTableName(), parameters.getValues().keySet());

		EntityMetadataRowMapper<T> rowMapper = new EntityMetadataRowMapper<T>(entityMetadata, this, proxyFactory,
				proxyRegister);
		List<T> findList = dbService.findList(sql, parameters.getValues(), rowMapper);
		return findList;
	}

	public <T> List<T> findListIn(Class<T> clazz, List<Long> ids) {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(clazz);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("ids", ids);
		String sql = sqlUtil.queryIn(entityMetadata.getTableName());

		EntityMetadataRowMapper<T> rowMapper = new EntityMetadataRowMapper<T>(entityMetadata, this, proxyFactory,
				proxyRegister);
		List<T> findList = dbService.findList(sql, parameters.getValues(), rowMapper);
		return findList;
	}

	@SuppressWarnings("unchecked")
	public Long insert(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(o.getClass());

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (PropertyMetadata propertyMetadata : entityMetadata.getResponsibleProperties()) {
			String columnName = propertyMetadata.getColumnName();

			if ((RelationshipMetadata.class.isAssignableFrom(propertyMetadata.getClass()))
					&& ((RelationshipMetadata) propertyMetadata).isJoinTableRelationship()) {
				continue;
			} else {
				Object propertyValue = PropertyUtils.getProperty(o, propertyMetadata.getPropertyName());
				if ((RelationshipMetadata.class.isAssignableFrom(propertyMetadata.getClass())
						&& propertyValue != null)) {
					propertyValue = PropertyUtils.getNestedProperty(o, propertyMetadata.getPropertyName() + ".id");
				}
				parameters.put(columnName, propertyValue);
			}
		}

		String sql = sqlUtil.insert(entityMetadata.getTableName(), parameters.keySet());

		Long pk = null;
		if (detached) {
			statements.add(new Statement(sql, parameters, StatementType.Insert, o));
			pk = Long.valueOf(-1L);
		} else {
			pk = dbService.insert(sql, parameters);
			logger.debug("sql insert: {} with parameters: {}", sql, parameters.toString());
			PropertyUtils.setProperty(o, "id", pk);
		}

		for (PropertyMetadata propertyMetadata : entityMetadata.getResponsibleJoinTableProperties()) {
			RelationshipMetadata relationshipTable = (RelationshipMetadata) propertyMetadata;
			Object propertyValue = PropertyUtils.getProperty(o, propertyMetadata.getPropertyName());
			if (propertyValue != null) {
				Collection collection = (Collection) PropertyUtils.getProperty(o, relationshipTable.getPropertyName());
				Map<String, Object> joinTable = new HashMap<String, Object>();
				joinTable.put(relationshipTable.getColumnName(), pk);
				joinTable.put(relationshipTable.getOtherSideColumnName(), null);
				String joinTableSql = sqlUtil.insert(relationshipTable.getTableName(), joinTable.keySet());
				collection.stream().forEach(e -> {
					try {
						Long elementId = (Long) PropertyUtils.getProperty(e, "id");
						joinTable.put(relationshipTable.getOtherSideColumnName(), elementId);
						dbService.insert(joinTableSql, joinTable);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				});
			}
		}
		return pk;
	}

	public void update(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(o.getClass());
		Long id = (Long) PropertyUtils.getProperty(o, "id");
		EntityInterceptor interceptor = proxyRegister.getInterceptor(o.getClass().getName() + "/" + id);

		Map<String, Object> dirtyProperties = null;
		if (interceptor != null) {
			dirtyProperties = interceptor.getDirtyProperties();
		} else {
			dirtyProperties = new HashMap<String, Object>();
			for (PropertyMetadata pm : entityMetadata.getResponsibleProperties()) {
				Object propertyValue = PropertyUtils.getProperty(o, pm.getPropertyName());
				if (pm instanceof RelationshipMetadata && propertyValue != null) {
					propertyValue = PropertyUtils.getProperty(o, pm.getPropertyName() + ".id");
				}
				dirtyProperties.put(pm.getPropertyName(), propertyValue);

			}
		}
		dirtyProperties.put("id", id);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : dirtyProperties.keySet()) {
			PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
			Object propertyValue = PropertyUtils.getProperty(o, propertyName);
			if ((propertyMetadata instanceof RelationshipMetadata) && propertyValue != null) {
				propertyValue = PropertyUtils.getNestedProperty(o, propertyName + ".id");
			}
			parameters.put(propertyMetadata.getColumnName(), propertyValue);
		}
		String sql = sqlUtil.update(entityMetadata.getTableName(), parameters.keySet(), "id");
		if (detached) {
			statements.add(new Statement(sql, parameters, StatementType.Update, null));
		} else {
			dbService.update(sql, parameters);
		}
	}

	public void delete(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Long id = (Long) PropertyUtils.getProperty(o, "id");
		delete(o.getClass(), id);
	}

	public void delete(Class clazz, Long id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(clazz);
		
		Object o = findOne(clazz, id);
		List<PropertyMetadata> joinTableRelationship = entityMetadata.getResponsibleJoinTableProperties();
		if(joinTableRelationship != null) {
			for(PropertyMetadata propertyMetadata : joinTableRelationship) {
				RelationshipMetadata relationshipTable = (RelationshipMetadata) propertyMetadata;
				Object propertyValue = PropertyUtils.getProperty(o, propertyMetadata.getPropertyName());
				if (propertyValue != null) {
					//Collection collection = (Collection) PropertyUtils.getProperty(o, relationshipTable.getPropertyName());
					String joinTableSql = sqlUtil.delete(relationshipTable.getTableName(), relationshipTable.getColumnName());
					Map<String, Object> joinTable = new HashMap<String, Object>();
					joinTable.put(relationshipTable.getColumnName(), id);
					dbService.delete(joinTableSql, joinTable);
				}
			}
		}
		
		String sql = sqlUtil.delete(entityMetadata.getTableName());
		if (detached) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("id", id);
			statements.add(new Statement(sql, parameters, StatementType.Delete, null));
		} else {
			dbService.delete(sql, id);
		}
		
	}

	public void commit() throws Exception {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		// explicitly setting the transaction name is something that can only be
		// done programmatically
		def.setName("AwakeTx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		TransactionStatus status = txManager.getTransaction(def);
		try {
			for (Statement statement : statements) {
				if (StatementType.Insert.equals(statement.getStatementType())) {
					Long pk = dbService.insert(statement.getSql(), statement.getParameters());
					PropertyUtils.setProperty(statement.getObject(), "id", pk);
				} else if (StatementType.Update.equals(statement.getStatementType())) {
					dbService.update(statement.getSql(), statement.getParameters());
				} else if (StatementType.Delete.equals(statement.getStatementType())) {
					dbService.delete(statement.getSql(), (Long) statement.getParameters().get("id"));
				}
			}
			txManager.commit(status);
			statements.clear();
		} catch (Exception ex) {
			txManager.rollback(status);
			throw ex;
		}
	}

	public Object loadRelationship(Object obj, Object nestedObject, PropertyMetadata propertyMetadata)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Throwable {

		boolean foreignTableRelationship = (RelationshipMetadata.class.isAssignableFrom(propertyMetadata.getClass()))
				&& ((RelationshipMetadata) propertyMetadata).isJoinTableRelationship();
		if (foreignTableRelationship) {
			RelationshipMetadata relationshipTable = (RelationshipMetadata) propertyMetadata;
			String columnName = relationshipTable.getColumnName();
			Set<String> whereColumns = new HashSet<String>();
			whereColumns.add(columnName);
			String query = sqlUtil.query(relationshipTable.getTableName(), whereColumns);

			Map<String, Object> whereParameters = new HashMap<String, Object>();
			whereParameters.put(columnName, PropertyUtils.getProperty(obj, "id"));

			String otherSideColumnName = relationshipTable.getOtherSideColumnName();
			List<Long> ids = dbService.findList(query, whereParameters, new JoinTableRowMapper(otherSideColumnName));
			List list = null;
			if(ids != null && !ids.isEmpty()) {
				list = findListIn(relationshipTable.getEntityClazz(), ids);
			}
			return list;
		}

		boolean foreignKeyRelationship = (RelationshipMetadata.class.isAssignableFrom(propertyMetadata.getClass()))
				&& !((RelationshipMetadata) propertyMetadata).isJoinTableRelationship();
		if (foreignKeyRelationship) {
			RelationshipMetadata relationshipForeignKey = (RelationshipMetadata) propertyMetadata;
			Long id = null;
			if (relationshipForeignKey.isResponsible()) {
				id = (Long) PropertyUtils.getProperty(nestedObject, "id");
				Object one = findOne(propertyMetadata.getJavaType(), id);
				return one;
			} else {
				Map<String, Object> whereParameters = new HashMap<String, Object>();
				whereParameters.put(relationshipForeignKey.getOtherSidePropertyName(),
						PropertyUtils.getProperty(obj, "id"));
				if (Collection.class.isAssignableFrom(relationshipForeignKey.getJavaType())) {
					List list = findList(relationshipForeignKey.getEntityClazz(), whereParameters);
					return list;
				} else {
					Object one = findOne(propertyMetadata.getJavaType(), whereParameters);
					return one;
				}
			}
		}

		return null; // should never be returned
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public boolean isDetached() {
		return detached;
	}

	public void setDetached(boolean detached) {
		this.detached = detached;
	}
	
	

}
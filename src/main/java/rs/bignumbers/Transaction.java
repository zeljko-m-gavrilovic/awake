package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import rs.bignumbers.metadata.RelationshipPropertyMetadata;
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
		List<T> all = findList(clazz, whereParameters);
		T one = null;
		if (all != null && !all.isEmpty()) {
			one = all.get(0);
		}
		return one;
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

	public Long insert(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(o.getClass());
		Set<String> columnNames = entityMetadata.getResponsibleProperties().stream().map(pm -> pm.getColumnName())
				.collect(Collectors.toSet());
		String sql = sqlUtil.insert(entityMetadata.getTableName(), columnNames);

		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
			PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
			String columnName = propertyMetadata.getColumnName();

			Object propertyValue = PropertyUtils.getProperty(o, propertyName);
			if ((propertyMetadata instanceof RelationshipPropertyMetadata)) {
				RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) propertyMetadata;
				if (rpm.isResponsible() && propertyValue != null) {
					propertyValue = PropertyUtils.getNestedProperty(o, propertyName + ".id");
					parameters.put(columnName, propertyValue);
				}
			} else {
				parameters.put(columnName, propertyValue);
			}
		}

		if (detached) {
			statements.add(new Statement(sql, parameters, StatementType.Insert, o));
			return Long.valueOf(-1L);
		} else {
			Long pk = dbService.insert(sql, parameters);
			logger.debug("sql insert: {} with parameters: {}", sql, parameters.toString());
			PropertyUtils.setProperty(o, "id", pk);
			return pk;
		}
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
			for (String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
				PropertyMetadata pm = entityMetadata.getPropertiesMetadata().get(propertyName);
				if ((pm instanceof RelationshipPropertyMetadata)) {
					RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) pm;
					if (rpm.isResponsible()) {
						dirtyProperties.put(propertyName, PropertyUtils.getProperty(o, propertyName + ".id"));
					}
				} else {
					dirtyProperties.put(propertyName, PropertyUtils.getProperty(o, propertyName));
				}
			}
		}
		Set<String> dirtyColumns = dirtyProperties.keySet().stream()
				.map(dirtyProperty -> entityMetadata.getPropertiesMetadata().get(dirtyProperty).getColumnName())
				.collect(Collectors.toSet());
		String sql = sqlUtil.update(entityMetadata.getTableName(), dirtyColumns, "id");

		dirtyProperties.put("id", id);
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : dirtyProperties.keySet()) {
			PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
			Object propertyValue = PropertyUtils.getProperty(o, propertyName);
			if ((propertyMetadata instanceof RelationshipPropertyMetadata) && propertyValue != null) {
				propertyValue = PropertyUtils.getNestedProperty(o, propertyName + ".id");
			}
			parameters.put(propertyMetadata.getColumnName(), propertyValue);
		}

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

	public void delete(Class clazz, Long id) {
		EntityMetadata entityMetadata = configuration.getEntityMetadata(clazz);
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
		RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) propertyMetadata;
		String tableName = rpm.getTableName();
		boolean foreignKeyRelationship = tableName == null && rpm.getOtherSideColumnName() == null;
		if (foreignKeyRelationship) {
			Long id = null;
			if (rpm.isResponsible()) {
				id = (Long) PropertyUtils.getProperty(nestedObject, "id");
			} else {
				Map<String, Object> whereParameters = new HashMap<String, Object>();
				whereParameters.put(rpm.getOtherSidePropertyName(), PropertyUtils.getProperty(obj, "id"));
				Object otherSide = findOne(rpm.getJavaType(), whereParameters);
				id = (Long) PropertyUtils.getProperty(otherSide, "id");
			}
			
			if(Collection.class.isAssignableFrom(rpm.getJavaType())){
				List<Long> ids = new ArrayList<Long>();
				ids.add(id);
				List list = findListIn(rpm.getJavaType(), ids);
				return list;
			} else {
				id = (Long) PropertyUtils.getProperty(nestedObject, "id");
				Object one = findOne(propertyMetadata.getJavaType(), id);
				return one;
			}
		} else {
			String columnName = rpm.getColumnName();
			Set<String> whereColumns = new HashSet<String>();
			whereColumns.add(columnName);
			String query = sqlUtil.query(tableName, whereColumns);

			String otherSideColumnName = rpm.getOtherSideColumnName();
			Map<String, Object> whereParameters = new HashMap<String, Object>();
			whereParameters.put(columnName, PropertyUtils.getProperty(obj, "id"));
			List<Long> ids = dbService.findList(query, whereParameters, new JoinTableRowMapper(otherSideColumnName));
			List list = findListIn(rpm.getJavaType(), ids);
			return list;
		}
	}

	public List<Statement> getStatements() {
		return statements;
	}

}
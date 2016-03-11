package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.AnnotationBasedMetadataExtractor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.PropertyMetadata;
import rs.bignumbers.metadata.RelationshipPropertyMetadata;
import rs.bignumbers.util.ProxyRegister;
import rs.bignumbers.util.SqlUtil;

public class Transaction {

	private DbService dbService;
	private ProxyRegister proxyPool;
	private List<Class> entities;
	private Map<String, EntityMetadata> entityMetadatas = new HashMap<String, EntityMetadata>();
	private SqlUtil sqlUtil;
	private AnnotationBasedMetadataExtractor metadataExtractor;
	private boolean detached = false;
	private List<Statement> statements;
	private PlatformTransactionManager txManager;

	public Transaction(List<Class> entities, DataSource datasource, PlatformTransactionManager txManager, boolean late) {
		this.sqlUtil = new SqlUtil();
		this.entities = entities;
		this.dbService = new DbService(datasource);
		this.detached = late;
		this.statements = new ArrayList<Statement>();
		this.txManager = txManager; 
		extactMetadata();
	}

	public <T> T findOne(Class<T> clazz, Long id) {
		EntityMetadata entityMetadata = getEntityMetadata(clazz);
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("id", id);
		String sql = sqlUtil.query(entityMetadata, where.keySet());
		T one = dbService.findOne(entityMetadata, sql, id);
		return one;
	}

	public <T> List<T> findList(Class<T> clazz, Map<String, Object> whereParameters) {
		EntityMetadata entityMetadata = getEntityMetadata(clazz);
		Map<String, Object> whereParams = new HashMap<String, Object>();
		String sql = sqlUtil.query(entityMetadata, whereParams.keySet());
		if (whereParams == null) {
			whereParams = new HashMap<String, Object>();
		}
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		for (String propertyName : whereParams.keySet()) {
			String columnName = entityMetadata.getPropertiesMetadata().get(propertyName).getColumnName();
			parameters.addValue(columnName, whereParams.get(propertyName));
		}

		return dbService.findList(entityMetadata, sql, whereParameters);
	}

	public Long insert(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = getEntityMetadata(o.getClass());
		String sql = sqlUtil.insert(entityMetadata);

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
			PropertyUtils.setProperty(o, "id", pk);
			return pk;
		}
	}

	public void update(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntityMetadata entityMetadata = getEntityMetadata(o.getClass());
		Long id = (Long) PropertyUtils.getProperty(o, "id");
		DirtyValueInterceptor interceptor = ProxyRegister.getInterceptor(o.getClass().getName() + "/" + id);

		Map<String, Object> dirtyProperties = null;
		if(interceptor != null) {
			dirtyProperties = interceptor.getDirtyProperties();
		} else {
			dirtyProperties = new HashMap<String, Object>();
			for(String propertyName : entityMetadata.getPropertiesMetadata().keySet()) {
				PropertyMetadata pm = entityMetadata.getPropertiesMetadata().get(propertyName);
				if ((pm instanceof RelationshipPropertyMetadata)) {
					RelationshipPropertyMetadata rpm = (RelationshipPropertyMetadata) pm;
					if(rpm.isResponsible()) {
						dirtyProperties.put(propertyName, PropertyUtils.getProperty(o, propertyName + ".id"));
					}
				} else {
					dirtyProperties.put(propertyName, PropertyUtils.getProperty(o, propertyName));
				}
			}
		}
		String sql = sqlUtil.update(entityMetadata, dirtyProperties.keySet(), "id");
		
		dirtyProperties.put("id", id);
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (String propertyName : dirtyProperties.keySet()) {
			PropertyMetadata propertyMetadata = entityMetadata.getPropertiesMetadata().get(propertyName);
			Object propertyValue = PropertyUtils.getProperty(o, propertyName);
			if((propertyMetadata instanceof RelationshipPropertyMetadata) && propertyValue != null) {
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
		EntityMetadata entityMetadata = getEntityMetadata(clazz);
		String sql = sqlUtil.delete(entityMetadata);
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
		// explicitly setting the transaction name is something that can only be done programmatically
		def.setName("AwakeTx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		TransactionStatus status = txManager.getTransaction(def);
		try {
			for(Statement statement : statements) {
				if(StatementType.Insert.equals(statement.getStatementType())) {
					Long pk = dbService.insert(statement.getSql(), statement.getParameters());
					PropertyUtils.setProperty(statement.getObject(), "id", pk);
				} else if(StatementType.Update.equals(statement.getStatementType())) {
					dbService.update(statement.getSql(), statement.getParameters());
				} else if(StatementType.Delete.equals(statement.getStatementType())) {
					dbService.delete(statement.getSql(), (Long) statement.getParameters().get("id"));
				}
			}
			txManager.commit(status);
			statements.clear();
		}
		catch (Exception ex) {
		  txManager.rollback(status);
		  throw ex;
		}
		
	}

	public void extactMetadata() {
		AnnotationBasedMetadataExtractor metadataExtractor = new AnnotationBasedMetadataExtractor();
		for (Class clazz : entities) {
			EntityMetadata em = metadataExtractor.extractMetadataForClass(clazz);
			entityMetadatas.put(clazz.getName(), em);
		}
	}

	private EntityMetadata getEntityMetadata(Class clazz) {
		if (clazz.getName().contains("$$EnhancerByCGLIB$$")) {
			clazz = clazz.getSuperclass();
		}
		EntityMetadata entityMetadata = entityMetadatas.get(clazz.getName());
		return entityMetadata;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public boolean isDetached() {
		return detached;
	}
}
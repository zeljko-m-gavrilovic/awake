package rs.bignumbers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;

import rs.bignumbers.interceptor.DirtyValueInterceptor;
import rs.bignumbers.metadata.EntityMetadata;
import rs.bignumbers.metadata.MetadataExtractor;
import rs.bignumbers.util.ProxyRegister;
import rs.bignumbers.util.SqlUtil;

//@Service
public class Transaction {

	//@Autowired
	private DbService dbService;
	private ProxyRegister proxyPool;
	private List<Class> entities;
	private Map<String, EntityMetadata> entityMetadatas = new HashMap<String, EntityMetadata>();
	private SqlUtil sqlUtil;

	public Transaction(List<Class> entities, DataSource datasource) {
		sqlUtil = new SqlUtil();
		this.entities = entities;
		dbService = new DbService(datasource);
		extactMetadata();
	}

	public void extactMetadata() {
		MetadataExtractor metadataExtractor = new MetadataExtractor();
		for (Class clazz : entities) {
			EntityMetadata em = metadataExtractor.extractMetadataForClass(clazz);
			entityMetadatas.put(clazz.getName(), em);
		}
	}

	public <T> List<T> findList(Class<T> clazz, Map<String, Object> whereParameters) {
		EntityMetadata entityMetadata = entityMetadatas.get(clazz.getName());
		return dbService.findList(entityMetadata, whereParameters);
	}

	public <T> T findOne(Class<T> clazz, Long id) {
		EntityMetadata entityMetadata = entityMetadatas.get(clazz.getName());
		return dbService.findOne(entityMetadata, id);
	}

	public Long insert(Object o) {
		EntityMetadata entityMetadata = entityMetadatas.get(o.getClass().getName());
		return dbService.insert(o, entityMetadata);
	}

	public void update(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String className = o.getClass().getName();
		EntityMetadata entityMetadata = entityMetadatas.get(className);
		Long id = (Long) PropertyUtils.getProperty(o, "id");
		DirtyValueInterceptor interceptor = ProxyRegister.getInterceptor(o.getClass().getName() + "/" + id);
		if (interceptor != null && interceptor.hasDirtyProperties()) {
			dbService.update(o, interceptor.getDirtyProperties(), entityMetadata);
		}
	}

	public void delete(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Long id = (Long) PropertyUtils.getProperty(o, "id");
		EntityMetadata entityMetadata = entityMetadatas.get(o.getClass().getName());
		dbService.delete(entityMetadata, id); 
	}
}
package rs.bignumbers.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.Relationship;

public class AnnotationMetadataExtractor {

	private final Logger logger = LoggerFactory.getLogger(AnnotationMetadataExtractor.this.getClass());

	private List<Class> entities;

	public AnnotationMetadataExtractor(List<Class> entities) {
		this.entities = entities;
	}
	
	public Map<String, EntityMetadata> extractMetadata() {
		Map<String, EntityMetadata> entityMetadatas = new HashMap<String, EntityMetadata>();
		for (Class clazz : entities) {
			EntityMetadata em = extractMetadataForClass(clazz);
			entityMetadatas.put(clazz.getName(), em);
		}
		return entityMetadatas;
	}

	public EntityMetadata extractMetadataForClass(Class clazz) {
		EntityMetadata m = new EntityMetadata();
		m.setClazz(clazz);
		boolean hasEntityAnnotation = clazz.isAnnotationPresent(Entity.class)
				&& !((Entity) clazz.getAnnotation(Entity.class)).ignore();
		if (hasEntityAnnotation) {
			Entity entityAnnotation = (Entity) clazz.getAnnotation(Entity.class);

			String tableName = entityAnnotation.tableName();
			if (tableName.length() == 0) {
				tableName = clazz.getSimpleName().toLowerCase();
			}
			m.setTableName(tableName);

			for (Field f : getAnnotatedDeclaredFields(clazz, Property.class, true)) {
				Property propertyAnnotation = f.getAnnotation(Property.class);
				if (propertyAnnotation.ignore()) {
					continue;
				}
				String columnName = propertyAnnotation.columnName();
				boolean nameExplicitellyDefined = columnName.length() > 0;
				if (!nameExplicitellyDefined) {
					columnName = f.getName();
				}
				PropertyMetadata pm = new PropertyMetadata(f.getName(), columnName, f.getType());
				m.addPropertyMetadata(pm.getPropertyName(), pm);
			}

			for (Field f : getAnnotatedDeclaredFields(clazz, Relationship.class, true)) {
				Relationship columnAnnotation = f.getAnnotation(Relationship.class);
				if (columnAnnotation.ignore()) {
					continue;
				}
				FetchType fetch = columnAnnotation.fetch();
				String columnName = columnAnnotation.columnName();
				String tn = columnAnnotation.tableName();
				boolean responsible = columnAnnotation.responsible();
				String otherSidePropertyName = columnAnnotation.otherSidePropertyName();
				String otherSideColumnName = columnAnnotation.otherSideColumnName();
				Class entityClazz = columnAnnotation.entityClazz();
				RelationshipMetadata rpm = new RelationshipMetadata(f.getName(), columnName, f.getType(), fetch,
						responsible, otherSidePropertyName, otherSideColumnName, entityClazz, tn);
				m.addPropertyMetadata(rpm.getPropertyName(), rpm);
			}
		} else {
			logger.error("class {} doesn't containt @Entity annotation", clazz);
		}
		return m;
	}

	/**
	 * Retrieving fields list of specified class If recursively is true,
	 * retrieving fields from all class hierarchy
	 *
	 * @param clazz
	 *            where fields are searching
	 * @param recursively
	 *            param
	 * @return list of fields
	 */
	public List<Field> getDeclaredFields(Class clazz, boolean recursively) {
		List<Field> fields = new LinkedList<Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		Collections.addAll(fields, declaredFields);

		Class superClass = clazz.getSuperclass();
		if (superClass != null && recursively) {
			fields.addAll(getDeclaredFields(superClass, recursively));
		}
		return fields;
	}

	/**
	 * Retrieving fields list of specified class and which are annotated by
	 * incoming annotation class If recursively is true, retrieving fields from
	 * all class hierarchy
	 *
	 * @param clazz
	 *            - where fields are searching
	 * @param annotationClass
	 *            - specified annotation class
	 * @param recursively
	 *            param
	 * @return list of annotated fields
	 */
	public List<Field> getAnnotatedDeclaredFields(Class clazz, Class<? extends Annotation> annotationClass,
			boolean recursively) {
		List<Field> allFields = getDeclaredFields(clazz, recursively);
		
		List<Field> annotatedFields = new LinkedList<Field>();
		for (Field field : allFields) {
			if (field.isAnnotationPresent(annotationClass))
				annotatedFields.add(field);
		}

		return annotatedFields;
	}
}
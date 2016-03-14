package rs.bignumbers.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.RelationshipForeignKey;
import rs.bignumbers.annotations.RelationshipForeignTable;

public class AnnotationBasedMetadataExtractor implements MetadataExtractor {

	public EntityMetadata extractMetadataForClass(Class clazz) {
		EntityMetadata m = new EntityMetadata();
		
		//clazz = getClassName(clazz);
		
		m.setClazz(clazz);
		boolean hasEntityAnnotation = clazz.isAnnotationPresent(Entity.class) && !((Entity) clazz.getAnnotation(Entity.class)).ignore();
		if (hasEntityAnnotation) {
			Entity entityAnnotation = (Entity) clazz.getAnnotation(Entity.class);
			
			String tableName = entityAnnotation.tableName();
			if (tableName.length() == 0) {
				tableName = clazz.getSimpleName().toLowerCase();
			}
			m.setTableName(tableName);
			for (Field f : getAnnotatedDeclaredFields(clazz, Property.class, true)) {
				Property propertyAnnotation = f.getAnnotation(Property.class);
				if(propertyAnnotation.ignore()) {
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
			
			for (Field f : getAnnotatedDeclaredFields(clazz, RelationshipForeignKey.class, true)) {
				RelationshipForeignKey columnAnnotation = f.getAnnotation(RelationshipForeignKey.class);
				if(columnAnnotation.ignore()) {
					continue;
				}
				String columnName = columnAnnotation.columnName();
				FetchType fetch = columnAnnotation.fetch();
				boolean responsible = columnAnnotation.responsible();
				RelationshipPropertyMetadata rpm = new RelationshipPropertyMetadata(f.getName(), columnName, f.getType(), fetch, responsible, null, null);
				m.addPropertyMetadata(rpm.getPropertyName(), rpm);
			}
			
			for (Field f : getAnnotatedDeclaredFields(clazz, RelationshipForeignTable.class, true)) {
				RelationshipForeignTable columnAnnotation = f.getAnnotation(RelationshipForeignTable.class);
				if(columnAnnotation.ignore()) {
					continue;
				}
				String columnName = columnAnnotation.columnName();
				FetchType fetch = columnAnnotation.fetch();
				boolean responsible = columnAnnotation.responsible();
				String joinTableName = columnAnnotation.columnName();
				String otherSideColumnName = columnAnnotation.otherSideColumnName();
				RelationshipPropertyMetadata rpm = new RelationshipPropertyMetadata(f.getName(), columnName, f.getType(), fetch, responsible, joinTableName, otherSideColumnName);
				m.addPropertyMetadata(rpm.getPropertyName(), rpm);
			}
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
	public static Field[] getDeclaredFields(Class clazz, boolean recursively) {
		List<Field> fields = new LinkedList<Field>();
		Field[] declaredFields = clazz.getDeclaredFields();
		Collections.addAll(fields, declaredFields);

		Class superClass = clazz.getSuperclass();

		if (superClass != null && recursively) {
			Field[] declaredFieldsOfSuper = getDeclaredFields(superClass, recursively);
			if (declaredFieldsOfSuper.length > 0)
				Collections.addAll(fields, declaredFieldsOfSuper);
		}

		return fields.toArray(new Field[fields.size()]);
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
	public static Field[] getAnnotatedDeclaredFields(Class clazz, Class<? extends Annotation> annotationClass,
			boolean recursively) {
		Field[] allFields = getDeclaredFields(clazz, recursively);
		List<Field> annotatedFields = new LinkedList<Field>();

		for (Field field : allFields) {
			if (field.isAnnotationPresent(annotationClass))
				annotatedFields.add(field);
		}

		return annotatedFields.toArray(new Field[annotatedFields.size()]);
	}

	/*public List<Class> getClasses() {
		return classes;
	}

	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}*/
}
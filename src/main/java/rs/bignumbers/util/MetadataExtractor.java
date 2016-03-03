package rs.bignumbers.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rs.bignumbers.annotations.DbColumn;
import rs.bignumbers.annotations.DbTable;

public class MetadataExtractor {

	List<Class> classes;

	public MetadataExtractor() {
	}

	/*
	 * public MetadataExtractor(List<Class> classes) { this.classes = classes; }
	 */

	public List<EntityMetadata> extractMetadata() {
		List result = new ArrayList<>();
		for (Class c : classes) {

		}
		return result;
	}

	public EntityMetadata extractMetadataForClass(Class clazz) {
		EntityMetadata m = new EntityMetadata();
		m.setClazz(clazz);
		boolean hasTableAnnotation = clazz.isAnnotationPresent(DbTable.class);
		if (hasTableAnnotation) {
			DbTable tableAnnotation = (DbTable) clazz.getAnnotation(DbTable.class);
			String tableName = tableAnnotation.name();
			if (tableName.length() == 0) {
				tableName = clazz.getSimpleName();
			}
			m.setTableName(tableName);
			for (Field f : getAnnotatedDeclaredFields(clazz, DbColumn.class, true)) {
				DbColumn columnAnnotation = f.getAnnotation(DbColumn.class);
				if(columnAnnotation.ignore()) {
					continue;
				}
				String columnName = columnAnnotation.name();
				boolean nameExplicitellyDefined = columnName.length() > 0;
				if (!nameExplicitellyDefined) {
					columnName = f.getName();
				}
				PropertyMetadata pm = new PropertyMetadata(f.getName(), columnName, f.getType());
				m.addPropertyMetadata(pm.getPropertyName(), pm);
				// }
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

	public List<Class> getClasses() {
		return classes;
	}

	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}
}
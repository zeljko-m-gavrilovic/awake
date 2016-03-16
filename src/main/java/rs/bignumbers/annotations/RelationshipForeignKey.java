package rs.bignumbers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RelationshipForeignKey {
	FetchType fetch() default FetchType.Lazy;

	String columnName() default "";
	String otherSidePropertyName() default "";
	String otherSideColumnName() default "";
	Class entityClazz() default String.class;

	/*
	 * responsible side is responsible for managing relationship between the two
	 * entities
	 */
	boolean responsible() default false;
	boolean ignore() default false;
}

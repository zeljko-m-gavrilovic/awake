package rs.bignumbers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RelationshipForeignTable {
	FetchType fetch() default FetchType.Lazy;

	String columnName() default "";
	String tableName() default "";
	String otherSideColumnName() default "";

	/*
	 * responsible side is the responsible for managing a relationship between the two entities
	 */
	boolean responsible() default false;
	boolean ignore() default false;
}

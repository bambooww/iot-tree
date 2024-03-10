package org.iottree.core.store.gdb.xorm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jason Zhu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XORMClass {
	

	String table_name();
	

	boolean inherit_parent() default false;

	String title() default "" ;

	Class<?> base_class() default Object.class;
	

}

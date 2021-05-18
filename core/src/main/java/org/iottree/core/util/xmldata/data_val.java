package org.iottree.core.util.xmldata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})  //ElementType.METHOD,
public @interface data_val
{
	/**
	 * may using another param name in xmldata
	 * @return
	 */
	String param_name() default "";
	
	boolean nullable() default true ;//
	
	boolean can_empty() default true ;
	
	boolean empty_ignore() default false;
	
	boolean extract_only() default false;
}


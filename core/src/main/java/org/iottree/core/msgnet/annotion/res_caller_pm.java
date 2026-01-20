package org.iottree.core.msgnet.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})  //ElementType.METHOD,
public @interface res_caller_pm
{
	String name();
	
	boolean required() default true;
}

package org.iottree.core.util.cache;

import java.io.* ;
import java.util.* ;

public class CacherException extends RuntimeException
{
	 public CacherException (String str)
	 {
		  super (str) ;
	 }

	 public String toString ()
	 {
		  return super.toString () ;
	 }
}
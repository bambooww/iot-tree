package org.iottree.core.basic;

import org.iottree.core.util.xmldata.XmlVal.*;

/**
 * single value transformer
 * 
 * @author jason.zhu
 *
 */
public abstract class ValTranser
{
	/**
	 * value type after transfered
	 */
	XmlValType transValTp = null ;
	
	String units = null;
	
	public abstract String getName();
	
	public abstract String getTitle() ;//may multi language
	
	
	
	
	public abstract Number transVal(Number inval) ;
	
}
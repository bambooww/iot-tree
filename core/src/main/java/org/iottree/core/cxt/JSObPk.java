package org.iottree.core.cxt;

import java.util.List;

/**
 * support outter object wrapper for JS utility
 * 
 * @author jason.zhu
 *
 */
public class JSObPk extends JSObMap
{
	private Object wrapperOb = null ;
	
	public JSObPk(Object ob)
	{
		this.wrapperOb = ob ;
	}
	
	protected List<JsMethod> JS_methods()
	{
		if(jsMethods!=null)
			return jsMethods;
		jsMethods = JsMethod.extractJsMethodsPub(wrapperOb) ;
		return jsMethods ;
	}
}

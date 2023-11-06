package org.iottree.core.plugin;

import java.util.List;

import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsMethod;

public class PlugJsApi extends JSObMap
{
	String name = null ;
	
	Object plugOb = null ;
	
	String desc = null ;
//	/**
//	 * 模式：缺省为当前类所有public函数开放api，"limited"=限定为public $$_xxx()函数
//	 */
//	String mode = null ;
	
	public PlugJsApi(String name,Object ob,String desc) //,String mode)
	{
		this.name = name ;
		this.plugOb = ob ;
		this.desc = desc ;
//		this.mode = mode ;
//		if(this.mode==null)
//			this.mode = "" ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public Object getPlugOb()
	{
		return this.plugOb ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
//	public String getMode()
//	{
//		return this.mode ;
//	}
	
	protected List<JsMethod> JS_methods()
	{
		if(jsMethods!=null)
			return jsMethods;
//		switch(this.mode)
//		{
//		case "limited":
//		default:
//			jsMethods = JsMethod.extractJsMethods(plugOb, true);
//		}
		// support extract JS_xxx() ;
		jsMethods = JsMethod.extractJsMethods(plugOb, true);
		
		return jsMethods ;
	}
}

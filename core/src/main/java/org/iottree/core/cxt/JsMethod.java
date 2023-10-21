package org.iottree.core.cxt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public class JsMethod implements ProxyExecutable
{
	Object ob = null ;
	
	Method method = null ;
	
	String name = null ;
	
	public JsMethod(Object ob,Method method)
	{
		this.ob = ob ;
		this.method = method ;
		this.method.setAccessible(true);
		this.name = method.getName() ;
		if(this.name.startsWith("JS_"))
			this.name = this.name.substring(3) ;
	}
	
	public JsMethod(Object ob,Method method,String name)
	{
		this.ob = ob ;
		this.method = method ;
		this.method.setAccessible(true);
		this.name = name ;
	}
	
	/**
	 * 
	 * @param ob
	 * @return
	 */
	public static List<JsMethod> extractJsMethods(Object ob)
	{
		ArrayList<JsMethod> rets = new ArrayList<>() ;
		Class<?> c = ob.getClass() ;
		for(Method m:c.getMethods())
		{
			HostAccess.Export exp = m.getAnnotation(HostAccess.Export.class) ;
			if(exp==null)
				continue ;
			
			JsMethod jm = new JsMethod(ob,m) ;
			rets.add(jm) ;
		}
		return rets ;
	}
	
	
	@Override
	public Object execute(Value... arguments)
	{
		try
		{
			int len = arguments.length ;
			Object[] args = new Object[arguments.length] ;
			Class<?>[] ptps = this.method.getParameterTypes() ;
			if(ptps.length!=len)
				throw new RuntimeException("JsMethod parameter is not matched") ;
			for(int i = 0 ; i < len ; i ++)
			{
				Value v = arguments[i] ;
				args[i] = v.as(ptps[i]) ;
			}
			return this.method.invoke(this.ob, args) ;
		}
		catch(Exception ee)
		{
			throw new RuntimeException(ee) ;
		}
	}

	public String getName()
	{
		return this.name ;
	}
}

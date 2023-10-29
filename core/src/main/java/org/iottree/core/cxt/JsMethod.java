package org.iottree.core.cxt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsMethod extends JsSub implements ProxyExecutable
{
	
	
	Object ob = null ;
	
	Method method = null ;
	
	
	
	public JsMethod(Object ob,Method method)
	{
		super(null,null,null) ;
		
		this.ob = ob ;
		this.method = method ;
		this.method.setAccessible(true);
		
		this.name = method.getName() ;
		if(this.name.startsWith("JS_"))
			this.name = this.name.substring(3) ;
		
		JsDef def = method.getAnnotation(JsDef.class) ;
		if(def!=null)
		{
			String n = def.name();
			if(Convert.isNotNullEmpty(n))
				this.name = n ;
			String t = def.title() ;
			if(Convert.isNotNullEmpty(t))
				this.title = t ;
			String d = def.desc();
			if(Convert.isNotNullEmpty(d))
				this.desc = d ;
		}
	}
	
	public JsMethod(Object ob,Method method,String name)
	{
		super(null,null,null) ;
		
		this.ob = ob ;
		this.method = method ;
		this.method.setAccessible(true);
		this.name = name ;
	}
	
	@Override
	public boolean hasSub()
	{
		return false;
	}
	
	public String getSubTitle()
	{
		String ret = getClassJsTitle(getReturnValTp())+" "+this.name+"(" ;
		Class<?>[] pvts = this.getParamsValTp() ;
		if(pvts!=null&&pvts.length>0)
		{
			ret += getClassJsTitle(pvts[0]) ;
			for(int i = 1 ; i < pvts.length ; i ++)
				ret+= ","+getClassJsTitle(pvts[i]) ;
		}
		ret += ")" ;
		return ret ;
	}
	
	@Override
	public String getSubIcon()
	{
		return "icon_method" ;
	}
	
	
	public Class<?> getReturnValTp()
	{
		return this.method.getReturnType();
	}
	
	public Class<?>[] getParamsValTp()
	{
		return this.method.getParameterTypes();
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

	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.putOpt("t", this.title) ;
		jo.putOpt("d", this.desc) ;
		jo.put("ret_tp", this.getReturnValTp().getCanonicalName()) ;
		JSONArray pm_tps = new JSONArray() ;
		Class<?>[] pmcs = this.getParamsValTp() ;
		if(pmcs!=null&&pmcs.length>0)
		{
			for(Class<?> c:pmcs)
				pm_tps.put(c.getCanonicalName()) ;
		}
		jo.put("param_tps", pm_tps) ;
		
		return jo ;
	}
}

package org.iottree.core.basic;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.script.Bindings;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.iottree.core.UACh;
import org.iottree.core.UANode;
import org.iottree.core.cxt.JsMethod;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

/**
 * support or limit to js object .ppp
 * @author zzj
 *
 */
public class JSObMap implements ProxyObject //extends HashMap<String,Object>
{
	public static final String SYS_HELP = "_help" ;
//	/**
//	 * override to fit JS context require
//	 */
//	@Override
//	public final Object get(Object key)
//	{
//		String pn = (String)key ;
//		return JS_get(pn) ;
//	}
	
	/**
	 * extender will override it to support sub js obj item
	 * @param pn
	 * @return
	 */
	public Object JS_get(String  key)
	{
		return null ;
	}
	
	
	/**
	 * extender will override to check value type of key
	 * normally , key that support JS_set may has type
	 * @param key
	 * @return
	 */
	public Class<?> JS_type(String key)
	{
		return null ;
	}
	/**
	 * sub class override it to support js properties(or members)
	 * @return
	 */
	public List<String> JS_names()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		rets.add(SYS_HELP) ;
		List<JsMethod> jms = JS_methods() ;
		if(jms!=null)
		{
			for(JsMethod jm:jms)
			{
				String mn = jm.getName();
				rets.add(mn) ;
			}
		}
		return rets ;
	}
	

	private JsMethod sys_help = null ;
	
	private JsMethod getSysMethod(String name)
	{
		try
		{
			switch(name)
			{
			case SYS_HELP:
				if(sys_help!=null)
					return sys_help ;
				Method m = getInnerMethod("SYS_help") ;
				sys_help = new JsMethod(this,m,"_help") ;
				return sys_help ;
			}
			
			return null ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	private Method getInnerMethod(String name)
	{
		for(Method m : JSObMap.class.getDeclaredMethods())
		{
			if(m.getName().equals(name))
				return m ;
		}
		return null ;
	}
	
	
	@SuppressWarnings("unused")
	@HostAccess.Export
	private String SYS_help()
	{
		List<String> ss = JS_names() ;
		StringBuilder sb = new StringBuilder() ;
		for(String s:ss)
		{
			sb.append(s).append("\r\n") ;
		}
		return sb.toString() ;
	}
	
	
	private List<JsMethod> jsMethods = null ; 
	
	/**
	 * extract methods that js can invoke
	 * @return
	 */
	private List<JsMethod> JS_methods()
	{
		if(jsMethods!=null)
			return jsMethods;
		jsMethods = JsMethod.extractJsMethods(this) ;
		return jsMethods ;
	}
	
	public void JS_set(String key,Object v)
	{
		
	}
	
	//--- for graalvm js engine, implements ProxyObject to support $this.xx.xx.xx expressions

	private Object transValueNumber(Value value,Class<?> tarc)
	{
		Number nv = (Number)value.as(Number.class) ;

		if(tarc==Integer.class)
			return nv.intValue();
		if(tarc==Float.class)
			return nv.floatValue();
		if(tarc==Long.class)
			return nv.longValue() ;
		if(tarc==Double.class)
			return nv.doubleValue() ;
		if(tarc==Short.class)
			return nv.shortValue();
		if(tarc==Byte.class)
			return nv.byteValue() ;
		
		return value.as(tarc) ;
	}

	private JsMethod getJsMethod(String key)
	{
		List<JsMethod> jms = JS_methods() ;
		if(jms==null)
			return null ;
		
		for(JsMethod jm:jms)
		{
			if(key.equals(jm.getName()))
				return jm ;
		}
		return null ;
	}
	
	@Override
	public final Object getMember(String key)
	{
		Object ob = JS_get(key) ;
		if(ob!=null)
			return ob ;
		JsMethod jm = getJsMethod(key) ;
		if(jm!=null)
			return jm;
		
		return getSysMethod(key);
	}
	
	private ProxyArray memKeys = null ;

	@Override
	public final Object getMemberKeys()
	{
		synchronized(this)
		{
			if(memKeys!=null)
				return memKeys;
		}
		
		List<String> ss = JS_names() ;
		if(ss==null)
			ss = Arrays.asList() ;

		ArrayList<Object> obss = new ArrayList<>() ;
		obss.addAll(ss) ;
		memKeys = ProxyArray.fromList(obss) ;
		return memKeys;
	}

	@Override
	public final boolean hasMember(String key)
	{
		List<String> jns = this.JS_names() ;
		if(jns==null)
			return false;
		return jns.contains(key) ;
		
//		return true;
	}
	
	synchronized protected final void clearJsNames()
	{
		memKeys = null ;
	}
	
	
	@Override
	public final void putMember(String key, Value value)
	{
		Object ov = null ;
		Class<?> c = JS_type(key) ;
		if(c!=null)
		{
			if(c==UnsignedInteger.class)
			{
				ov = UnsignedInteger.valueOf(value.as(Long.class)) ;
			}
			else if(c==UnsignedLong.class)
			{
				ov = UnsignedInteger.valueOf(value.as(BigInteger.class)) ;
			}
			else if(Number.class.isAssignableFrom(c))
			{
				ov = transValueNumber(value,c);
			}
			else if(c==Boolean.class)
			{
				if(value.isBoolean())
					ov = value.asBoolean() ;
				else if(value.isNumber())
					ov = value.asInt()>0;
			}
			else
				ov = value.as(c) ;
		}
		else if(value.isHostObject())
			ov = value.asHostObject();
		else
			throw new RuntimeException("unknown JS_type with key="+key) ;
		//Object ov = value.asHostObject() ;
		JS_set(key, ov);
	}
}

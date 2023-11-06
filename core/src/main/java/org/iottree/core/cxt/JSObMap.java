package org.iottree.core.cxt;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.iottree.core.UANodeOCTagsCxt;
import org.json.JSONArray;
import org.json.JSONObject;

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
	

	public void JS_set(String key,Object v)
	{
		
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
	 * sub class override it to support js properties
	 * @return
	 */
	public List<JsProp> JS_props()
	{
		ArrayList<JsProp> rets = new ArrayList<>() ;
		//rets.add(SYS_HELP) ;
		
		return rets ;
	}
	
	public final List<JsSub> JS_get_subs()
	{
		ArrayList<JsSub> rets = new ArrayList<>() ;
		List<JsProp> ss = JS_props() ;
		if(ss!=null)
			rets.addAll(ss) ;
		
		List<JsMethod> jms = JS_methods() ;
		if(jms!=null)
			rets.addAll(jms) ;
		
		return rets ;
	}
	
	public final JsSub JS_get_sub(String subname)
	{
		List<JsProp> ss = JS_props() ;
		if(ss!=null)
		{
			for(JsProp jp:ss)
			{
				if(jp.name.equals(subname))
					return jp ;
			}
		}
		
		List<JsMethod> jms = JS_methods() ;
		if(jms!=null)
		{
			for(JsMethod jm:jms)
			{
				if(jm.name.equals(subname))
					return jm ;
			}
		}
		return null ;
	}

//	private JsMethod sys_help = null ;
//	
//	private JsMethod getSysMethod(String name)
//	{
//		try
//		{
//			switch(name)
//			{
//			case SYS_HELP:
//				if(sys_help!=null)
//					return sys_help ;
//				Method m = getInnerMethod("SYS_help") ;
//				sys_help = new JsMethod(this,m,"_help") ;
//				return sys_help ;
//			}
//			
//			return null ;
//		}
//		catch(Exception ee)
//		{
//			ee.printStackTrace();
//			return null ;
//		}
//	}
	
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
	@JsDef(name="_help",title="list help",desc="list help info")
	private String SYS_help()
	{
		List<JsProp> ss = JS_props() ;
		StringBuilder sb = new StringBuilder() ;
		for(JsProp s:ss)
		{
			sb.append(s.getName()).append("\r\n") ;
		}
		return sb.toString() ;
	}
	
	/**
	 * support js doc output
	 * @return
	 */
	public final JSONObject JS_help_json()
	{
		JSONObject jo = new JSONObject() ;
		List<JsProp> ss = JS_props() ;
		
		JSONArray jarr_p = new JSONArray() ;
		for(JsProp s:ss)
		{
			jarr_p.put(s.toJO()) ;
		}
		jo.put("props", jarr_p) ;
		
		List<JsMethod> jms = JS_methods() ;
		JSONArray jarr_m = new JSONArray() ;
		if(jms!=null)
		{
			for(JsMethod jm:jms)
			{
				jarr_m.put(jm.toJO()) ;
			}
		}
		jo.put("methods", jarr_m) ;
		
		return jo;
	}
	
	protected List<JsMethod> jsMethods = null ; 
	
	/**
	 * extract methods that js can invoke
	 * @return
	 */
	protected List<JsMethod> JS_methods()
	{
		if(jsMethods!=null)
			return jsMethods;
		jsMethods = JsMethod.extractJsMethods(this) ;
		//jsMethods.add(getSysMethod(SYS_HELP)) ;
		return jsMethods ;
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
		
		//return getSysMethod(key);
		return null ;
	}
	
	private ProxyArray memKeys = null ;
	
	private HashSet<String> memNames = null ;

	@Override
	public final Object getMemberKeys()
	{
		synchronized(this)
		{
			if(memKeys!=null)
				return memKeys;
		}
		
		ArrayList<Object> obss = new ArrayList<>() ;
		HashSet<String> nameset = new HashSet<>() ;
		
		List<JsProp> ss = JS_props() ;
		if(ss==null)
			ss = Arrays.asList() ;
		for(JsProp s:ss)
		{
			String n = s.getName() ;
			obss.add(n) ;
			nameset.add(n);
		}
		
		List<JsMethod> jms = JS_methods() ;
		if(jms!=null)
		{
			for(JsMethod jm:jms)
			{
				String mn = jm.getName();
				obss.add(mn) ;
				nameset.add(mn);
			}
		}
		
		memNames = nameset ;
		memKeys = ProxyArray.fromList(obss) ;
		return memKeys;
	}
	
	
	
	public final Set<String> getMemberNames()
	{
		if(memKeys==null)
		{
			getMemberKeys();
		}
		
		return memNames ;
	}

	@Override
	public final boolean hasMember(String key)
	{
		if(memKeys==null)
		{
			getMemberKeys();
		}
		
		return memNames.contains(key) ;
//		List<String> jns = this.JS_props() ;
//		if(jns==null)
//			return false;
//		return jns.contains(key) ;
		
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

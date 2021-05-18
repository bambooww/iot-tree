package org.iottree.core.basic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.script.Bindings;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.iottree.core.UACh;
import org.iottree.core.UANode;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

/**
 * support or limit to js object .ppp
 * @author zzj
 *
 */
public class JSObMap  extends HashMap<String,Object> implements ProxyObject
{
	/**
	 * override to fit JS context require
	 */
	@Override
	public final Object get(Object key)
	{
		String pn = (String)key ;
		return JS_get(pn) ;
	}
	
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
	public List<Object> JS_names()
	{
		return null ;
	}
	
	
	public void JS_set(String key,Object v)
	{
		
	}
	
	//--- for graalvm js engine, implements ProxyObject to support $this.xx.xx.xx expressions

	
	
	@Override
	public final Object getMember(String key)
	{
//		if(key.startsWith("_"))
//		{
//			return this.JS_get(key) ;
//		}
//		return this.getSubNodeByName(key) ;
		return JS_get(key) ;
	}

	@Override
	public final Object getMemberKeys()
	{
		List<Object> ss = JS_names() ;
		if(ss==null)
			return null ;

		return ProxyArray.fromList(ss) ;
	}

	@Override
	public final boolean hasMember(String key)
	{
		List<Object> jns = this.JS_names() ;
		if(jns==null)
			return false;
		return jns.contains(key) ;
//		if(key.startsWith("_"))
//		{
//			List<Object> jns = this.JS_names() ;
//			if(jns==null)
//				return false;
//			return jns.contains(key) ;
//		}
//		return this.getSubNodeByName(key)!=null;
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

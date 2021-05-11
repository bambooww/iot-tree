package org.iottree.core.cxt;

import java.util.HashMap;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

@SuppressWarnings("serial")
public abstract class JSProxyOb extends HashMap<String, Object> implements ProxyObject
{
	// @Override
	// public boolean containsKey(Object key)
	// {
	// String pn = (String)key ;
	// boolean b = containsJSKey(pn) ;
	// if(b)
	// return true ;
	// return super.containsKey(key) ;
	// }
	//
	// @Override
	// public Object get(Object key)
	// {
	// String pn = (String)key ;
	// Object ob = getByJSKey(pn);
	// if(ob!=null)
	// return ob ;
	// Object r = super.get(key) ;
	// return r ;
	// }
	
	

	//public abstract JSProxyOb getSubJSProxyOb(String jsk);

	public void putMember(String key, Value value)
	{
		put(key, value.isHostObject() ? value.asHostObject() : value);
	}

	public boolean hasMember(String key)
	{
		return containsKey(key);
	}

	public Object getMemberKeys()
	{
		return new ProxyArray() {
			private final Object[] keys = keySet().toArray();

			public void set(long index, Value value)
			{
				throw new UnsupportedOperationException();
			}

			public long getSize()
			{
				return keys.length;
			}

			public Object get(long index)
			{
				if (index < 0 || index > Integer.MAX_VALUE)
				{
					throw new ArrayIndexOutOfBoundsException();
				}
				return keys[(int) index];
			}
		};
	}

	public Object getMember(String key)
	{
		
		return get(key);
	}

	@Override
	public boolean removeMember(String key)
	{
		if (containsKey(key))
		{
			remove(key);
			return true;
		} else
		{
			return false;
		}
	}

}
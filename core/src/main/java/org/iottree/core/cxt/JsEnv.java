package org.iottree.core.cxt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * for test or helper in node ,which will has $xxx input param support
 * 
 * @author jason.zhu
 *
 */
public class JsEnv
{
	private static ThreadLocal<JsEnv> TH_LOC = new ThreadLocal<>() ;
	
	
	public static void setInThLoc(JsEnv je)
	{
		TH_LOC.set(je);
	}
	
	public static void delInThLoc()
	{
		TH_LOC.remove(); 
	}
	
	public static JsEnv getInThLoc()
	{
		return TH_LOC.get() ;
	}
	
	LinkedHashMap<String,Object> key2ob = new LinkedHashMap<>();
	
	ArrayList<JsProp> props = new ArrayList<>() ;
	
	public JsEnv asKeyClass(String key,Class<?> c) throws InstantiationException, IllegalAccessException 
	{
		Object ob = c.newInstance() ;
		if(ob instanceof IJsProp)
			((IJsProp)ob).constructSubForCxtHelper();
		
		return asKeyOb(key, ob) ;
	}
	
	public JsEnv asKeyOb(String key,Object ob)
	{
		key2ob.put(key, ob);
		if(ob instanceof IJsProp)
		{
			IJsProp jp = (IJsProp)ob ;
			JsProp jsp = jp.toJsProp() ;
			if(jsp!=null)
				props.add(jsp) ;
		}
		return this ;
	}
	
	/**
	 * pm key=var value=classname
	 * @param pm
	 * @return
	 */
	public JsEnv asPmJO(JSONObject pm) throws Exception
	{
		for(String key:pm.keySet())
		{
			if(!key.startsWith("$"))
				continue ; //no env member
			String cn = pm.getString(key) ;
			if(Convert.isNullOrEmpty(cn))
				continue ;
			Class<?> c = Class.forName(cn) ;
			if(c==null)
				continue ;
			asKeyClass(key,c) ;
		}
		return this ;
	}
	
	public Object JS_get(String  key)
	{
		return key2ob.get(key) ;
	}
	
	public List<JsProp> JS_get_props()
	{
		return props;
	}
}

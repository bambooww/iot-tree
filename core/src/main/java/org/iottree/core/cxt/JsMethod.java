package org.iottree.core.cxt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
	public static final String JS_PREFIX = "JS_" ;
	
	Object ob = null;

	Method method = null;

	public JsMethod(Object ob, Method method)
	{
		super(null, null, null);

		this.ob = ob;
		this.method = method;
		this.method.setAccessible(true);

		this.name = method.getName();
		if (this.name.startsWith(JS_PREFIX))
			this.name = this.name.substring(3);

		JsDef def = method.getAnnotation(JsDef.class);
		if (def != null)
		{
			String n = def.name();
			if (Convert.isNotNullEmpty(n))
				this.name = n;
			String t = def.title();
			if (Convert.isNotNullEmpty(t))
				this.title = t;
			String d = def.desc();
			if (Convert.isNotNullEmpty(d))
				this.desc = d;
		}
	}

	public JsMethod(Object ob, Method method, String name)
	{
		super(null, null, null);

		this.ob = ob;
		this.method = method;
		this.method.setAccessible(true);
		this.name = name;
	}

	@Override
	public boolean hasSub()
	{
		return false;
	}
	
	private String getRetTitle()
	{
		String ret_tpstr = null;
		if ( java.util.List.class.isAssignableFrom(this.method.getReturnType()))
		{
			Type genericType = this.method.getGenericReturnType();
			if (genericType != null)
			{
				if (genericType instanceof ParameterizedType)
				{
					ParameterizedType pt = (ParameterizedType) genericType;
					Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
					ret_tpstr = getClassJsTitle(genericClazz) + "[]";
				}
			}
		}
		
		if (Convert.isNullOrEmpty(ret_tpstr))
			ret_tpstr = getClassJsTitle(getReturnValTp());
		return ret_tpstr ;
	}
	
	private List<String> getParamTitles()
	{
		Parameter[] pms = this.method.getParameters() ;
		if(pms==null||pms.length<=0)
			return null ;
		Type[] pm_tps = this.method.getGenericParameterTypes() ;
		ArrayList<String> rets =new ArrayList<>() ;
		for(int i = 0 ; i < pms.length ; i ++)
		{
			Parameter pm = pms[i] ;
			Class<?> ptp = pm.getType();
			String tpstr = null ;
			if ( java.util.List.class.isAssignableFrom(ptp))
			{
				Type genericType = pm_tps[i] ;
				if (genericType != null)
				{
					if (genericType instanceof ParameterizedType)
					{
						ParameterizedType pt = (ParameterizedType) genericType;
						Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
						tpstr = getClassJsTitle(genericClazz) + "[]";
					}
				}
			}
			if (Convert.isNullOrEmpty(tpstr))
				tpstr = getClassJsTitle(ptp);
			rets.add(tpstr) ;
		}
		return rets ;
	}
	
	public String getParamsTitle()
	{
		String ret = "(" ;
		List<String> ptps = getParamTitles() ;
		int n = 0 ;
		if (ptps != null && (n=ptps.size()) > 0)
		{
			ret += ptps.get(0);
			for (int i = 1; i < n; i++)
				ret += "," + ptps.get(i);
		}
		ret += ")";
		return ret ;
	}

	public String getSubTitle()
	{
		return getRetTitle() + " " + this.name +getParamsTitle();
	}

	@Override
	public String getSubIcon()
	{
		return "icon_method";
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
	public static List<JsMethod> extractJsMethods(Object ob,boolean chk_prefix)
	{
		ArrayList<JsMethod> rets = new ArrayList<>();
		Class<?> c = ob.getClass();
		for (Method m : c.getMethods())
		{
			HostAccess.Export exp = m.getAnnotation(HostAccess.Export.class);
			if (exp == null)
			{
				JsDef jsdef = m.getAnnotation(JsDef.class) ;
				if(jsdef==null)
				{
					if(!chk_prefix || !m.getName().startsWith(JS_PREFIX))
						continue ;
				}
			}

			JsMethod jm = new JsMethod(ob, m);
			rets.add(jm);
		}
		return rets;
	}
	
	public static List<JsMethod> extractJsMethods(Object ob)
	{
		return extractJsMethods(ob,false) ;
	}
	
	public static List<JsMethod> extractJsMethodsPub(Object ob)
	{
		ArrayList<JsMethod> rets = new ArrayList<>();
		Class<?> c = ob.getClass();
		for (Method m : c.getDeclaredMethods()) //.getMethods())
		{
			int mdf = m.getModifiers() ;
			if(!Modifier.isPublic(mdf))
				continue ;
			
			JsMethod jm = new JsMethod(ob, m);
			rets.add(jm);
		}
		return rets;
	}
	
	

	@Override
	public Object execute(Value... arguments)
	{
		try
		{
			int len = arguments.length;
			Object[] args = new Object[arguments.length];
			Class<?>[] ptps = this.method.getParameterTypes();
			if (ptps.length != len)
				throw new RuntimeException("JsMethod parameter is not matched");
			for (int i = 0; i < len; i++)
			{
				Value v = arguments[i];
				args[i] = v.as(ptps[i]);
			}
			return this.method.invoke(this.ob, args);
		}
		catch ( Exception ee)
		{
			throw new RuntimeException(ee);
		}
	}

	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("n", this.name);
		jo.putOpt("t", this.title);
		jo.putOpt("d", this.desc);
		jo.put("ret_tp", this.getReturnValTp().getCanonicalName());
		JSONArray pm_tps = new JSONArray();
		Class<?>[] pmcs = this.getParamsValTp();
		if (pmcs != null && pmcs.length > 0)
		{
			for (Class<?> c : pmcs)
				pm_tps.put(c.getCanonicalName());
		}
		jo.put("param_tps", pm_tps);

		return jo;
	}
}

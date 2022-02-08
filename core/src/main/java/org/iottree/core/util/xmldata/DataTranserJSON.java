package org.iottree.core.util.xmldata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataTranserJSON
{
	public static JSONObject extractJSONFromObj(Object o) throws Exception
	{
		JSONObject xd = new JSONObject();
		extractJSONFromObj(o, xd);
		return xd;
	}

	public static boolean injectJSONToObj(Object o, JSONObject xd) throws Exception
	{
		Class c = o.getClass();
		boolean b = false;
		do
		{
			for (Field f : c.getDeclaredFields())
			{
				data_val xmlv = f.getAnnotation(data_val.class);
				if (xmlv != null && !xmlv.extract_only())
				{
					if (jsonProp2ObjField(o, f, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = f.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (jsonObj2ObjField(o, f, xmld, xd))
						b = true;
					continue;
				}
			}
			
			for(Method m:c.getDeclaredMethods())
			{
				data_val xmlv = m.getAnnotation(data_val.class);
				if (xmlv != null&&!xmlv.extract_only())
				{
					if (jsonProp2ObjMethod(o, m, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = m.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (jsonObj2ObjMethod(o, m, xmld, xd))
						b = true;
					continue;
				}
			}
			
			c = c.getSuperclass();
			if (c == null || c.getAnnotation(data_class.class) == null)
				c = null;

		} while (c != null);

		return b;
	}
	

	private static boolean extractJSONFromObj(Object o, JSONObject xd) throws Exception
	{
		Class c = o.getClass();
		boolean b = false;
		do
		{
			for (Field f : c.getDeclaredFields())
			{
				data_val xmlv = f.getAnnotation(data_val.class);
				if (xmlv != null)
				{
					if (objField2JSONProp(o, f, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = f.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (objField2JSONObj(o, f, xmld, xd))
						b = true;
					continue;
				}
			}
			
			for (Method m : c.getDeclaredMethods())
			{
				data_val xmlv = m.getAnnotation(data_val.class);
				if (xmlv != null)
				{
					if (objMethod2JSONProp(o, m, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = m.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (objMethod2JSONObj(o, m, xmld, xd))
						b = true;
					continue;
				}
			}

			c = c.getSuperclass();
			if (c == null || c.getAnnotation(data_class.class) == null)
				c = null;

		} while (c != null);

		return b;
	}
	

	private static boolean objField2JSONProp(Object o, Field f, data_val xmlv, JSONObject jobj) throws Exception
	{
		f.setAccessible(true);
		Object pv = f.get(o);
		if (pv == null)
			return false;
		String n = f.getName();
		if (!"".contentEquals(xmlv.param_name()))
			n = xmlv.param_name();
		Class dc = f.getDeclaringClass();
		// if(List.class.isAssignableFrom(dc))
		if (pv instanceof List)
		{
			JSONArray arr = new JSONArray() ;
			for(Object tmpo : (List)pv)
			{
				arr.put(tmpo) ;
			}
			jobj.put(n, arr) ;
		}
		else
			jobj.put(n, pv);

		return true;
	}
	
	private static boolean objMethod2JSONProp(Object o, Method m, data_val xmlv, JSONObject jobj) throws Exception
	{
		m.setAccessible(true);
		String mn = m.getName() ;
		if(!mn.startsWith("get"))
			return false;
		Object pv = m.invoke(o, new Object[] {});
		if (pv == null)
			return false;
		String n =xmlv.param_name();
		if(Convert.isNullOrEmpty(n))
			return false;
		
		Class dc = m.getReturnType();
		
		// if(List.class.isAssignableFrom(dc))
		if (pv instanceof List)
		{
			JSONArray arr = new JSONArray() ;
			for(Object tmpo : (List)pv)
			{
				arr.put(tmpo) ;
			}
			jobj.put(n, arr) ;
		}
		else
			jobj.put(n, pv);

		return true;
	}
	
	private static boolean jsonProp2ObjField(Object o, Field f, data_val xmlv, JSONObject tarxd) throws Exception
	{
		f.setAccessible(true);
		String n = f.getName();
		if (!"".contentEquals(xmlv.param_name()))
			n = xmlv.param_name();
		Class dc = f.getType();
		if (List.class.isAssignableFrom(dc))
		{
			JSONArray jos = tarxd.optJSONArray(n);
			if(jos==null)
				return false;
			f.set(o, jos.toList());
		} else
		{
			Object v = tarxd.opt(n);
			if (v == null)
				return false;
			f.set(o, v);
		}

		return true;
	}
	
	private static boolean jsonProp2ObjMethod(Object o, Method m, data_val xmlv, JSONObject tarxd) throws Exception
	{
		m.setAccessible(true);
		String mn = m.getName() ;
		if(!mn.startsWith("set"))
			return false;
		String n = xmlv.param_name();
		if(Convert.isNullOrEmpty(n))
			return false;
		Parameter[] ps = m.getParameters();
		if(ps==null||ps.length!=1)
			return false;
		Class dc = ps[0].getType();
		
		if (List.class.isAssignableFrom(dc))
		{
			JSONArray jos = tarxd.optJSONArray(n);
			if(jos==null)
				return false;
			m.invoke(o, jos.toList());
		} else
		{
			Object v = tarxd.opt(n);
			if (v == null)
				return false;
			m.invoke(o, v);
		}

		return true;
	}
	
	

	private static boolean objField2JSONObj(Object o, Field f, data_obj xmld, JSONObject tarxd) throws Exception
	{
		f.setAccessible(true);

		Object pv = f.get(o);
		if (pv == null)
			return false;
		String n = f.getName();
		if (!"".contentEquals(xmld.param_name()))
			n = xmld.param_name();
		// boolean bmulti = xmld.multi() ;
		Class dc = f.getType();
		if(IJSONObj.class.isAssignableFrom(dc))
		{
			tarxd.put(n,((IJSONObj)pv).toJSONObj()) ;
		}
		else if(IJSONArr.class.isAssignableFrom(dc))
		{
			tarxd.put(n,((IJSONArr)pv).toJSONArr()) ;
		}
		else if (pv instanceof List)
		{
			JSONArray subarr = new JSONArray() ;
			tarxd.put(n, subarr) ;
			for (Object subo : (List) pv)
			{
				JSONObject subjo = new JSONObject();
				if(extractJSONFromObj(subo, subjo))
					subarr.put(subjo) ;
			}
		}
		else
		{
			JSONObject subjo = new JSONObject();
			if(extractJSONFromObj(pv, subjo))
				tarxd.put(n,subjo) ;
		}

		return true;
	}
	
	
	
	private static boolean objMethod2JSONObj(Object o, Method m, data_obj xmld, JSONObject tarxd) throws Exception
	{
		m.setAccessible(true);
		String mn = m.getName() ;
		if(!mn.startsWith("get"))
			return false;
		Object pv = m.invoke(o, new Object[] {});
		if (pv == null)
			return false;
		String n =xmld.param_name();
		if(Convert.isNullOrEmpty(n))
			return false;
		
		Class dc = m.getReturnType();
		if(IJSONObj.class.isAssignableFrom(dc))
		{
			tarxd.put(n,((IJSONObj)pv).toJSONObj()) ;
		}
		else if(IJSONArr.class.isAssignableFrom(dc))
		{
			tarxd.put(n,((IJSONArr)pv).toJSONArr()) ;
		}
		else if (pv instanceof List)
		{
			JSONArray subarr = new JSONArray() ;
			tarxd.put(n, subarr) ;
			for (Object subo : (List) pv)
			{
				JSONObject subjo = new JSONObject();
				if(extractJSONFromObj(subo, subjo))
					subarr.put(subjo) ;
			}
		}
		else
		{
			JSONObject subjo = new JSONObject();
			if(extractJSONFromObj(pv, subjo))
				tarxd.put(n,subjo) ;
		}

		return true;
	}

	private static boolean jsonObj2ObjField(Object o, Field f, data_obj xmld, JSONObject tarxd) throws Exception
	{
		f.setAccessible(true);

		String n = f.getName();
		if (!"".contentEquals(xmld.param_name()))
			n = xmld.param_name();
		Class dc = f.getType();
		Object subo = xmld.obj_c().getConstructor().newInstance();
		if(IJSONObj.class.isAssignableFrom(dc))
		{
			JSONObject subjo = tarxd.optJSONObject(n) ;
			if(subjo==null)
				return false;
			if(((IJSONObj)subo).fromJSONObj(subjo))
				f.set(o, subo);
		}
		else if(IJSONArr.class.isAssignableFrom(dc))
		{
			JSONArray subjarr = tarxd.optJSONArray(n) ;
			if(subjarr==null)
				return false;
			if(((IJSONArr)subo).fromJSONArr(subjarr))
				f.set(o, subo);
		}
		else if (List.class.isAssignableFrom(dc))
		{
			JSONArray subjarr = tarxd.optJSONArray(n) ;
			if(subjarr==null)
				return false;
			ArrayList vs = new ArrayList();
			int subn = subjarr.length() ;
			for (int i = 0 ; i < subn ; i ++)
			{
				subo = xmld.obj_c().getConstructor().newInstance();
				JSONObject tmpjo = subjarr.getJSONObject(i);
				if(injectJSONToObj(subo, tmpjo))
					vs.add(subo);
			}
			f.set(o, vs);
		} else
		{
			JSONObject subjo = tarxd.optJSONObject(n);
			if (subjo == null)
				return false;
			if (injectJSONToObj(subo, subjo))
				f.set(o, subo);
		}

		return true;
	}
	
	private static boolean jsonObj2ObjMethod(Object o, Method m, data_obj xmld, JSONObject tarxd) throws Exception
	{
		m.setAccessible(true);
		String mn = m.getName() ;
		if(!mn.startsWith("set"))
			return false;
		String n = xmld.param_name();
		if(Convert.isNullOrEmpty(n))
			return false;
		Parameter[] ps = m.getParameters();
		if(ps==null||ps.length!=1)
			return false;
		Class dc = ps[0].getType();
		
		Object subo = xmld.obj_c().getConstructor().newInstance();
		if(IJSONObj.class.isAssignableFrom(dc))
		{
			JSONObject subjo = tarxd.optJSONObject(n) ;
			if(subjo==null)
				return false;
			if(((IJSONObj)subo).fromJSONObj(subjo))
				m.invoke(o, subo);
		}
		else if(IJSONArr.class.isAssignableFrom(dc))
		{
			JSONArray subjarr = tarxd.optJSONArray(n) ;
			if(subjarr==null)
				return false;
			if(((IJSONArr)subo).fromJSONArr(subjarr))
				m.invoke(o, subo);
		}
		else if (List.class.isAssignableFrom(dc))
		{
			JSONArray subjarr = tarxd.optJSONArray(n) ;
			if(subjarr==null)
				return false;
			ArrayList vs = new ArrayList();
			int subn = subjarr.length() ;
			for (int i = 0 ; i < subn ; i ++)
			{
				subo = xmld.obj_c().getConstructor().newInstance();
				JSONObject tmpjo = subjarr.getJSONObject(i);
				if(injectJSONToObj(subo, tmpjo))
					vs.add(subo);
			}
			m.invoke(o, vs);
		}
		else
		{
			JSONObject subjo = tarxd.optJSONObject(n);
			if (subjo == null)
				return false;
			if (injectJSONToObj(subo, subjo))
				m.invoke(o, subo);
		}

		return true;
	}
}

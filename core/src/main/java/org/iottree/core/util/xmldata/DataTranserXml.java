package org.iottree.core.util.xmldata;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.*;

import org.iottree.core.util.Convert;
import org.json.*;

/**
 * inject or extract from object ,which has data_class data_obj data_val annotions
 * @author zzj
 *
 */
public class DataTranserXml
{
	/**
	 * object implemtns this to make some opter after extraction or injection
	 * @author zzj
	 *
	 */
	public static interface ITranser
	{
		public void afterXmlDataExtract(XmlData xd) ;
		
		public void afterXmlDataInject(XmlData xd) ;
	}
	
	public static XmlData extractXmlDataFromObj(Object o) throws Exception
	{
		XmlData xd = new XmlData();
		extractXmlDataFromObj(o, xd);
		
		return xd;
	}
	
	
	// public static boolean injectXmlDataToObj(Object o)

	public static boolean injectXmDataToObj(Object o, XmlData xd) throws Exception
	{
		Class c = o.getClass();
		boolean b = false;
		do
		{
			for (Field f : c.getDeclaredFields())
			{
				data_val xmlv = f.getAnnotation(data_val.class);
				if (xmlv != null&&!xmlv.extract_only())
				{
					if (paramValue2ObjField(o, f, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = f.getAnnotation(data_obj.class);
				if (xmld != null)
				{
//					System.out.println("pn="+xmld.param_name());
//					if(xmld.param_name().equals("taglist"))
//					{
//						System.out.println("taglist");
//					}
					if (subXmlData2ObjField(o, f, xmld, xd))
						b = true;
					continue;
				}
			}
			
			for(Method m:c.getDeclaredMethods())
			{
				data_val xmlv = m.getAnnotation(data_val.class);
				if (xmlv != null&&!xmlv.extract_only())
				{
					if (paramValue2ObjMethod(o, m, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = m.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (subXmlData2ObjMethod(o, m, xmld, xd))
						b = true;
					continue;
				}
			}
			
			c = c.getSuperclass();
			if (c == null || c.getAnnotation(data_class.class) == null)
				c = null;

		} while (c != null);
		if(o instanceof ITranser)
		{
			((ITranser)o).afterXmlDataInject(xd);
		}
		return b;
	}

	@SuppressWarnings("unchecked")
	private static boolean extractXmlDataFromObj(Object o, XmlData xd) throws Exception
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
					if (objField2ParamValue(o, f, xmlv, xd))
						b = true;
					continue;
				}

				data_obj xmld = f.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (objField2SubXmlData(o, f, xmld, xd))
						b = true;
					continue;
				}
			}
			
			for(Method m:c.getDeclaredMethods())
			{
				data_val dv = m.getAnnotation(data_val.class);
				if (dv != null)
				{
					if (objMethod2ParamValue(o, m, dv, xd))
						b = true;
					continue;
				}
				
				data_obj xmld = m.getAnnotation(data_obj.class);
				if (xmld != null)
				{
					if (objMethod2SubXmlData(o, m, xmld, xd))
						b = true;
					continue;
				}
			}

			c = c.getSuperclass();
			if (c == null || c.getAnnotation(data_class.class) == null)
				c = null;
		} while (c != null);
		if(o instanceof ITranser)
		{
			((ITranser)o).afterXmlDataExtract(xd);
		}
		return b;
	}
	

	private static boolean objField2ParamValue(Object o, Field f, data_val xmlv, XmlData tarxd) throws Exception
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
			tarxd.setParamValues(n, (List) pv);
		// else if(dc.isArray())
		// tarxd.setParamValues(n, v);
		else
			tarxd.setParamValue(n, pv);

		return true;
	}
	
	private static boolean objMethod2ParamValue(Object o, Method m, data_val xmlv, XmlData tarxd) throws Exception
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
			tarxd.setParamValues(n, (List) pv);
		// else if(dc.isArray())
		// tarxd.setParamValues(n, v);
		else
			tarxd.setParamValue(n, pv);

		return true;
	}
	
	
	
//	private static boolean objField2JSONProp(Object o, Field f, xml_val xmlv, JSONObject tarxd) throws Exception
//	{
//		f.setAccessible(true);
//		Object pv = f.get(o);
//		if (pv == null)
//			return false;
//		String n = f.getName();
//		if (!"".contentEquals(xmlv.param_name()))
//			n = xmlv.param_name();
//		Class dc = f.getDeclaringClass();
//		// if(List.class.isAssignableFrom(dc))
//		if (pv instanceof List)
//		{
//			JSONArray jsarr = new JSONArray() ;
//			for()
//			tarxd.put(n, (List) pv);
//		}
//		// else if(dc.isArray())
//		// tarxd.setParamValues(n, v);
//		else
//			tarxd.setParamValue(n, pv);
//
//		return true;
//	}

	private static boolean paramValue2ObjField(Object o, Field f, data_val xmlv, XmlData tarxd) throws Exception
	{
		f.setAccessible(true);
		String n = f.getName();
		if (!"".contentEquals(xmlv.param_name()))
			n = xmlv.param_name();
		Class dc = f.getType();
		if (List.class.isAssignableFrom(dc))
		{
			List<Object> vs = tarxd.getParamValues(n);
			if (vs == null)
				return false;
			f.set(o, vs);
		} else
		{
			Object v = tarxd.getParamValue(n);
			if (v == null)
				return false;
			f.set(o, v);
		}

		return true;
	}
	
	private static boolean paramValue2ObjMethod(Object o, Method m, data_val xmlv, XmlData tarxd) throws Exception
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
			List<Object> vs = tarxd.getParamValues(n);
			if (vs == null)
				return false;
			m.invoke(o, vs);
		}else
		{
			Object v = tarxd.getParamValue(n);
			if (v == null)
				return false;
			m.invoke(o, v) ;
		}

		return true;
	}

	private static boolean objField2SubXmlData(Object o, Field f, data_obj xmld, XmlData tarxd) throws Exception
	{
		f.setAccessible(true);

		Object pv = f.get(o);
		if (pv == null)
			return false;
		String n = f.getName();
		if (!"".contentEquals(xmld.param_name()))
			n = xmld.param_name();
		// boolean bmulti = xmld.multi() ;
		if (pv instanceof List)
		{
			List<XmlData> subxds = tarxd.getOrCreateSubDataArray(n);
			for (Object subo : (List) pv)
			{
				XmlData subxd = new XmlData();
				if (extractXmlDataFromObj(subo, subxd))
					subxds.add(subxd);
			}
		} else
		{
			XmlData tmpxd = new XmlData();
			if (extractXmlDataFromObj(pv, tmpxd))
				tarxd.setSubDataSingle(n, tmpxd);
		}

		return true;
	}
	
	private static boolean objMethod2SubXmlData(Object o, Method m, data_obj datao, XmlData tarxd) throws Exception
	{
		m.setAccessible(true);
		String mn = m.getName() ;
		if(!mn.startsWith("get"))
			return false;
		Object pv = m.invoke(o, new Object[] {});
		if (pv == null)
			return false;
		String n =datao.param_name();
		if(Convert.isNullOrEmpty(n))
			return false;
		// boolean bmulti = xmld.multi() ;
		if (pv instanceof List)
		{
			List<XmlData> subxds = tarxd.getOrCreateSubDataArray(n);
			for (Object subo : (List) pv)
			{
				XmlData subxd = new XmlData();
				if (extractXmlDataFromObj(subo, subxd))
					subxds.add(subxd);
			}
		} else
		{
			XmlData tmpxd = new XmlData();
			if (extractXmlDataFromObj(pv, tmpxd))
				tarxd.setSubDataSingle(n, tmpxd);
		}

		return true;
	}

	private static boolean subXmlData2ObjField(Object o, Field f, data_obj xmld, XmlData tarxd) throws Exception
	{
		f.setAccessible(true);

		String n = f.getName();
		if (!"".contentEquals(xmld.param_name()))
			n = xmld.param_name();
		Class dc = f.getType();
		if (List.class.isAssignableFrom(dc))
		{
			List<XmlData> subxds = tarxd.getOrCreateSubDataArray(n);
			if (subxds == null)
				return false;
			ArrayList vs = new ArrayList();
			for (XmlData subxd : subxds)
			{
				Object subo = xmld.obj_c().getConstructor().newInstance();
				if (injectXmDataToObj(subo, subxd))
				{
					if(subo instanceof data_obj_checker)
					{
						if(!((data_obj_checker)subo).check_obj())
							continue ;
					}
					vs.add(subo);
				}

			}
			f.set(o, vs);
		} else
		{
			XmlData subxd = tarxd.getSubDataSingle(n);
			if (subxd == null)
				return false;
			Object subo = xmld.obj_c().getConstructor().newInstance();
			if (injectXmDataToObj(subo, subxd))
			{
				if(subo instanceof data_obj_checker)
				{
					if(((data_obj_checker)subo).check_obj())
						f.set(o, subo);
				}
				else
					f.set(o, subo);
			}
		}

		return true;
	}

	private static boolean subXmlData2ObjMethod(Object o, Method m, data_obj xmld, XmlData tarxd) throws Exception
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
		
		if (List.class.isAssignableFrom(dc))
		{
			List<XmlData> subxds = tarxd.getOrCreateSubDataArray(n);
			if (subxds == null)
				return false;
			ArrayList vs = new ArrayList();
			for (XmlData subxd : subxds)
			{

				Object subo = xmld.obj_c().getConstructor().newInstance();
				if (injectXmDataToObj(subo, subxd))
					vs.add(subo);

			}
			m.invoke(o, vs);
		} else
		{
			XmlData subxd = tarxd.getSubDataSingle(n);
			if (subxd == null)
				return false;
			Object subo = xmld.obj_c().getConstructor().newInstance();
			if (injectXmDataToObj(subo, subxd))
				m.invoke(o, subo);
		}

		return true;
	}

}

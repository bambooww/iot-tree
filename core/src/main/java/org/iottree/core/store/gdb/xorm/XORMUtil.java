package org.iottree.core.store.gdb.xorm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.iottree.core.store.gdb.DataColumn;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBType;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONObject;

public class XORMUtil
{
	public static interface IXORMPropFilter
	{
		public boolean checkXORMProp(XORMPropWrapper xormp) ;
	}
	
	public static final String COL_XORM_EXT = "xorm_ext";

	public static class XORMPropWrapper implements Comparable<XORMPropWrapper>
	{
		XORMProperty xormP = null;

		Member fieldOrMethod = null;
		
		boolean bSynClient = false;

		XORMPropWrapper(XORMProperty xormp, Member m)
		{
			xormP = xormp;
			fieldOrMethod = m;
		}
		
		public XORMPropWrapper()
		{}
		
		public void setPropMem(XORMProperty xormp, Member m)
		{
			xormP = xormp;
			fieldOrMethod = m;
		}

		public XORMProperty getXORMProperty()
		{
			return xormP;
		}

		public Member getFieldOrMethod()
		{
			return fieldOrMethod;
		}
		
//		public Class<?> getFieldOrMethodValType()
//		{
//			if(fieldOrMethod instanceof Field)
//			{
//				Field f = (Field)fieldOrMethod;
//				f.getType()
//			}
//		}
		
		public String getFieldOrMethodName()
		{
			return fieldOrMethod.getName();
		}

		public int compareTo(XORMPropWrapper o)
		{
			int v = xormP.order_num() - o.xormP.order_num();
			if(v!=0)
				return v ;
			return xormP.name().compareTo(o.xormP.name()) ;
		}
	}
	
	public static final String ATTRN_IN_XMLDATA = "biz_xml_data";
	
	public static final String ATTRN_OUT_XMLDATA = "biz_out_xmldata";
	
	public static void updateXmlDataFromHttpRequest(XmlData xd,
			HttpServletRequest req, String p_prefix) throws Exception
	{
		XmlData.updateXmlDataFromHttpRequest(xd,req, p_prefix) ;
	}
	
	/**
	 * �ݹ��������Ӧ��ʹ�õ�XORMProperty�������
	 * @param c
	 * @return
	 */
	public static ArrayList<Field> listXORMPropFields(Class<?> c)
	{
		return listXORMPropFields(c,null) ;
	}
	
	public static ArrayList<Field> listXORMPropFields(Class<?> c,IXORMPropFilter filter)
	{
		ArrayList<Field> rets = new ArrayList<Field>() ;
		listXORMPropFields(c,rets, filter);
		return rets ;
	}
	
	private static void listXORMPropFields(Class<?> c,ArrayList<Field> fs,IXORMPropFilter filter)
	{
		XORMClass xormc = (XORMClass) c.getAnnotation(XORMClass.class);
		
		for(Field f:c.getDeclaredFields())
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;
			XORMPropWrapper xpw = new XORMPropWrapper(xdf,f);
			if(filter!=null&&!filter.checkXORMProp(xpw))
				continue ;
			fs.add(f);
		}
		
		if(xormc!=null&&xormc.inherit_parent())
		{//�Ӹ�������ȡ
			Class<?> sc = c.getSuperclass();
			if(sc!=null)
			{
				listXORMPropFields(sc,fs,filter) ;
			}
		}
	}
	
	
	/**
	 * �ݹ��������Ӧ��ʹ�õ�XORMProperty����ķ���
	 * @param c
	 * @return
	 */
	public static ArrayList<Method> listXORMPropMethods(Class<?> c)
	{
		return listXORMPropMethods(c,null) ;
	}
	
	public static ArrayList<Method> listXORMPropMethods(Class<?> c,IXORMPropFilter filter)
	{
		ArrayList<Method> rets = new ArrayList<Method>() ;
		listXORMPropMethods(c,rets,filter);
		return rets ;
	}
	
	private static void listXORMPropMethods(Class<?> c,ArrayList<Method> ms,IXORMPropFilter filter)
	{
		XORMClass xormc = (XORMClass) c.getAnnotation(XORMClass.class);
		
		for(Method f:c.getDeclaredMethods())
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;
			XORMPropWrapper xpw = new XORMPropWrapper(xdf,f);
			if(filter!=null&&!filter.checkXORMProp(xpw))
				continue ;
			ms.add(f);
		}
		
		if(xormc!=null&&xormc.inherit_parent())
		{//�Ӹ�������ȡ
			Class<?> sc = c.getSuperclass();
			if(sc!=null)
			{
				listXORMPropMethods(sc,ms,filter) ;
			}
		}
	}
	
	
	private static Method getXORMPropMethod(Class<?> c,String methodn,Class<?>[] parms)
	{
		Method m = null;
		try
		{
			m = c.getDeclaredMethod(methodn, parms) ;
			if(m!=null)
				return m ;
		}
		catch(NoSuchMethodException nsme)
		{
			
		}
		
		XORMClass xormc = (XORMClass) c.getAnnotation(XORMClass.class);
		if(xormc!=null&&xormc.inherit_parent())
		{//�Ӹ�������ȡ
			Class<?> sc = c.getSuperclass();
			if(sc!=null)
			{
				return getXORMPropMethod(sc,methodn,parms);
			}
		}
		
		return null ;
	}
	
	public static XORMPropWrapper extractPkXORMPropWrapper(Class<?> c)
	{
		if (c == null)
			return null;

		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			if (xdf.is_pk())
				return new XORMPropWrapper(xdf, f);
		}
		return null;
	}

	public static HashMap<XORMPropWrapper, Class<?>> extractXORMProperties(Class<?> c)
	{
		if (c == null)
			return null;

		HashMap<XORMPropWrapper, Class<?>> rets = new HashMap<XORMPropWrapper, Class<?>>();

		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			String n = xdf.name();
			if (n == null || n.equals(""))
				continue;

			Class<?> dc = f.getType();
			rets.put(new XORMPropWrapper(xdf, f), dc);
		}

		for (Method m : listXORMPropMethods(c))
		{
			XORMProperty xdf = m.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			String n = xdf.name();
			if (n == null || n.equals(""))
				continue;

			String mn = m.getName();
			Class<?> dc = null;
			if (mn.startsWith("get"))
				dc = m.getReturnType();// .getType();//.getDeclaringClass<?>();
			else if (mn.startsWith("set"))
				dc = m.getParameterTypes()[0];

			if (dc == null)
				continue;

			rets.put(new XORMPropWrapper(xdf, m), dc);
		}
		return rets;
	}

	
	

	public static Object extractPkValFromObj(Object o) throws Exception
	{
		if (o == null)
			return null;

		Class<?> c = o.getClass();

//		XmlData xd = new XmlData();

		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;
			
			if(!xdf.is_pk())
				continue ;
			
			//String n = xdf.name();
			f.setAccessible(true);

			return f.get(o) ;
		}
		
		return null ;
	}
	
	
	public static JSONObject extractJSONFromObj(Object o,boolean include_transient) throws Exception
	{
		if (o == null)
			return null;

		Class<?> c = o.getClass();
		JSONObject jo = new JSONObject() ;

		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;
			
			if(!include_transient && xdf.is_transient())
				continue;

			String n = xdf.name();
			f.setAccessible(true);
			Object ov = f.get(o);
			jo.putOpt(n,ov) ;
//			IXmlStringable xs = transXV(f.get(o));
//
//			if (xs instanceof XmlVal)
//			{
//				jo.setParamXmlVal(n, (XmlVal) xs);
//			}
//			else if (xs instanceof XmlData)
//			{
//				xd.setSubDataSingle(n, (XmlData) xs);
//			}
		}

		for (Method m : listXORMPropMethods(c))
		{
			// long tst = System.currentTimeMillis();
			XORMProperty xdf = m.getAnnotation(XORMProperty.class);
			// long tet = System.currentTimeMillis();
			// System.out.println("getAnnotation cost="+(tet-tst));
			if (xdf == null)
				continue;
			
			if(xdf.is_transient())
				continue;

			String n = xdf.name();
			String mn = m.getName();
			String xxxn = mn.substring(3);
			Method tmpm = m;
			if (!mn.startsWith("get"))
			{
				//tmpm = c.getDeclaredMethod("get" + xxxn, (Class<?>[]) null);
				tmpm = getXORMPropMethod(c,"get" + xxxn, (Class<?>[]) null);
			}
			if (tmpm == null)
				continue;

			tmpm.setAccessible(true);
			Object ov = tmpm.invoke(o, (Object[]) null);
			jo.putOpt(n,ov) ;
//			IXmlStringable xs = transXV(tmpm.invoke(o, (Object[]) null));
//
//			if (xs instanceof XmlVal)
//			{
//				xd.setParamXmlVal(n, (XmlVal) xs);
//			}
//			else if (xs instanceof XmlData)
//			{
//				xd.setSubDataSingle(n, (XmlData) xs);
//			}
		}

		return jo;
	}

	/**
	 * ��XmlData��Ϣע�뵽������
	 * 
	 * @param o
	 * @param xd
	 */
	public static void injectXmlDataToObj(Object o, XmlData xd)
			throws Exception
	{
		injectXmlDataRowToObj(o, null, xd);
	}
	
	/**
	 * ��XmlData����ע�뵽��Ӧ�Ķ�����
	 * @param o
	 * @param xd
	 * @param update_cols �޶���������
	 * @throws Exception
	 */
	public static void injectXmlDataToObj(Object o, XmlData xd,String[] update_cols)
		throws Exception
	{
		injectXmlDataRowToObj(o, null, xd,update_cols);
	}

	/**
	 * ����
	 * 
	 * @param o
	 * @param dr
	 * @param xd
	 * @throws Exception
	 */
	private static void injectXmlDataRowToObj(Object o, DataRow dr, XmlData xd)
	throws Exception
	{
		injectXmlDataRowToObj(o, dr, xd,null);
	}
	
	private static void injectXmlDataRowToObj(Object o, DataRow dr, XmlData xd,String[] limit_cols)
			throws Exception
	{
		if (o == null)
			return;
		HashSet<String> limit_col_set = null;
		if(limit_cols!=null&&limit_cols.length>0)
		{
			limit_col_set = new HashSet<String>() ;
			for(String lcols:limit_cols)
			{
				limit_col_set.add(lcols.toUpperCase());
			}
		}
		// System.out.println("injectXmlDataRowToObj------\n"+xd.toXmlString());

		Class<?> c = o.getClass();
		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			//is_transient=true������ҲӦ�ü���
			
			String n = xdf.name();
			if(limit_col_set!=null&&!limit_col_set.contains(n.toUpperCase()))
				continue ;//���޶�����

			Object ov = null;
			if (dr != null && (xdf.has_col()||xdf.store_as_file()||xdf.is_transient()) && dr.hasColumn(n))
			{
				ov = dr.getValue(n);
				if (ov != null)
				{
					setFieldVal(o, f, ov);
				}
				continue;
			}
			if (xd != null)
			{
				Class<?> tmpc = f.getType();
				f.setAccessible(true);

				ov = extractObjFromXmlData(tmpc, n, xd);
				setFieldVal(o, f, ov);
				// XmlVal xv = xd.getParamXmlVal(n);
				// if (xv == XmlVal.VAL_NULL)
				// f.set(o, null);
				// else
				// {
				// ov = extractObjFromXmlData(tmpc, n, xd);
				// if (ov != null)
				// {
				// setFieldVal(o, f, ov);
				// }
				// }
				continue;
			}
			// injectXVObj(o,f,tmpc,n,xd);
		}
		
		for (Method m : listXORMPropMethods(c))
		{
			XORMProperty xdf = m.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			String n = xdf.name();

			Method tmpm = m;
			String mn = m.getName();

			if (!mn.startsWith("set"))
			{
//				tmpm = c.getDeclaredMethod("set" + mn.substring(3),
//						new Class<?>[] { m.getReturnType() });
				
				tmpm = getXORMPropMethod(c,"set" + mn.substring(3),new Class<?>[] { m.getReturnType() });
			}
			else
			{

			}
			if (tmpm == null)
				continue;

			if (dr != null && (xdf.has_col()||xdf.store_as_file()||xdf.is_transient()) && dr.hasColumn(n))
			{
				Object ov = dr.getValue(n);
				if (ov != null)
				{
					setMethodVal(o, tmpm, ov);
				}
				continue;
			}
			if (xd != null)
			{
				Class<?> tmpc = tmpm.getParameterTypes()[0];
				tmpm.setAccessible(true);

				Object ov = extractObjFromXmlData(tmpc, n, xd);
				setMethodVal(o, tmpm, ov);
				// XmlVal xv = xd.getParamXmlVal(n);
				// if (xv == XmlVal.VAL_NULL)
				// tmpm.invoke(o, new Object[] { null });
				// else
				// {
				// Object ov = extractObjFromXmlData(tmpc, n, xd);
				// if (ov != null)
				// {
				// setMethodVal(o, tmpm, ov);
				// }
				// //tmpm.invoke(o, new Object[] { ov });
				// }
				continue;
			}
		}
	}
	
	public static void injectJSONToObj(Object o, JSONObject jo)
			throws Exception
	{
		injectJSONToObj(o, jo,null);
	}
	
	
	public static void injectJSONToObj(Object o, JSONObject jo,String[] limit_cols)
			throws Exception
	{
		if (o == null)
			return;

		HashSet<String> limit_col_set = null;
		if(limit_cols!=null&&limit_cols.length>0)
		{
			limit_col_set = new HashSet<String>() ;
			for(String lcols:limit_cols)
			{
				limit_col_set.add(lcols.toUpperCase());
			}
		}
		// System.out.println("injectXmlDataRowToObj------\n"+xd.toXmlString());

		Class<?> c = o.getClass();
		for (Field f : listXORMPropFields(c))
		{
			XORMProperty xdf = f.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			//is_transient=true������ҲӦ�ü���
			
			String n = xdf.name();
			if(limit_col_set!=null&&!limit_col_set.contains(n.toUpperCase()))
				continue ;//���޶�����

			Object ov = null;
			if (jo != null && (xdf.has_col()||xdf.store_as_file()||xdf.is_transient()) )
			{
				ov = jo.opt(n);
				if (ov != null)
				{
					setFieldVal(o, f, ov);
				}
				continue;
			}
			
		}
		
		for (Method m : listXORMPropMethods(c))
		{
			XORMProperty xdf = m.getAnnotation(XORMProperty.class);
			if (xdf == null)
				continue;

			String n = xdf.name();

			Method tmpm = m;
			String mn = m.getName();

			if (!mn.startsWith("set"))
			{
//				tmpm = c.getDeclaredMethod("set" + mn.substring(3),
//						new Class<?>[] { m.getReturnType() });
				
				tmpm = getXORMPropMethod(c,"set" + mn.substring(3),new Class<?>[] { m.getReturnType() });
			}
			else
			{

			}
			if (tmpm == null)
				continue;

			if (jo != null && (xdf.has_col()||xdf.store_as_file()||xdf.is_transient()) )
			{
				Object ov =  jo.opt(n);
				if (ov != null)
				{
					setMethodVal(o, tmpm, ov);
				}
				continue;
			}
			
		}
	}

	private static void setMethodVal(Object o, Method tmpm, Object ov)
			throws Exception, InvocationTargetException
	{
		Class<?> mc = tmpm.getParameterTypes()[0];
		if (ov == null && mc.isPrimitive())
		{
			return;
		}

		tmpm.setAccessible(true);

		if (mc == Boolean.class || mc == boolean.class)
		{// ��bool��������⴦��,ʹ֮�ܹ���Ӧ����
			if (ov instanceof Number)
			{
				tmpm.invoke(o, new Object[] { ((Number) ov).intValue() > 0 });
			}
			else
			{
				tmpm.invoke(o, new Object[] { ov });
			}

			return;
		}

		if (mc == XmlData.class)
		{
			if (ov instanceof byte[])
				ov = XmlData.parseFromByteArrayUTF8((byte[]) ov);
		}
		else if (IXmlDataable.class.isAssignableFrom(mc))
		{
			IXmlDataable tmpv = (IXmlDataable) mc.newInstance();
			if (ov instanceof byte[])
				ov = XmlData.parseFromByteArrayUTF8((byte[]) ov);
			tmpv.fromXmlData((XmlData) ov);
			ov = tmpv;
		}

		tmpm.invoke(o, new Object[] { ov });
	}

	private static void setFieldVal(Object o, Field f, Object ov)
			throws Exception
	{
		Class<?> fc = f.getType();
		if (ov == null && fc.isPrimitive())
		{
			return;
		}

		f.setAccessible(true);

		// System.out.println("field class==>>>"+fc.getCanonicalName());
		if (fc == Boolean.class || fc == boolean.class)
		{// ��bool��������⴦��,ʹ֮�ܹ���Ӧ����
			if (ov instanceof Number)
			{
				f.setBoolean(o, ((Number) ov).intValue() > 0);
			}
			else if(ov instanceof String)
			{//for derby err
				f.setBoolean(o, "true".equals(ov) || "1".equals(ov)) ;
			}
			else
			{
				f.set(o, ov);
			}
			return;
		}
		
		if(ov instanceof Number)
		{
			if(fc==ov.getClass())
			{
				f.set(o,ov) ;
				return;
			}
			
			Number ovn = (Number)ov ;
			if(fc==short.class||fc==Short.class)
				f.set(o, ovn.shortValue()) ;
			else if(fc==int.class||fc==Integer.class)
				f.set(o, ovn.intValue()) ;
			else if(fc==long.class||fc==Long.class)
				f.set(o, ovn.longValue()) ;
			else if(fc==float.class||fc==Float.class)
				f.set(o, ovn.floatValue()) ;
			else if(fc==double.class||fc==Double.class)
				f.set(o, ovn.doubleValue()) ;
			else if(fc==byte.class||fc==Byte.class)
				f.set(o, ovn.byteValue()) ;
			else
				f.set(o, ovn) ;
			return ;
		}

		if (fc == XmlData.class)
		{
			if (ov instanceof byte[])
				ov = XmlData.parseFromByteArrayUTF8((byte[]) ov);
			else if(ov instanceof String)
			{
				if(null==ov || "".equals(ov))
					ov = new XmlData() ;
				else
					ov = XmlData.parseFromXmlStr((String)ov) ;
			}
		}
		else if (IXmlDataable.class.isAssignableFrom(fc))
		{
			IXmlDataable tmpv = (IXmlDataable) fc.newInstance();
			if (ov instanceof byte[])
				ov = XmlData.parseFromByteArrayUTF8((byte[]) ov);
			tmpv.fromXmlData((XmlData) ov);
			ov = tmpv;
		}
		
		f.set(o, ov);
	}

	private static Object extractObjFromXmlData(Class<?> c, String name, XmlData xd)
			throws Exception
	{
		boolean barray = false;
		if (c.isArray())
		{
			barray = true;
			c = c.getComponentType();
		}

		if (c == byte.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;
			return xv.getObjectVal();
		}
		else if (c == int.class || c == Integer.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == int.class)
					{
						int[] tmpis = new int[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Integer) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Integer[] tmpis = new Integer[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Integer) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == short.class || c == Short.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == short.class)
					{
						short[] tmpis = new short[s];
						for (short i = 0; i < s; i++)
						{
							tmpis[i] = (Short) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Short[] tmpis = new Short[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Short) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == long.class || c == Long.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == long.class)
					{
						long[] tmpis = new long[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Long) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Long[] tmpis = new Long[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Long) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == Date.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					Date[] tmpds = new Date[objs.size()];
					objs.toArray(tmpds);
					// setv = objs.toArray();
					setv = tmpds;
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == float.class || c == Float.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == float.class)
					{
						float[] tmpis = new float[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Float) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Float[] tmpis = new Float[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Float) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == double.class || c == Double.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == double.class)
					{
						double[] tmpis = new double[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Double) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Double[] tmpis = new Double[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Double) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == String.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					String[] tmpsss = new String[objs.size()];
					objs.toArray(tmpsss);
					setv = tmpsss;
					// setv = objs.toArray();
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == boolean.class || c == Boolean.class)
		{
			XmlVal xv = xd.getParamXmlVal(name);
			if (xv == null)
				return null;

			if (barray)
			{
				List<Object> objs = xv.getObjectVals();
				Object setv = null;
				if (objs != null)
				{
					int s = objs.size();
					// Object os = Array.newInstance(c, s);
					if (c == boolean.class)
					{
						boolean[] tmpis = new boolean[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Boolean) objs.get(i);
						}
						setv = tmpis;
					}
					else
					{
						Boolean[] tmpis = new Boolean[s];
						for (int i = 0; i < s; i++)
						{
							tmpis[i] = (Boolean) objs.get(i);
						}
						setv = tmpis;
					}
				}
				return setv;
			}
			else
			{
				return xv.getObjectVal();
			}
		}
		else if (c == XmlData.class)
		{
			if (barray)
			{
				throw new RuntimeException("not support XmlData[]");
			}
			else
			{
				XmlData xd00 = xd.getSubDataSingle(name);
				if(xd00==null)
					xd00 = new XmlData() ;
				return xd00 ;
			}
		}
		else if (IXmlDataable.class.isAssignableFrom(c))
		{
			if (barray)
			{
				throw new RuntimeException("not support XmlData[]");
			}

			Object setv = null;
			XmlData tmpxd0 = xd.getSubDataSingle(name);
			if (tmpxd0 != null)
			{
				IXmlDataable ret0 = (IXmlDataable) c.newInstance();
				ret0.fromXmlData(tmpxd0);
			}
			return setv;
		}
		else
		{
			throw new RuntimeException("not support XmlDataField class field:"
					+ c.getCanonicalName());
		}
	}

	/**
	 * �����ݿ��в��ҳ��Ľ��(���ܰ���xorm_ext��),�е�����װ��XORM����
	 * 
	 * @param <T>
	 * @param t
	 * @param dr
	 * @param o
	 * @throws Exception
	 */
	public static <T> void fillXORMObjByDataRow(DataRow dr, T o)
			throws Exception
	{
		//DataTable dt = dr.getBelongToTable();
		byte[] ext_cont = (byte[]) dr.getValue(COL_XORM_EXT);
		XmlData xd = null;
		if (ext_cont != null && ext_cont.length > 0)
			xd = XmlData.parseFromByteArrayUTF8(ext_cont);

		injectXmlDataRowToObj(o, dr, xd);
	}

	/**
	 * ����xorm��,��ö�Ӧ��JavaTableInfo����
	 * 
	 * @param xorm_class
	 * @return
	 */
//	public static JavaTableInfo extractJavaTableInfo(Class<?> xorm_class,
//			StringBuilder failedreson)
//	{
//		return extractJavaTableInfo(xorm_class,false,
//				failedreson) ;
//	}
	
	public static JavaTableInfo extractJavaTableInfo(Class<?> xorm_class,
			StringBuilder failedreson)
	{
		XORMClass xormc = (XORMClass) xorm_class.getAnnotation(XORMClass.class);
		if (xormc == null)
		{
			failedreson.append("no XORMClass annotion found in Class<?>");
			return null;
		}

		String tablen = xormc.table_name();
		if (tablen == null || tablen.equals(""))
		{
			failedreson.append("no table name in Class<?> annotion XORMClass");
			return null;
		}

		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		HashMap<XORMPropWrapper, Class<?>> xormp2c = extractXORMProperties(xorm_class);
		if (xormp2c == null || xormp2c.size() <= 0)
		{
			failedreson.append("no XORMProperty found in Class<?>");
			return null;
		}

		boolean has_ext_blob = false;

		XORMPropWrapper[] xormpws = new XORMPropWrapper[xormp2c.size()];
		xormp2c.keySet().toArray(xormpws);
		Arrays.sort(xormpws);

		for (XORMPropWrapper pw : xormpws)
		{
			XORMProperty p = pw.getXORMProperty();
			
			//transient=true�����������
			if(p.is_transient())
				continue;
			
			Class<?> c = xormp2c.get(pw);

			if (!p.has_col()&&!p.store_as_file())
			{
				has_ext_blob = true;
				continue;
			}
			
			if(p.store_as_file())
			{
				continue ;
			}
			
			boolean b_read_on_demand = p.read_on_demand() ;
			boolean b_update_as_single = p.update_as_single() ;

			String coln = p.name();
			if (coln.toLowerCase().startsWith("xorm"))
			{
				failedreson
						.append("XORMProperty column["+coln+"] name cannot start with [xorm],it is reserved for inner use!");
				return null;
			}

			XmlVal.XmlValType xvt = null;
			
			int max_len = p.max_len()*2 ;

			if (XmlData.class == c || IXmlDataable.class.isAssignableFrom(c))
			{
				xvt = XmlVal.XmlValType.vt_byte_array;
			}
			else
			{
				xvt = XmlVal.class2VT(c);

				if (xvt == XmlVal.XmlValType.vt_string)// ||xvt==XmlVal.XmlValType.vt_byte_array)
				{
					if(p.is_pk()&&p.is_auto())
					{
						max_len = IdCreator.MAX_ID_LEN ;
					}
					
					if (max_len <= 0)
					{
						failedreson.append(p.name() + " in "
								+ c.getCanonicalName()
								+ " String or ByteArray must has max_len > 0!");
						return null;
					}
				}
			}

			JavaColumnInfo jci = new JavaColumnInfo(coln,p.is_pk(), xvt, max_len,
					p.has_idx(), p.is_unique_idx(),p.idx_name(), p.is_auto(),p.auto_value_start(), p
							.default_str_val(),b_read_on_demand,b_update_as_single);

			if (p.is_pk())
				pkcol = jci;
			else
				norcols.add(jci);

			if (p.has_fk())
			{
				if(p.fk_base_class())
				{//����������������,ͨ�������������Ҷ�Ӧ������pk��Ϊ�������
					Class<?> bc = xormc.base_class();
					XORMClass bc_xormc = (XORMClass)bc.getAnnotation(XORMClass.class);
					if(bc_xormc==null)
						throw new RuntimeException("Class<?> ["+bc.getCanonicalName()+"] has no XORMClass annotation!");
					
					XORMPropWrapper bc_pkpw = extractPkXORMPropWrapper(bc) ;
					if(bc_pkpw==null)
						throw new RuntimeException("Class<?> ["+bc.getCanonicalName()+"] has no pk XORMProperty annotation!");
					
					fks.add(new JavaForeignKeyInfo(coln, bc_xormc.table_name(), bc_pkpw.getXORMProperty().name()));
				}
				else
				{
					fks.add(new JavaForeignKeyInfo(coln, p.fk_table(), p
							.fk_column()));
				}
			}
		}

		if (has_ext_blob)
			norcols.add(new JavaColumnInfo(COL_XORM_EXT,false,
					XmlVal.XmlValType.vt_byte_array, -1, false, false, null,false,-1,
					null,true,false));
		return new JavaTableInfo(tablen, pkcol, norcols, fks);
	}


	public static JavaTableInfo checkTableAlter(
			Class<?> xorm_class,DataTable query_dt,
			List<JavaColumnInfo> add_cols,List<JavaColumnInfo> alter_cols)
	{
		StringBuilder fsb = new StringBuilder() ;
		JavaTableInfo jti = extractJavaTableInfo(xorm_class,fsb) ;
		//
		jti.setTableName(query_dt.getTableName());
		//boolean bret = false;
		//Ŀǰֻ�����ͨ�н����ж�
		for(JavaColumnInfo jci:jti.getNorColumnInfos())
		{
			String coln = jci.getColumnName() ;
			DataColumn dc = query_dt.getColumn(coln) ;
			if(dc==null)
			{//new column found
				add_cols.add(jci) ;
				//bret = true ;
				continue ;
			}


			if(dc.getJdbcType()!=jci.getSqlValType())
			{
				//System.out.println(jti.getTableName()+" > "+jci.toLnStr()+" - "+dc.toLnStr());
				continue ;
			}
			if(jci.isNeedMaxLen() && jci.getMaxLen()>0 && dc.getPreciesion()!=jci.getMaxLen())
			{
				//System.out.println(jti.getTableName()+" > "+jci.toLnStr()+" - "+dc.toLnStr());
				continue ;
			}
		}
		return jti;
	}
	/**
	 * ����xorm��,��ȡ��������Ӧ���ݿ���sql���
	 * 
	 * @param xorm_class
	 * @return
	 */
	public static List<String> extractCreationDBSqls(String tablename,
			DBType dbt,
			Class<?> xorm_class, StringBuilder failedreson)
	{
		JavaTableInfo jti = extractJavaTableInfo(xorm_class, failedreson);
		if (jti == null)
			return null;

		if(tablename!=null&&!"".equals(tablename))
			jti.setTableName(tablename) ;
		
		XORMClass xormc = (XORMClass) xorm_class.getAnnotation(XORMClass.class);
		if (xormc == null)
			return null;
		
		return DbSql.getDbSqlByDBType(dbt).getCreationSqls(jti);
		
//		int dm = xormc.distributed_mode() ;
//		if(dm==0)
//		{//�Ƿֲ�ʽ���
//			return DbSql.getDbSqlByDBType(dbt).getCreationSqls(jti);
//		}
//		else if(dm==1)
//		{//mode1
//			if(is_distributed_proxy)//ģʽ1��������
//				return DbSql.getDbSqlByDBType(dbt).getCreationSqls(jti);
//			else//server��
//				return DbSql.getDbSqlByDBType(dbt).getCreationDistributedMode1Sqls(jti);
//		}
//		else if(dm==2)
//		{
//			return DbSql.getDbSqlByDBType(dbt).getCreationDistributedMode2Sqls(jti);
//		}
		
//		return null ;
	}
	
	public static List<String> extractCreationDBSqls(
			DBType dbt,
			Class<?> xorm_class, StringBuilder failedreson)
	{
		return extractCreationDBSqls(null,
				dbt,xorm_class, failedreson) ;
	}

	public static String getDropXORMClassTable(Class<?> xorm_c)
	{
		XORMClass xormc = (XORMClass) xorm_c.getAnnotation(XORMClass.class);
		if (xormc == null)
		{
			return null;
		}

		String tablen = xormc.table_name();
		if (tablen == null || tablen.equals(""))
		{
			return null;
		}

		return "drop table " + tablen;
	}

	public static StringBuilder getSelectByPkSql(DBType dbt, Class<?> xorm_class,
			StringBuilder failedreson)
	{
		JavaTableInfo jti = extractJavaTableInfo(xorm_class, failedreson);
		if (jti == null)
			return null;
		return DbSql.getDbSqlByDBType(dbt).getSelectByPkIdSql(jti,null);
	}

	public static StringBuffer[] getInsertWithNewIdReturnSqls(DBType dbt,
			Class<?> xorm_class, StringBuilder failedreson)
	{
		JavaTableInfo jti = extractJavaTableInfo(xorm_class, failedreson);
		if (jti == null)
			return null;
		return DbSql.getDbSqlByDBType(dbt).getInsertSqlWithNewIdReturn(jti,null);
	}
	
	
	/**
	 * ���������ƻ�����е�SupportAuto���У�����������
	 * @param xorm_c
	 * @return
	 */
	public static String[] getSupportAutoXORMColumns(Class<?> xorm_c)
	{
		HashMap<XORMPropWrapper,Class<?>> xormpws = XORMUtil.extractXORMProperties(xorm_c) ;
		if(xormpws==null)
			return null ;
		ArrayList<String> rets = new ArrayList<String>() ;
		
		for(XORMPropWrapper pw:xormpws.keySet())
		{
			XORMProperty p = pw.getXORMProperty() ;
			if(p.support_auto())
				rets.add(p.name()) ;
		}
		
		String[] rr = new String[rets.size()];
		rets.toArray(rr);
		return rr ;
	}
	

}

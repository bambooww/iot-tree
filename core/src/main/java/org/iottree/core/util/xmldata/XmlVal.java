package org.iottree.core.util.xmldata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.iottree.core.util.Convert;


public class XmlVal implements IXmlStringable
{
	public static final String VAL_TYPE_SCHEMA = "xml_schema";

	public static final String VAL_TYPE_BYTEARRAY = "byte_array";

	public static final String VAL_TYPE_DATE = "date";

	public static final String VAL_TYPE_DOUBLE = "double";

	public static final String VAL_TYPE_FLOAT = "float";

	public static final String VAL_TYPE_INT64 = "int64";

	public static final String VAL_TYPE_INT32 = "int32";

	public static final String VAL_TYPE_INT16 = "int16";

	public static final String VAL_TYPE_BYTE = "byte";

	public static final String VAL_TYPE_STR = "string";

	public static final String VAL_TYPE_BOOL = "bool";
	
	public static final String VAL_TYPE_NULL = "null";
	
	public static final String VAL_TYPE_BIGDECIMAL = "bigdecimal" ;
	
	public static final String VAL_TYPE_CHAR = "char" ;

	//public static final String VAL_TYPE_XMLDATA = "xml_data";
	
	//

	private static HashSet<String> TypesSet = new HashSet<String>();
	static
	{
		TypesSet.add(VAL_TYPE_SCHEMA);
		TypesSet.add(VAL_TYPE_BYTEARRAY);
		TypesSet.add(VAL_TYPE_DATE);
		TypesSet.add(VAL_TYPE_DOUBLE);
		TypesSet.add(VAL_TYPE_FLOAT);
		TypesSet.add(VAL_TYPE_INT64);
		TypesSet.add(VAL_TYPE_INT32);
		TypesSet.add(VAL_TYPE_INT16);
		TypesSet.add(VAL_TYPE_BYTE);
		TypesSet.add(VAL_TYPE_STR);
		TypesSet.add(VAL_TYPE_BOOL);
		TypesSet.add(VAL_TYPE_NULL);
		TypesSet.add(VAL_TYPE_CHAR);
		TypesSet.add(VAL_TYPE_BIGDECIMAL);
	}

	public static boolean isXmlValType(String type)
	{
		return TypesSet.contains(type);
	}

	static public enum XmlValType
	{
		vt_xml_schema(1),
		vt_byte_array(2), 
		vt_date(3),
		vt_double(4),
		vt_float(5),
		vt_int64(6),
		vt_int32(7),
		vt_int16(8),
		vt_byte(9),
		vt_string(10),
		vt_bool(11),
		vt_xml_data(22),
		vt_null(12),
		vt_bigdecimal(13),
		vt_char(14);
		
		private final int val ;
		
		XmlValType(int v)
		{
			val = v ;
		}
		
		public int getIntValue()
		{
			return val ;
		}
		
		public short getShortValue()
		{
			return (short)val ;
		}
		/**
		 * ������Ͷ�Ӧ���ַ���
		 * @return
		 */
		public String getTypeStr()
		{
			return this.toString().substring(3) ;
		}
		
		public boolean isNumberFloat()
		{
			return val==4||val==5 ;
		}
		
		public boolean isNumberInteger()
		{
			return val==6 || val==7 || val==8 || val==9 ;
		}
	}
	
	private static XmlValType[] valTypeInIdxs = null;
	static
	{
		valTypeInIdxs = new XmlValType[23] ;
		for(int k=0;k<valTypeInIdxs.length;k++)
			valTypeInIdxs[k] = null ;
		for(XmlValType xvt:XmlValType.values())
		{
			valTypeInIdxs[xvt.val] = xvt ;
		}
	}
	
	/**
	 * ��������ֵ����ö�Ӧ��XmlValType����
	 * @param v
	 * @return
	 */
	public static XmlValType valTypeOf(int v)
	{
		if(v<0 || v>=valTypeInIdxs.length)
			return null ;
		
		return valTypeInIdxs[v] ;
	}

	public static final String[] ALL_TYPES = new String[] { VAL_TYPE_STR,
			VAL_TYPE_BOOL, VAL_TYPE_BYTE, VAL_TYPE_INT16, VAL_TYPE_INT32,
			VAL_TYPE_INT64, VAL_TYPE_FLOAT, VAL_TYPE_DOUBLE, VAL_TYPE_DATE,
			VAL_TYPE_BYTEARRAY,VAL_TYPE_NULL,VAL_TYPE_BIGDECIMAL,VAL_TYPE_CHAR };
	
	private static HashMap<String,XmlValType> smartstr2vt = new HashMap<String,XmlValType>() ;
	
	static
	{
		smartstr2vt.put("byte[]", XmlValType.vt_byte_array) ;
		smartstr2vt.put("bytearray", XmlValType.vt_byte_array) ;
		smartstr2vt.put("bytes", XmlValType.vt_byte_array) ;
		
		smartstr2vt.put("date", XmlValType.vt_date) ;
		smartstr2vt.put("time", XmlValType.vt_date) ;
		
		smartstr2vt.put("double", XmlValType.vt_double) ;
		
		smartstr2vt.put("float", XmlValType.vt_float) ;
		smartstr2vt.put("real", XmlValType.vt_float) ;
		
		smartstr2vt.put("int64", XmlValType.vt_int64) ;
		smartstr2vt.put("long", XmlValType.vt_int64) ;
		
		smartstr2vt.put("int32", XmlValType.vt_int32) ;
		smartstr2vt.put("int", XmlValType.vt_int32) ;
		smartstr2vt.put("integer", XmlValType.vt_int32) ;
		
		smartstr2vt.put("int16", XmlValType.vt_int16) ;
		smartstr2vt.put("short", XmlValType.vt_int16) ;
		smartstr2vt.put("word", XmlValType.vt_int16) ;
		
		smartstr2vt.put("int8", XmlValType.vt_byte) ;
		smartstr2vt.put("byte", XmlValType.vt_byte) ;
		
		smartstr2vt.put("string", XmlValType.vt_string) ;
		smartstr2vt.put("str", XmlValType.vt_string) ;
		
		smartstr2vt.put("bool", XmlValType.vt_bool) ;
		smartstr2vt.put("boolean", XmlValType.vt_bool) ;
		smartstr2vt.put("bit", XmlValType.vt_bool) ;
		
		smartstr2vt.put("xmldata", XmlValType.vt_xml_data) ;
		smartstr2vt.put("xml_data", XmlValType.vt_xml_data) ;
		
		smartstr2vt.put("null", XmlValType.vt_null) ;
		smartstr2vt.put("nul", XmlValType.vt_null) ;
		smartstr2vt.put("^", XmlValType.vt_null) ;
		smartstr2vt.put("none", XmlValType.vt_null) ;
		
		smartstr2vt.put("bigdecimal", XmlValType.vt_bigdecimal) ;
		smartstr2vt.put("bigd", XmlValType.vt_bigdecimal) ;
		smartstr2vt.put("decimal", XmlValType.vt_bigdecimal) ;
		
		smartstr2vt.put("char", XmlValType.vt_char) ;
		smartstr2vt.put("chr", XmlValType.vt_char) ;
	}

	public static XmlValType StrType2ValType(String strt)
	{
		return XmlValType.valueOf("vt_" + strt);
	}

	public static String ValType2StrType(XmlValType xvt)
	{
		// return "vt_" + xvt.toString();
		return xvt.toString();
	}

	/**
	 * ���̶Ⱥܸߵ�����ת��
	 * @param strt
	 * @return
	 */
	public static XmlValType transStr2TypeSmart(String strt)
	{
		if(strt==null)
			return null ;
		
		return smartstr2vt.get(strt.toLowerCase()) ;
	}
	
	public static final String BOOL_STR_TRUE = "true";

	public static final String BOOL_STR_FALSE = "false";

	public static XmlVal createSingleVal(Object obj)
	{
		if (obj == null)
		{
			XmlVal vi = new XmlVal();
			vi.type = VAL_TYPE_NULL;
			return vi ;
		}
		else
		{
			XmlVal vi = new XmlVal();
			vi.objVal = obj;
			vi.type = obj2type(obj);
			return vi;
		}
	}
	
	public static XmlVal createSingleVal(String typestr,Object obj)
	{
		if (obj == null)
		{
			XmlVal vi = new XmlVal();
			vi.type = typestr;
			return vi ;
		}
		else
		{
			XmlVal vi = new XmlVal();
			vi.objVal = obj;
			vi.type = typestr;
			return vi;
		}
	}

	public static XmlVal createArrayVal(Object[] objs)
	{
		if (objs == null || objs.length <= 0)
			return null;

		ArrayList<Object> al = new ArrayList<Object>(objs.length);
		for (Object o : objs)
		{
			al.add(o);
		}

		return createArrayVal(al);
	}

	public static XmlVal createArrayVal(List<Object> objs)
	{
		if (objs == null || objs.size() <= 0)
			throw new IllegalArgumentException("obj cannot be null");

		XmlVal vi = new XmlVal();
		String vt = obj2type(objs.get(0));
		XmlValType xvt = StrType2ValType(vt);
		switch(xvt.val)
		{
			case T_DATE:
			case T_DOUBLE:
			case T_FLOAT:
			case T_INT64:
			case T_INT32:
			case T_INT16:
			case T_BYTE:
			case T_STR:
			case T_BOOL:
			//case T_NULL:
			case T_BIGDECIMAL:
			case T_CHAR:
				break ;
			default:
				throw new IllegalArgumentException("array inner type illegal-"+xvt.toString()) ;
		}
		vi.type = vt;
		vi.bArray = true;
		vi.objVals = objs;
		return vi;
	}
	
	public static XmlVal createArrayVal(String type,List<Object> objs)
	{
		if (objs == null)
			throw new IllegalArgumentException("obj cannot be null");

		XmlVal vi = new XmlVal();
		vi.type = type;
		vi.bArray = true;
		vi.objVals = objs;
		return vi;
	}
//	static Object transArrayObject(String vt,List<Object> objs)
//	{
//		
//	}

	public static XmlVal createSingleValByStr(String type, String strval)
	{
		return new XmlVal(type, strval);
	}
	

	public static XmlVal createArrayValByStr(String type, List<String> strvals)
	{
		return new XmlVal(type, strvals);
	}

	public static XmlVal createArrayValByStr(String type, String[] strvals)
	{
		List<String> ll = new ArrayList<String>(strvals.length);
		for (String s : strvals)
		{
			ll.add(s);
		}
		return new XmlVal(type, ll);
	}

	public static final XmlVal VAL_NULL = new XmlVal(VAL_TYPE_NULL, (String)null) ;
	
	/**
	 * 
	 */
	String type = VAL_TYPE_STR;

	boolean bArray = false;

	String strVal = null;

	// String xmlEncodedStrVal = null ;
	Object objVal = null;

	List<String> strVals = null;

	// List<String> xmlEncodedStrVals = null ;
	List<Object> objVals = null;

	public XmlVal(String type, String xmlstrval)
	{
		// if(xmlstrval==null)
		// throw new IllegalArgumentException("str val cannot be null");
		if (type == null)
			throw new IllegalArgumentException("str val type cannot be null");
		this.type = type;

		strVal = xmlstrval;
		// xmlEncodedStrVal = XmlHelper.xmlEncoding(xmlstrval) ;
	}

	public XmlVal(String type, List<String> xmlstrval_array)
	{
		// if(xmlstrval_array==null||xmlstrval_array.size()<=0)
		// throw new IllegalArgumentException("str val cannot be null");
		if (type == null)
			throw new IllegalArgumentException("str val type cannot be null");
		this.type = type;

		bArray = true;
		strVals = xmlstrval_array;
		// xmlEncodedStrVals = new ArrayList<String>(xmlstrval_array.size());
		// for(String sv : xmlstrval_array)
		// xmlEncodedStrVals.add(XmlHelper.xmlEncoding(sv)) ;
	}

	XmlVal()
	{
	}

	public XmlVal copyMe()
	{
		XmlVal xvi = new XmlVal();

		xvi.type = this.type;
		xvi.bArray = this.bArray;

		if (bArray)
		{
			xvi.strVals = new ArrayList<String>();
			xvi.strVals.addAll(this.getStrVals());
		}
		else
		{
			xvi.strVal = this.getStrVal();
		}

		return xvi;
	}
	
	public XmlVal copyMeWithNewType(String newxv_type)
	{
		if(!isXmlValType(newxv_type))
			throw new IllegalArgumentException("not xml val type="+newxv_type);
		
		XmlVal xvi = new XmlVal();

		xvi.type = newxv_type;
		xvi.bArray = this.bArray;

		if (bArray)
		{
			xvi.strVals = new ArrayList<String>();
			xvi.strVals.addAll(this.getStrVals());
			
			if(!newxv_type.equals(this.type))
				xvi.getObjectVals() ;//���Ͳ���ͬ,��������������Ƿ���ȷ
		}
		else
		{
			xvi.strVal = this.getStrVal();
			if(!newxv_type.equals(this.type))
				xvi.getObjectVal() ;//���Ͳ���ͬ,��������������Ƿ���ȷ
		}

		return xvi;
	}

	public Object getObjectVal()
	{
		if (objVal != null)
			return objVal;

		if (bArray)
			throw new RuntimeException(
					"ValItem is array,it must use getObjectVals()");

		if (strVal == null)
			return null;

		objVal = xmlEncodedStr2Obj(type, strVal, false);
		// objVal = xmlEncodedStr2Obj(type,xmlEncodedStrVal);
		return objVal;
	}
	
	public static Object transStr2ObjVal(String type,String strv)
	{
		return xmlEncodedStr2Obj(type, strv, false);
	}
	
//	public static Object transStr2ObjVal(String type,String strv)
//	{
//		return xmlEncodedStr2Obj(type, strv, false);
//	}

	public void setObjectVal(Object ov)
	{
		if (objVal == null)
		{
			objVal = null ;
			strVal = null;
			return;
		}

		objVal = ov;
		strVal = Obj2XmlEncodedStr(type, objVal, false);
		if(strVal==null)
			objVal = null ;
	}

	public List<Object> getObjectVals()
	{
		if (objVals != null)
			return objVals;

		if (!bArray)
			throw new RuntimeException(
					"ValItem is not array,it must use getObjectVal()");

		if (strVals == null)
			return null;

		objVals = new ArrayList<Object>(strVals.size());
		for (String s : strVals)
			objVals.add(xmlEncodedStr2Obj(type, s, false));

		return objVals;
	}
	
//	public Object getArrayObject()
//	{
//		
//	}

	public void setObjectVals(List<Object> ovs)
	{
		objVals = ovs;
		if (objVals == null)
		{
			strVals = null;
			return;
		}

		ArrayList<String> tmpll = new ArrayList<String>(objVals.size());

		for (Object o : objVals)
			tmpll.add(Obj2XmlEncodedStr(type, o, false));

		strVals = tmpll;
	}

	public String getStrVal()
	{
		if (strVal != null)
			return strVal;

		if (bArray)
			throw new RuntimeException(
					"ValItem is array,it must use getXmlEncodedStrVals()");

		if (objVal == null)
			return null;

		strVal = Obj2XmlEncodedStr(type, objVal, false);

		return strVal;
	}
	
	/**
	 * �ж��ַ���ֵ�Ƿ�������ֵƥ��һ��
	 * @param strv
	 * @return
	 */
	public boolean equalsSingleStrVal(String strv)
	{
		if(bArray)
			return false;
		String sv = getStrVal() ;
		if(sv==null)
		{
			return strv==null ;
		}
		
		return sv.equals(strv) ;
	}

	public void setStrVal(String strv)
	{
		if (strv == null)
		{
			strVal = null;
			objVal = null;
			return;
		}
		
		strVal = strv ;
		objVal = xmlEncodedStr2Obj(type, strVal, false);
		if(objVal==null)
			strVal = null ;
	}

	public List<String> getStrVals()
	{
		if (strVals != null)
			return strVals;

		if (!bArray)
			throw new RuntimeException(
					"ValItem is not array,it must use getXmlEncodedStrVal()");

		if (objVals == null)
			return null;

		strVals = new ArrayList<String>(objVals.size());

		for (Object o : objVals)
			strVals.add(Obj2XmlEncodedStr(type, o, false));

		return strVals;
	}

	public void setStrVals(List<String> strvals)
	{
		strVals = strvals;
		if (strvals == null)
		{
			objVals = null;
			return;
		}

		ArrayList<Object> tmpll = new ArrayList<Object>(strVals.size());
		for (String s : strVals)
			tmpll.add(xmlEncodedStr2Obj(type, s, false));
		objVals = tmpll;
	}

	public void setStrVals(String[] strvals)
	{
		if (strvals == null)
		{
			setStrVals((List<String>) null);
			return;
		}

		ArrayList<String> tmpll = new ArrayList<String>(strvals.length);
		for (String s : strvals)
			tmpll.add(s);

		setStrVals(tmpll);
	}

	// String getXmlEncodedStrVal()
	// {
	// if(xmlEncodedStrVal!=null)
	// return xmlEncodedStrVal;
	//		
	// if(bArray)
	// throw new RuntimeException("ValItem is array,it must use
	// getXmlEncodedStrVals()");
	//		
	// xmlEncodedStrVal = Obj2XmlEncodedStr(type,objVal);
	//		
	// return xmlEncodedStrVal;
	// }
	//	
	// List<String> getXmlEncodedStrVals()
	// {
	// if(xmlEncodedStrVals!=null)
	// return xmlEncodedStrVals;
	//		
	// if(!bArray)
	// throw new RuntimeException("ValItem is not array,it must use
	// getXmlEncodedStrVal()");
	//		
	// xmlEncodedStrVals = new ArrayList<String>(objVals.size());
	//		
	// for(Object o : objVals)
	// xmlEncodedStrVals.add(Obj2XmlEncodedStr(type,o));
	//		
	// return xmlEncodedStrVals;
	// }

	public String getValType()
	{
		return type;
	}
	
	
	public XmlValType getXmlValType()
	{
		return StrType2ValType(type);
	}

	public boolean isArray()
	{
		return bArray;
	}
	
	public int calCompactNotXmlLen() throws UnsupportedEncodingException
	{
		XmlValType xvt = this.getXmlValType() ;
		int r = 1 ;
		
		switch(xvt.val)
		{
			case T_BYTEARRAY:
				//������array,ֱ�ӵĳ���
				byte[] bv = (byte[])this.getObjectVal() ;
				r += 4 ;
				r += bv.length ;
				return r ;
			case T_DATE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 8 ;
				}
				else
				{
					//Date dv = (Date)this.getObjectVal() ;
					//out.write(DataUtil.longToBytes(dv.getTime())) ;
					r += 8 ;
				}
				return r ;
			case XmlVal.T_DOUBLE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 8 ;
				}
				else
				{
					r += 8 ;
				}
				return r ;
			case XmlVal.T_FLOAT:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 4 ;
				}
				else
				{
					r += 4 ;
				}
				return r ;
			case XmlVal.T_INT64:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 8 ;
				}
				else
				{
					r += 8 ;
				}
				return r ;
			case XmlVal.T_INT32:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 4 ;
				}
				else
				{
					r += 4 ;
				}
				return r ;
			case XmlVal.T_INT16:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s * 2 ;
				}
				else
				{
					r += 2 ;
				}
				return r ;
			case XmlVal.T_BYTE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s ;
				}
				else
				{
					r += 1 ;
				}
				return r ;
			case XmlVal.T_STR:
				if(bArray)
				{
					//int s = this.getObjectVals().size() ;
					r += 4 ;
					//out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						String sv = (String)tmpo ;
						byte[] svbs = sv.getBytes("UTF-8") ;
						r +=4 ;
						r += svbs.length ;
						//out.write(DataUtil.intToBytes(svbs.length)) ;
						//out.write(svbs) ;
					}
				}
				else
				{
					String sv = (String)this.getObjectVal() ;
					byte[] svbs = sv.getBytes("UTF-8");
					r += 4 ;
					r += svbs.length ;
					//out.write(DataUtil.intToBytes(svbs.length)) ;
					//out.write(svbs) ;
				}
				return r ;
			case XmlVal.T_BOOL:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					r  += 4 ;
					r += s ;
				}
				else
				{
					r += 1 ;
				}
				return r ;
			case XmlVal.T_NULL:
				if(bArray)
				{
					//int s = this.getObjectVals().size() ;
					r += 4 ;
				}
				return r ;
			case XmlVal.T_BIGDECIMAL:
				if(bArray)
				{
					//int s = this.getObjectVals().size() ;
					//out.write(DataUtil.intToBytes(s)) ;//num
					r += 4 ;
					for(Object tmpo:objVals)
					{
						byte[] dv = tmpo.toString().getBytes() ;
						//out.write(DataUtil.intToBytes(dv.length)) ;
						//out.write(dv) ;
						r += 4 ;
						r += dv.length ;
					}
				}
				else
				{
					byte[] dv = this.getObjectVal().toString().getBytes() ;
					//out.write(DataUtil.intToBytes(dv.length)) ;
					//out.write(dv) ;
					r += 4 ;
					r += dv.length ;
				}
				return r ;
			default:
				throw new RuntimeException("illegal xml val type="+xvt.toString()) ;
		}
	}
	
	void writeCompactToStream(OutputStream out) throws IOException
	{//���ͣ�1�ֽڣ���������ֵ+100��
		XmlValType xvt = this.getXmlValType() ;
		if(this.bArray)
			out.write(xvt.val+50) ;//����50��ֵ��ʾ����
		else
			out.write(xvt.val) ;
		
		switch(xvt.val)
		{
			case T_BYTEARRAY:
				//������array,ֱ�ӵĳ���
				byte[] bv = (byte[])this.getObjectVal() ;
				out.write(DataUtil.intToBytes(bv.length)) ;
				out.write(bv) ;
				break ;
			case T_DATE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Date dv = (Date)tmpo ;
						out.write(DataUtil.longToBytes(dv.getTime())) ;
					}
				}
				else
				{
					Date dv = (Date)this.getObjectVal() ;
					out.write(DataUtil.longToBytes(dv.getTime())) ;
				}
				break ;
			case XmlVal.T_DOUBLE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Double dv = (Double)tmpo ;
						out.write(DataUtil.doubleToBytes(dv)) ;
					}
				}
				else
				{
					Double dv = (Double)this.getObjectVal() ;
					out.write(DataUtil.doubleToBytes(dv)) ;
				}
				break ;
			case XmlVal.T_FLOAT:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Float dv = (Float)tmpo ;
						out.write(DataUtil.floatToBytes(dv)) ;
					}
				}
				else
				{
					Float dv = (Float)this.getObjectVal() ;
					out.write(DataUtil.floatToBytes(dv)) ;
				}
				break ;
			case XmlVal.T_INT64:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Long dv = (Long)tmpo ;
						out.write(DataUtil.longToBytes(dv)) ;
					}
				}
				else
				{
					Long dv = (Long)this.getObjectVal() ;
					out.write(DataUtil.longToBytes(dv)) ;
				}
				break ;
			case XmlVal.T_INT32:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Integer dv = (Integer)tmpo ;
						out.write(DataUtil.intToBytes(dv)) ;
					}
				}
				else
				{
					Integer dv = (Integer)this.getObjectVal() ;
					out.write(DataUtil.intToBytes(dv)) ;
				}
				break ;
			case XmlVal.T_INT16:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Short dv = (Short)tmpo ;
						out.write(DataUtil.shortToBytes(dv)) ;
					}
				}
				else
				{
					Short dv = (Short)this.getObjectVal() ;
					out.write(DataUtil.shortToBytes(dv)) ;
				}
				break ;
			case XmlVal.T_BYTE:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Byte dv = (Byte)tmpo ;
						out.write(dv) ;
					}
				}
				else
				{
					Byte dv = (Byte)this.getObjectVal() ;
					out.write(dv) ;
				}
				break ;
			case XmlVal.T_STR:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						String sv = (String)tmpo ;
						byte[] svbs = sv.getBytes("UTF-8") ;
						out.write(DataUtil.intToBytes(svbs.length)) ;
						out.write(svbs) ;
					}
				}
				else
				{
					String sv = (String)this.getObjectVal() ;
					byte[] svbs = sv.getBytes("UTF-8");
					out.write(DataUtil.intToBytes(svbs.length)) ;
					out.write(svbs) ;
				}
				break ;
			case XmlVal.T_BOOL:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						Boolean dv = (Boolean)tmpo ;
						out.write(DataUtil.booleanToByte(dv)) ;
					}
				}
				else
				{
					Boolean dv = (Boolean)this.getObjectVal() ;
					out.write(DataUtil.booleanToByte(dv)) ;
				}
				break ;
			case XmlVal.T_NULL:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;
				}
				break ;
			case XmlVal.T_BIGDECIMAL:
				if(bArray)
				{
					int s = this.getObjectVals().size() ;
					out.write(DataUtil.intToBytes(s)) ;//num
					for(Object tmpo:objVals)
					{
						byte[] dv = tmpo.toString().getBytes() ;
						out.write(DataUtil.intToBytes(dv.length)) ;
						out.write(dv) ;
					}
				}
				else
				{
					byte[] dv = this.getObjectVal().toString().getBytes() ;
					out.write(DataUtil.intToBytes(dv.length)) ;
					out.write(dv) ;
				}
				break ;
			default:
				throw new IOException("illegal xml val type="+xvt.toString()) ;
		};
	}
	
	private int read4BytesLen(int datalen,InputStream in) throws IOException
	{
		int len = DataUtil.readInt(in) ;
		if(len<0)
			throw new IllegalArgumentException("illegal len <0") ;
		if(len>datalen)
			throw new IllegalArgumentException("illegal len is too big;len="+len) ;
		return len ;
	}
	
	void readCompactFromStream(int datalen,InputStream in) throws IOException
	{
		int b = in.read() ;
		//XmlValType xvt = this.getXmlValType() ;
		if(b>50)
		{
			this.bArray = true ;
			b -= 50 ;
		}
	
		int len ;
		switch(b)
		{
			case T_BYTEARRAY:
				//������array,ֱ�ӵĳ���
				this.type = VAL_TYPE_BYTEARRAY ;
				len = read4BytesLen(datalen,in) ;
				this.objVal = DataUtil.readBytes(in, len) ;
				break ;
			case T_DATE:
				this.type = VAL_TYPE_DATE ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						long ct = DataUtil.readLong(in) ;
						Date tmpdt = new Date(ct) ;
						objVals.add(tmpdt) ;
					}
				}
				else
				{
					long ct = DataUtil.readLong(in) ;
					objVal = new Date(ct) ;
				}
				break ;
			case XmlVal.T_DOUBLE:
				this.type = VAL_TYPE_DOUBLE ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Double dv = DataUtil.readDouble(in) ;
						objVals.add(dv) ;
					}
				}
				else
				{
					objVal = DataUtil.readDouble(in) ;
				}
				break ;
			case XmlVal.T_FLOAT:
				this.type = VAL_TYPE_FLOAT ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Float dv = DataUtil.readFloat(in) ;
						objVals.add(dv) ;
					}
				}
				else
				{
					objVal = DataUtil.readFloat(in) ;
				}
				break ;
			case XmlVal.T_INT64:
				this.type = VAL_TYPE_INT64 ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Long dv = DataUtil.readLong(in) ;
						objVals.add(dv) ;
					}
				}
				else
				{
					this.objVal=DataUtil.readLong(in) ;
				}
				break ;
			case XmlVal.T_INT32:
				this.type = VAL_TYPE_INT32 ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Integer dv = DataUtil.readInt(in) ;
						objVals.add(dv) ;
					}
				}
				else
				{
					this.objVal=DataUtil.readInt(in) ;
				}
				break ;
			case XmlVal.T_INT16:
				this.type = VAL_TYPE_INT16 ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Short dv = DataUtil.readShort(in) ;
						objVals.add(dv) ;
					}
				}
				else
				{
					this.objVal=DataUtil.readShort(in) ;
				}
				break ;
			case XmlVal.T_BYTE:
				this.type = VAL_TYPE_BYTE ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Byte dv = (byte)in.read() ;
						objVals.add(dv) ;
					}
				}
				else
				{
					this.objVal =(byte)in.read();
				}
				break ;
			case XmlVal.T_STR:
				this.type = VAL_TYPE_STR ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						int lstr = read4BytesLen(datalen,in) ;
						byte[] svbs = DataUtil.readBytes(in, lstr) ;
						objVals.add(new String(svbs,"UTF-8")) ;
					}
				}
				else
				{
					int lstr = read4BytesLen(datalen,in) ;
					byte[] svbs = DataUtil.readBytes(in, lstr) ;
					objVal = new String(svbs,"UTF-8") ;
				}
				break ;
			case XmlVal.T_BOOL:
				this.type = VAL_TYPE_BOOL ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						Boolean dv = DataUtil.readBoolean(in);
						objVals.add(dv) ;
					}
				}
				else
				{
					objVal = DataUtil.readBoolean(in);
				}
				break ;
			case XmlVal.T_NULL:
				this.type = VAL_TYPE_NULL ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>(len) ;
					//out.write(DataUtil.intToBytes(s)) ;
				}
				break ;
			case XmlVal.T_BIGDECIMAL:
				this.type = VAL_TYPE_BIGDECIMAL ;
				if(bArray)
				{
					len = read4BytesLen(datalen,in) ;
					this.objVals = new ArrayList<Object>() ;
					for(int k = 0 ; k < len ; k ++)
					{
						int lstr = read4BytesLen(datalen,in) ;
						byte[] svbs = DataUtil.readBytes(in, lstr) ;
						String str = new String(svbs) ;
						objVals.add(new BigDecimal(str)) ;
					}
				}
				else
				{
					int lstr = read4BytesLen(datalen,in) ;
					byte[] svbs = DataUtil.readBytes(in, lstr) ;
					String str = new String(svbs) ;
					objVal = new BigDecimal(str) ;
				}
				break ;
//			case T_CHAR:
//				this.type = VAL_TYPE_CHAR ;
//				if(bArray)
//				{
//					len = read4BytesLen(datalen,in) ;
//					this.objVals = new ArrayList<Object>() ;
//					for(int k = 0 ; k < len ; k ++)
//					{
//						int lstr = read4BytesLen(datalen,in) ;
//						byte[] svbs = DataUtil.readBytes(in, lstr) ;
//						String str = new String(svbs) ;
//						objVals.add(new BigDecimal(str)) ;
//					}
//				}
//				else
//				{
//					int lstr = read4BytesLen(datalen,in) ;
//					byte[] svbs = DataUtil.readBytes(in, lstr) ;
//					String str = new String(svbs) ;
//					objVal = new BigDecimal(str) ;
//				}
//				break ;
//				break;
			default:
				throw new IOException("illegal xml val type="+b) ;
		};
	}

	private static String Obj2XmlEncodedStr(String type, Object obj,
			boolean bxmlencode)
	{
		if (type.equals(XmlVal.VAL_TYPE_STR))
		{
			if (bxmlencode)
				return XmlHelper.xmlEncoding((String) obj);
			else
				return (String) obj;
		}
		else if (type.equals(XmlVal.VAL_TYPE_BYTEARRAY))
		{
			return BinHexTransfer.TransBinToHexStr((byte[]) obj);
		}
		else if (type.equals(XmlVal.VAL_TYPE_BOOL))
		{
			Boolean b = (Boolean) obj;
			if (b.booleanValue())
				return BOOL_STR_TRUE;
			else
				return BOOL_STR_FALSE;
		}
		else if (type.equals(XmlVal.VAL_TYPE_DATE))
		{
//			SimpleDateFormat sdf = new SimpleDateFormat(
//					"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//			return sdf.format((Date) obj);
			return transDateToValStr((Date) obj);
		}
		else if(type.equals(VAL_TYPE_NULL))
		{
			return null ;
		}
		else
		{
			return obj.toString();
		}
	}
	
	public static String transDateToShortShowStr(Date d)
	{
		if(d==null)
			return "" ;
		
		SimpleDateFormat sdf = new SimpleDateFormat(
		"yyyy-MM-dd");
return sdf.format(d);
	}
	
	public static String transDateToDetailShowStr(Date d)
	{
		if(d==null)
			return "" ;
		
		SimpleDateFormat sdf = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
return sdf.format(d);
	}
	
	static SimpleDateFormat XmlDateStr = new SimpleDateFormat(
	"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	private static String toXmlValDateStr(Date d)
	{
		if(d==null)
			return "" ;
		return XmlDateStr.format(d);
	}
	
	
	
	public static String transDateToValStr(Date d)
	{
		return toXmlValDateStr(d);
	}

	private static Object xmlEncodedStr2Obj(String type, String xmlestr,
			boolean bxmldecode)
	{
		try
		{
			if (type.equals(XmlVal.VAL_TYPE_BOOL)||type.equalsIgnoreCase("boolean"))
			{
				return new Boolean(BOOL_STR_TRUE.equals(xmlestr));
			}
			else if (type.equals(XmlVal.VAL_TYPE_STR))
			{
				if (bxmldecode)
					return XmlHelper.xmlDecoding(xmlestr);
				else
					return xmlestr;
			}
			else if (type.equals(XmlVal.VAL_TYPE_BYTE))
			{
				if("".equals(xmlestr))
					return null ;
				return new Byte(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_INT16))
			{
				if("".equals(xmlestr))
					return null ;
				return new Short(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_INT32))
			{
				if("".equals(xmlestr))
					return null ;
				return new Integer(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_INT64))
			{
				if("".equals(xmlestr))
					return null ;
				return new Long(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_FLOAT))
			{
				if("".equals(xmlestr))
					return null ;
				return new Float(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_DOUBLE))
			{
				if("".equals(xmlestr))
					return null ;
				return new Double(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_BIGDECIMAL))
			{
				if("".equals(xmlestr))
					return null ;
				return new BigDecimal(xmlestr);
			}
			else if (type.equals(XmlVal.VAL_TYPE_DATE))
			{
//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//				Date d = sdf.parse(xmlestr);
				
				Calendar cal = Convert.toCalendar(xmlestr) ;
				if(cal==null)
					return null ;
				Date d = cal.getTime();
				return new java.sql.Timestamp(d.getTime());
			}
			else if (type.equals(XmlVal.VAL_TYPE_BYTEARRAY))
			{
				if(xmlestr==null)
					return null ;
				
				if("".equals(xmlestr))
					return new byte[0] ;
				return BinHexTransfer.TransHexStrToBin(xmlestr);
			}
			else if(type.equals(XmlVal.VAL_TYPE_NULL))
			{
				return null ;
			}
			// else if(type.equals(XmlVal.VAL_TYPE_XMLDATA))
			// {
			// return XmlData.parseFromXmlStr(xmlestr);
			// }
			else
			{
				throw new Exception("unknow type=" + type);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	
	 * @param type
	 * @param len
	 * @return
	 */
	public static Object[] createObjArrayByType(String type, int len)
	{
		if (len < 0)
			return null;

		if (type.equals(XmlVal.VAL_TYPE_BOOL))
		{
			return new Boolean[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_STR))
		{
			return new String[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_BYTE))
		{
			return new Byte[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_INT16))
		{
			return new Short[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_INT32))
		{
			return new Integer[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_INT64))
		{
			return new Long[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_FLOAT))
		{
			return new Float[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_DOUBLE))
		{
			return new Double[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_DATE))
		{
			return new java.sql.Timestamp[len];
		}
		else if (type.equals(XmlVal.VAL_TYPE_BYTEARRAY))
		{
			return null;// new byte[len][];
		}
		else if(type.equals(XmlVal.VAL_TYPE_NULL))
		{
			return null ;
		}
		// else if(type.equals(XmlVal.VAL_TYPE_XMLDATA))
		// {
		// return XmlData.parseFromXmlStr(xmlestr);
		// }
		else
		{
			throw new RuntimeException("unknow type=" + type);
		}
	}

	public static String obj2type(Object obj)
	{
		if(obj==null)
			return VAL_TYPE_NULL;
		
		if (obj instanceof Boolean)
		{
			return XmlVal.VAL_TYPE_BOOL;
		}
		else if (obj instanceof String)
		{
			return XmlVal.VAL_TYPE_STR;
		}
		else if (obj instanceof Byte)
		{
			return XmlVal.VAL_TYPE_BYTE;
		}
		else if (obj instanceof Short)
		{
			return XmlVal.VAL_TYPE_INT16;
		}
		else if (obj instanceof Integer)
		{
			return XmlVal.VAL_TYPE_INT32;
		}
		else if (obj instanceof Long)
		{
			return XmlVal.VAL_TYPE_INT64;
		}
		else if (obj instanceof Float)
		{
			return XmlVal.VAL_TYPE_FLOAT;
		}
		else if (obj instanceof Double)
		{
			return XmlVal.VAL_TYPE_DOUBLE;
		}
		else if (obj instanceof Date)
		{
			return XmlVal.VAL_TYPE_DATE;
		}
		else if (obj instanceof byte[])
		{
			return XmlVal.VAL_TYPE_BYTEARRAY;
		}
		else if(obj instanceof BigDecimal)
		{
			return XmlVal.VAL_TYPE_BIGDECIMAL ;
		}
		// else if(obj instanceof XmlData)
		// {
		// return XmlVal.VAL_TYPE_XMLDATA;
		// }
		else
		{
			throw new IllegalArgumentException(
					"cannot process Object with type="
							+ obj.getClass().getName());
		}
	}

	
	public static XmlValType class2VT(Class<?> c)
	{
		return StrType2ValType(class2xmlValType(c)) ;
	}
	
	/**
	 * ����
	 * @param c
	 * @return
	 */
	public static String class2xmlValType(Class<?> c)
	{
		if(c==null)
			return VAL_TYPE_NULL;
		
		if (c == boolean.class)
		{
			return XmlVal.VAL_TYPE_BOOL;
		}
		else if (c == String.class)
		{
			return XmlVal.VAL_TYPE_STR;
		}
		else if (c == byte.class)
		{
			return XmlVal.VAL_TYPE_BYTE;
		}
		else if (c == short.class)
		{
			return XmlVal.VAL_TYPE_INT16;
		}
		else if (c == int.class)
		{
			return XmlVal.VAL_TYPE_INT32;
		}
		else if (c == long.class)
		{
			return XmlVal.VAL_TYPE_INT64;
		}
		else if (c == float.class)
		{
			return XmlVal.VAL_TYPE_FLOAT;
		}
		else if (c == double.class)
		{
			return XmlVal.VAL_TYPE_DOUBLE;
		}
		else if (c == Date.class)
		{
			return XmlVal.VAL_TYPE_DATE;
		}
		else if (c == byte[].class)
		{
			return XmlVal.VAL_TYPE_BYTEARRAY;
		}
		else if(c == BigDecimal.class)
		{
			return XmlVal.VAL_TYPE_BIGDECIMAL;
		}
		// else if(obj instanceof XmlData)
		// {
		// return XmlVal.VAL_TYPE_XMLDATA;
		// }
		else
		{
			return null;
		}
	}
	
	
	//Ϊ���������ı��--��XmlValTypeֵ����
	
	final int T_SCHEMA = 1;

	final int T_BYTEARRAY = 2;

	static final int T_DATE = 3;

	static final int T_DOUBLE = 4;

	static final int T_FLOAT = 5;

	static final int T_INT64 = 6;

	static final int T_INT32 = 7;

	static final int T_INT16 = 8;

	static final int T_BYTE = 9;

	static final int T_STR = 10;

	static final int T_BOOL = 11;
	
	static final int T_NULL = 12;
	
	static final int T_BIGDECIMAL = 13 ;
	
	static final int T_CHAR = 14 ;

	static final int T_PROP = 21;//xmldata �ڲ�������ֵ
	
	static final int T_XMLDATA = 22;
	
	static final int T_XMLDATA_SUB = 23;
	
	static final int T_XMLDATA_SUB_ARRAY = 24;
}

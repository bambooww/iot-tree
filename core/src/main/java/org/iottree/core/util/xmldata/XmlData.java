package org.iottree.core.util.xmldata;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlDataPath.PathItem;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
//import javax.swing.tree.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;

//import org.iottree.workflow.api.cmd.XmlDataSet;
//import org.iottree.workflow.api.util.XmlHelper;

public class XmlData implements IXmlStringable// ,IExpPropProvider
{
	static DocumentBuilderFactory xmlDocBuilderFactory = null;

	static
	{
		xmlDocBuilderFactory = DocumentBuilderFactory.newInstance();
		// xmlDocBuilderFactory.set
		xmlDocBuilderFactory.setNamespaceAware(false);
		xmlDocBuilderFactory.setValidating(false);
	}

	static class XmlDataParam
	{
		boolean bArray = false;

		XmlData xmlData = null;

		List<XmlData> xmlDatas = null;

		public XmlDataParam copyMe()
		{
			XmlDataParam xdp = new XmlDataParam();
			xdp.bArray = bArray;
			if (xmlData != null)
			{
				xdp.xmlData = xmlData.copyMe();
			}
			else if (xmlDatas != null)
			{
				xdp.xmlDatas = new ArrayList<XmlData>();
				for (XmlData tmpxd : xmlDatas)
				{
					xdp.xmlDatas.add(tmpxd.copyMe());
				}
			}

			return xdp;
		}
	}

	// String dataName = null ;

	Hashtable<String, XmlVal> pname2val = new Hashtable<String, XmlVal>();

	Hashtable<String, XmlDataParam> pname2data = new Hashtable<String, XmlDataParam>();

	/**
	 * �����XmlData��������XmlDataWithFile���ڲ�XmlData����˳���ָ��
	 */
	XmlDataWithFile belongToXdFile = null;

	public XmlData()
	{
		// dataName = "" ;
	}

	/**
	 * �����XmlData��������XmlDataWithFile���ڲ�XmlData����˷������
	 */
	public XmlDataWithFile getBelongToXdFile()
	{
		return belongToXdFile;
	}

	public boolean isEmpty()
	{
		if (pname2val.size() > 0)
			return false;

		if (pname2data.size() > 0)
			return false;

		return true;
	}

	// public XmlData(String dn)
	// {
	// if(dn==null)
	// dn = "";
	//		
	// dataName = dn ;
	// }
	//	
	// public String getName()
	// {
	// return dataName ;
	// }

	static char[] NAME_SPLIT = new char[] { '[', ']', '/' };

	public XmlData copyMe()
	{
		XmlData xd = new XmlData();

		for (Map.Entry<String, XmlVal> pv : pname2val.entrySet())
		{
			String n = pv.getKey();
			XmlVal xv = pv.getValue().copyMe();
			xd.pname2val.put(n, xv);
		}

		for (Map.Entry<String, XmlDataParam> pv : pname2data.entrySet())
		{
			String n = pv.getKey();
			XmlDataParam xv = pv.getValue().copyMe();
			xd.pname2data.put(n, xv);
		}

		return xd;
	}

	private void checkParamOrSubName(String n)
	{
		if (n == null || n.equals(""))
			throw new IllegalArgumentException(
					"param name or sub name cannot be null or empty!");

		if (n.indexOf('[') >= 0)
			throw new IllegalArgumentException(
					"param name or sub name cannot has '['");

		if (n.indexOf(']') >= 0)
			throw new IllegalArgumentException(
					"param name or sub name cannot has ']'");

		if (n.indexOf('/') >= 0)
			throw new IllegalArgumentException(
					"param name or sub name cannot has '/'");

		if (n.indexOf(':') >= 0)
			throw new IllegalArgumentException(
					"param name or sub name cannot has ':'");

		if (n.length() > 300)
			throw new IllegalArgumentException(
					"param name or sub name cannot bigger than 300");
	}

	public void setParamValue(String pn, Object v)
	{
		checkParamOrSubName(pn);

		if (v == null)
		{
			this.removeParam(pn);
			return;
		}

		XmlVal xv = XmlVal.createSingleVal(v);

		pname2val.put(pn, xv);
	}

	public void unsetParamValue(String pn)
	{
		pname2val.remove(pn);
	}

	public void setParamValues(String pn, List vs)
	{
		checkParamOrSubName(pn);

		if (vs == null || vs.size() <= 0)
			pname2val.remove(pn);
		else
			pname2val.put(pn, XmlVal.createArrayVal(vs));
	}

	public void setParamValues(String pn, Object[] vs)
	{
		checkParamOrSubName(pn);

		if (vs == null || vs.length == 0)
			pname2val.remove(pn);
		else
			pname2val.put(pn, XmlVal.createArrayVal(vs));
	}

	// public void setParamValue(String pn, XmlVal xv)
	// {
	// checkParamOrSubName(pn);
	//
	// if (xv == null)
	// pname2val.remove(pn);
	// else
	// pname2val.put(pn, xv);
	// }

	public XmlVal getParamXmlVal(String pn)
	{
		return pname2val.get(pn);
	}

	public void setParamXmlVal(String pn, XmlVal xv)
	{
		checkParamOrSubName(pn);

		if (xv == null)
			pname2val.remove(pn);
		else
			pname2val.put(pn, xv);
	}

	// public void setIdValue(IdValue idv)
	// {
	// XmlVal xv = idv.getValue() ;
	// if(xv==null)
	// pname2val.remove(idv.getId());
	// else
	// pname2val.put(idv.getId(),xv);
	// }
	//	
	// public IdValue getIdValue(String id)
	// {
	// XmlVal vi = pname2val.get(id);
	// if(vi==null)
	// return null ;
	//		
	// return new IdValue(id,vi);
	// }

	public Object getParamValue(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		return vi.getObjectVal();
	}

	public String getParamXmlValStr(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		if(vi.isArray())
			return null ;
		return vi.getStrVal();
	}

	public String convertParamValStr(String pn, String defaultval)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return defaultval;

		String s = vi.getStrVal();
		if (s == null)
			return defaultval;

		return s;
	}

	/**
	 * 
	 * 
	 * @param path
	 * @return
	 */
	public String getStrSingleValueByPath(String path)
	{
		Object o = getSingleParamValueByPath(path);
		if (o == null)
			return null;

		return o.toString();
	}

	public XmlVal removeParam(String pn)
	{
		return pname2val.remove(pn);
	}

	public void clearParam()
	{
		pname2val.clear();
	}

	/**
	 * ת��Ϊ�ַ���ֵӳ�䣬����֧�ֺ�XmlElement�������Ƶ����
	 * 
	 * @return
	 */
	public HashMap<String, String> toNameStrValMap()
	{
		HashMap<String, String> rets = new HashMap<String, String>();
		for (Map.Entry<String, XmlVal> n2v : pname2val.entrySet())
		{
			String n = n2v.getKey();
			XmlVal xv = n2v.getValue();
			String v = xv.getStrVal();
			if (v == null)
				continue;

			rets.put(n, xv.getStrVal());
		}
		return rets;
	}
	
	public void fromNameStrValMap(HashMap<String,String> n2v)
	{
		for(Map.Entry<String, String> nv:n2v.entrySet())
		{
			this.setParamValue(nv.getKey(), nv.getValue()) ;
		}
	}

	public Object removeParamValue(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		if (vi.isArray())
			throw new IllegalArgumentException("param with name=" + pn
					+ " is array");
		vi = pname2val.remove(pn);
		return vi.getObjectVal();
	}

	public Object removeParamValues(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		if (!vi.isArray())
			throw new IllegalArgumentException("param with name=" + pn
					+ " is not array");
		vi = pname2val.remove(pn);
		return vi.getObjectVals();
	}

	// public Object get

	public List<Object> getParamValues(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		return vi.getObjectVals();
	}

	public List<String> getParamXmlValStrs(String pn)
	{
		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		return vi.getStrVals();
	}

	public Object getParamValuesIdx(String pn, int idx)
	{
		if (idx < 0)
			return null;

		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		List ll = vi.getObjectVals();
		if (ll == null)
			return null;

		if (idx < ll.size())
			return ll.get(idx);

		return null;
	}

	public String getParamXmlValStrsIdx(String pn, int idx)
	{
		if (idx < 0)
			return null;

		XmlVal vi = pname2val.get(pn);
		if (vi == null)
			return null;

		List<String> ll = vi.getStrVals();
		if (ll == null)
			return null;

		if (idx < ll.size())
			return ll.get(idx);

		return null;
	}

	public String[] getParamNames()
	{
		String[] rets = new String[pname2val.size()];
		pname2val.keySet().toArray(rets);
		return rets;
	}
	
	public Set<String> getParamNameSet()
	{
		return pname2val.keySet();
	}

	public boolean containsParamName(String n)
	{
		return pname2val.containsKey(n);
	}

	public String containsParamNameIgnoreCase(String n)
	{
		for (String tmpn : pname2val.keySet())
		{
			if (tmpn.equalsIgnoreCase(n))
				return tmpn;
		}
		return null;
	}

	public int getParamNum()
	{
		return pname2val.size();
	}

	/**
	 * ���ݳ�Ա�����ж��Ƿ���ڸó�Ա������
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasParam(String name)
	{
		return pname2val.containsKey(name);
	}

	public short getParamValueInt16(String pn, short default_val)
	{
		Object o = getParamValue(pn);
		if (o == null)
			return default_val;

		return (Short) o;
	}

	public int getParamValueInt32(String pn, int default_val)
	{
		Object o = getParamValue(pn);
		if (o == null)
			return default_val;

		return (Integer) o;
	}

	public long getParamValueInt64(String pn, long default_val)
	{
		Object o = getParamValue(pn);
		if (o == null)
			return default_val;

		return (Long) o;
	}

	public byte[] getParamValueBytes(String pn)
	{
		return (byte[]) getParamValue(pn);
	}

	public BigDecimal getParamValueBigDecimal(String pn, BigDecimal def_val)
	{
		Object o = getParamValue(pn);
		if (o == null)
			return def_val;

		return (BigDecimal) o;
	}

	public Short[] getParamValuesInt16(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Short[] rets = new Short[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public Integer[] getParamValuesInt32(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Integer[] rets = new Integer[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public Long[] getParamValuesInt64(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Long[] rets = new Long[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public Boolean getParamValueBool(String pn, boolean default_val)
	{
		Object o = getParamValue(pn);
		if (o == null)
			return default_val;

		return (Boolean) o;
	}

	public Boolean[] getParamValuesBool(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Boolean[] rets = new Boolean[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public String getParamValueStr(String pn)
	{
		return (String) getParamValue(pn);
	}

	public String getParamValueStr(String pn, String defaultval)
	{
		String tmps = (String) getParamValue(pn);
		if (tmps == null)
			return defaultval;

		return tmps;
	}

	/**
	 * ǿ�ƻ��ֵ����ת���ַ�����ʽ���
	 * @param pn
	 * @return
	 */
	public String getParamValueTransStr(String pn)
	{
		Object o = getParamValue(pn);
		if(o==null)
			return null;
		return o.toString() ;
	}
	// public boolean getParamValueBool(String pn,boolean defaultv)
	// {
	// Object obj = getParamValue(pn);
	// if (obj == null)
	// return defaultv;
	//
	// return (Boolean) obj;
	// }

	public Date getParamValueDate(String pn, Date defaultv)
	{
		Object obj = getParamValue(pn);
		if (obj == null)
			return defaultv;

		return (Date) obj;
	}

	public String[] getParamValuesStr(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		String[] rets = new String[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public float getParamValueFloat(String pn, float defaultv)
	{
		Object obj = getParamValue(pn);
		if (obj == null)
			return defaultv;

		return (Float) obj;
	}

	public Float[] getParamValuesFloat(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Float[] rets = new Float[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public double getParamValueDouble(String pn, double defaultv)
	{
		Object obj = getParamValue(pn);
		if (obj == null)
			return defaultv;

		return (Double) obj;
	}

	public Double[] getParamValuesDouble(String pn)
	{
		List<Object> ll = getParamValues(pn);
		if (ll == null)
			return null;

		Double[] rets = new Double[ll.size()];
		ll.toArray(rets);
		return rets;
	}

	public String[] getSubDataNames()
	{
		String[] rets = new String[pname2data.size()];
		pname2data.keySet().toArray(rets);
		return rets;
	}

	public ArrayList<String> getSubDataSingleNames()
	{
		ArrayList<String> rets = new ArrayList<String>();
		for (Map.Entry<String, XmlDataParam> pds : pname2data.entrySet())
		{
			if (!pds.getValue().bArray)
				rets.add(pds.getKey());
		}

		return rets;
	}

	public ArrayList<String> getSubDataArrayNames()
	{
		ArrayList<String> rets = new ArrayList<String>();
		for (Map.Entry<String, XmlDataParam> pds : pname2data.entrySet())
		{
			if (pds.getValue().bArray)
				rets.add(pds.getKey());
		}

		return rets;
	}

	public void setSubDataSingle(String n, XmlData xd)
	{
		checkParamOrSubName(n);

		XmlDataParam xdp = new XmlDataParam();
		xdp.bArray = false;
		xdp.xmlData = xd;
		pname2data.put(n, xdp);
	}

	public void setSubDataSingleByPath(XmlDataPath xdp, XmlData xd)
	{
		if (!xdp.isStruct())
			throw new IllegalArgumentException(
					"xml data path is not for struct!");

		if (xdp.isValueArray())
			throw new IllegalArgumentException("xml data path is array!");

		PathItem[] pis = xdp.getPath();
		if (pis == null || pis.length <= 0)
			throw new IllegalArgumentException("xml data path is no items!");

		XmlData tmpxd = this;
		for (int i = 0; i < pis.length - 1; i++)
		{
			tmpxd = tmpxd.getOrCreateSubDataSingle(pis[i].getPathItemName());
		}

		tmpxd.setSubDataSingle(pis[pis.length - 1].getPathItemName(), xd);
	}

	public void setSubDataArrayByPath(XmlDataPath xdp, List<XmlData> xds)
	{
		if (!xdp.isStruct())
			throw new IllegalArgumentException(
					"xml data path is not for struct!");

		if (!xdp.isValueArray())
			throw new IllegalArgumentException("xml data path is not array!");

		PathItem[] pis = xdp.getPath();
		if (pis == null || pis.length <= 0)
			throw new IllegalArgumentException("xml data path is no items!");

		XmlData tmpxd = this;
		for (int i = 0; i < pis.length - 1; i++)
		{
			tmpxd = tmpxd.getOrCreateSubDataSingle(pis[i].getPathItemName());
		}

		List<XmlData> aas = tmpxd.getOrCreateSubDataArray(pis[pis.length - 1]
				.getPathItemName());
		aas.addAll(xds);
	}

	public void setSubDataArray(String subname, List<XmlData> xds)
	{
		if (xds == null)
		{
			this.removeSubData(subname);
			return;
		}

		List<XmlData> aas = this.getOrCreateSubDataArray(subname);
		aas.clear();
		for (XmlData xd : xds)
			aas.add(xd);
	}

	public XmlData getSubDataSingle(String n)
	{
		XmlDataParam xdp = pname2data.get(n);
		if (xdp != null)
			return xdp.xmlData;

		return null;
	}

	public List<XmlData> getSubDataArray(String n)
	{
		XmlDataParam xdp = pname2data.get(n);
		if (xdp != null)
			return xdp.xmlDatas;

		return null;
	}

	// public XmlData getSubDataInArray(String n,int idx)
	// {
	// if(idx<0)
	// return null ;
	//		
	// XmlDataParam xdp = pname2data.get(n);
	// if (xdp == null)
	// return null;
	//
	// List<XmlData> xds = xdp.xmlDatas;
	// if(xds==null||xds.size()<=0||xds.size()<=idx)
	// return null ;
	//		
	// return xds.get(idx);
	// }

	/**
	 * ����·����Ϣ���
	 */
	public List<XmlData> getSubDataArrayByPath(String[] ps)
	{
		XmlData xd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			XmlDataPath.PathItem pi = new XmlDataPath.PathItem(ps[i]);
			if (pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path is not specify array sub XmlData pos!");

				xd = xd.getSubDataArrayIdx(pi.getPathItemName(), pi
						.getArrayIdx());
			}
			else
			{
				xd = xd.getSubDataSingle(pi.getPathItemName());
			}

			if (xd == null)
				return null;
		}

		return xd.getSubDataArray(ps[ps.length - 1]);
	}

	public List<XmlData> getSubDataArrayByPath(XmlDataPath xdp)
	{
		if (!xdp.isValueArray())
			throw new IllegalArgumentException("path=" + xdp
					+ " is not point to array!");
		XmlData xd = this;
		PathItem[] ps = xdp.getPath();
		for (int i = 0; i < ps.length - 1; i++)
		{
			XmlDataPath.PathItem pi = ps[i];
			if (pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path is not specify array sub XmlData pos!");

				xd = xd.getSubDataArrayIdx(pi.getPathItemName(), pi
						.getArrayIdx());
			}
			else
			{
				xd = xd.getSubDataSingle(pi.getPathItemName());
			}

			if (xd == null)
				return null;
		}

		return xd.getSubDataArray(ps[ps.length - 1].getPathItemName());
	}

	public XmlData getSubDataArrayIdx(String n, int idx)
	{
		if (idx < 0)
			return null;

		XmlDataParam xdp = pname2data.get(n);
		if (xdp != null && idx < xdp.xmlDatas.size())
			return xdp.xmlDatas.get(idx);

		return null;
	}

	// public void setSubDataArray(String n,XmlData xd)
	// {
	// XmlDataParam xdp = new XmlDataParam();
	// xdp.bArray = false;
	// xdp.xmlData = xd ;
	// pname2data.put(n,xdp);
	// }

	public XmlData getOrCreateSubDataSingle(String n)
	{
		checkParamOrSubName(n);

		XmlDataParam xdp = pname2data.get(n);
		if (xdp != null)
			return xdp.xmlData;

		xdp = new XmlDataParam();
		xdp.xmlData = new XmlData();
		xdp.bArray = false;
		pname2data.put(n, xdp);
		return xdp.xmlData;
	}

	public List<XmlData> getOrCreateSubDataArray(String n)
	{
		checkParamOrSubName(n);
		// if(n==null)
		// n = "";

		XmlDataParam xdp = pname2data.get(n);
		if (xdp != null)
			return xdp.xmlDatas;

		xdp = new XmlDataParam();
		xdp.xmlDatas = new ArrayList<XmlData>();
		xdp.bArray = true;
		pname2data.put(n, xdp);
		return xdp.xmlDatas;
	}

	public boolean removeSubData(String n)
	{
		return pname2data.remove(n) != null;
	}

	/**
	 * ɾ�����е��ӽṹ
	 * 
	 */
	public void clearSubXmlData()
	{
		pname2data.clear();
	}

	/**
	 * ɾ�����е��Ӳ������ӽṹ
	 */
	public void clearParamsAndSubXmlData()
	{
		pname2val.clear();
		pname2data.clear();
	}

	// public void clearSubData
	/**
	 * 
	 * @param path
	 * @return ·����ָ��ľ���ֵ
	 */
	public Object getParamValueByPath(String path)
	{
		XmlDataPath xdp = new XmlDataPath(path);
		if (xdp.isStruct())
			throw new IllegalArgumentException(
					"invalid path ,it is a struct path");

		PathItem[] ps = xdp.getPath();
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			if (ps[i].isArray())
			{
				curxd = curxd.getSubDataArrayIdx(ps[i].getPathItemName(), ps[i]
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(ps[i].getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		PathItem p = ps[ps.length - 1];
		if (p.isArray())
		{
			return curxd
					.getParamValuesIdx(p.getPathItemName(), p.getArrayIdx());
		}
		else
		{
			return curxd.getParamValue(p.getPathItemName());
		}
	}

	public Object getSingleParamValueByPath(String p)
	{
		XmlDataPath xdp = new XmlDataPath(p);
		if (xdp.isStruct())
			throw new IllegalArgumentException("path=" + p + " is struct!");
		if (xdp.isValueArray())
			throw new IllegalArgumentException("path=" + p
					+ " is array value path!");

		return getParamValueByPath(xdp);
	}

	public Object getParamValueByPath(XmlDataPath xdp)
	{
		if (xdp == null)
			return null;

		if (xdp.isValueArray())
			throw new IllegalArgumentException("path is not for single !");

		PathItem[] ps = xdp.getPath();
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			PathItem p = ps[i];

			if (p.isArray())
			{
				curxd = curxd.getSubDataArrayIdx(p.getPathItemName(), p
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(p.getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		PathItem p = ps[ps.length - 1];
		if (p.isArray())
		{
			return curxd
					.getParamValuesIdx(p.getPathItemName(), p.getArrayIdx());
		}
		else
		{
			return curxd.getParamValue(p.getPathItemName());
		}
	}

	/**
	 * ����·����õ�ֵ��XmlVal�ڲ����ַ�����ʾ��ֵ
	 * 
	 * @param xdp
	 * @return
	 */
	public String getParamXmlValStrByPath(XmlDataPath xdp)
	{
		if (xdp == null)
			return null;

		if (xdp.isValueArray())
			throw new IllegalArgumentException("path is not for single !");

		PathItem[] ps = xdp.getPath();
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			PathItem p = ps[i];

			if (p.isArray())
			{
				curxd = curxd.getSubDataArrayIdx(p.getPathItemName(), p
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(p.getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		PathItem p = ps[ps.length - 1];
		if (p.isArray())
		{
			return curxd.getParamXmlValStrsIdx(p.getPathItemName(), p
					.getArrayIdx());
		}
		else
		{
			return curxd.getParamXmlValStr(p.getPathItemName());
		}
	}

	public List<Object> getParamValuesByPath(XmlDataPath xdp)
	{
		if (xdp == null)
			return null;

		if (!xdp.isValueArray())
			throw new IllegalArgumentException("path is not for array !");

		PathItem[] ps = xdp.getPath();
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			PathItem p = ps[i];

			if (p.isArray())
			{
				curxd = curxd.getSubDataArrayIdx(p.getPathItemName(), p
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(p.getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		PathItem p = ps[ps.length - 1];
		return curxd.getParamValues(p.getPathItemName());
	}

	public XmlVal getParamXmlValByPath(XmlDataPath xdp)
	{
		if (xdp == null)
			return null;

		if (xdp.isStruct())
			throw new IllegalArgumentException("invalid path,it is struct!");

		PathItem[] ps = xdp.getPath();
		if (ps == null || ps.length == 0)
			return null;

		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			PathItem p = ps[i];

			if (p.isArray())
			{
				curxd = curxd.getSubDataArrayIdx(p.getPathItemName(), p
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(p.getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		PathItem p = ps[ps.length - 1];
		return curxd.getParamXmlVal(p.getPathItemName());
	}

	public void setParamXmlValStrByPath(XmlDataPath xdp, String xmlval_str)
	{
		String xvt = xdp.getXmlValType();
		if (xvt == null)
			throw new IllegalArgumentException(
					"XmlDataPath must has xml val type info:");
		if (xdp.isValueArray())
		{
			StringTokenizer st = new StringTokenizer(xmlval_str, "|");
			ArrayList<String> vss = new ArrayList<String>();
			while (st.hasMoreTokens())
			{
				String tmps = st.nextToken().trim();
				if (tmps.equals(""))
					continue;
				vss.add(tmps);
			}
			XmlVal xv = new XmlVal(xvt, vss);
			setParamXmlValByPath(xdp, xv);
		}
		else
		{
			// if("".xvt)
			XmlVal xv = new XmlVal(xvt, xmlval_str);
			XmlVal.XmlValType xvtt = xv.getXmlValType();
			if (xvtt != XmlVal.XmlValType.vt_string)
			{
				if (xmlval_str == null || xmlval_str.equals(""))
					return;
			}
			setParamXmlValByPath(xdp, xv);
		}
	}

	public void setParamXmlValByPath(XmlDataPath xdp, XmlVal xv)
	{
		if (xdp == null)
			throw new IllegalArgumentException("path cannot be null!");

		if (xdp.isStruct())
			throw new IllegalArgumentException("invalid path,it is struct!");

		PathItem[] ps = xdp.getPath();
		if (ps == null || ps.length == 0)
			throw new IllegalArgumentException("path,path item is null!");

		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			PathItem p = ps[i];

			if (p.isArray())
			{
				throw new IllegalArgumentException(
						"middle path item cannot be array!");
				// curxd =
				// curxd.getSubDataArrayIdx(p.getPathItemName(),p.getArrayIdx());
			}
			else
			{
				curxd = curxd.getOrCreateSubDataSingle(p.getPathItemName());
			}
		}

		PathItem p = ps[ps.length - 1];
		curxd.setParamXmlVal(p.getPathItemName(), xv);
	}

	public Object getSingleParamValueByPath(String[] ps)
	{
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			String p = ps[i];

			curxd = curxd.getSubDataSingle(p);

			if (curxd == null)
				return null;
		}

		String p = ps[ps.length - 1];
		return curxd.getParamValue(p);
	}

	public void setSingleParamValueByPath(String path, Object v)
	{
		XmlDataPath xdp = new XmlDataPath(path);
		if (xdp.isStruct())
			throw new IllegalArgumentException(
					"invalid path ,it is a struct path");

		PathItem[] ps = xdp.getPath();
		setSingleParamValueByPath(ps, v);
	}

	public void setSingleParamValueByPath(PathItem[] path, Object v)
	{
		if (path == null || path.length <= 0)
			return;

		for (PathItem pi : path)
		{
			if (pi.isArray())
				throw new IllegalArgumentException(
						"invalid path,because it has array info!");
		}

		XmlData curxd = this;
		for (int i = 0; i < path.length - 1; i++)
		{
			PathItem pi = path[i];
			curxd = this.getOrCreateSubDataSingle(pi.getPathItemName());
		}

		curxd.setParamValue(path[path.length - 1].getPathItemName(), v);
	}

	public void setSingleParamValueByPath(String[] path, Object v)
	{
		String[] ps = path;
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			String p = ps[i];
			curxd = this.getOrCreateSubDataSingle(p);
		}

		curxd.setParamValue(ps[ps.length - 1], v);
	}

	public Object removeSingleParamValueByPath(String path)
	{
		XmlDataPath xdp = new XmlDataPath(path);
		if (xdp.isStruct())
			throw new IllegalArgumentException(
					"invalid path ,it is a struct path");

		PathItem[] ps = xdp.getPath();
		return removeSingleParamValueByPath(ps);
	}

	public Object removeSingleParamValueByPath(PathItem[] ps)
	{
		if (ps == null || ps.length <= 0)
			return null;

		for (PathItem pi : ps)
		{
			if (pi.isArray())
				throw new IllegalArgumentException(
						"invalid path,because it has array info!");
		}

		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			curxd = getSubDataSingle(ps[i].getPathItemName());
			if (curxd == null)
				return null;
		}

		return curxd.removeParamValue(ps[ps.length - 1].getPathItemName());
	}

	public Object removeSingleParamValueByPath(String[] path)
	{
		String[] ps = path;
		XmlData curxd = this;
		for (int i = 0; i < ps.length - 1; i++)
		{
			String p = ps[i];
			curxd = getSubDataSingle(p);
			if (curxd == null)
				return null;
		}

		return curxd.removeParamValue(ps[ps.length - 1]);
	}

	/**
	 * 
	 * @param path
	 * @return ·����ָ��ľ����ӽṹ
	 */
	public XmlData getSubDataByPath(String path)
	{
		XmlDataPath xdp = new XmlDataPath(path);
		if (!xdp.isStruct())
			throw new IllegalArgumentException(
					"invalid path ,it is not a struct path");

		return getSubDataByPath(xdp);
	}

	public XmlData getSubDataByPath(XmlDataPath xdp)
	{
		if (xdp.isValueArray())
			throw new IllegalArgumentException("path=" + xdp.toString()
					+ " is point to array!");

		PathItem[] ps = xdp.getPath();
		XmlData curxd = this;
		for (int i = 0; i < ps.length; i++)
		{
			if (ps[i].isArray())
			{
				curxd = curxd.getSubDataArrayIdx(ps[i].getPathItemName(), ps[i]
						.getArrayIdx());
			}
			else
			{
				curxd = curxd.getSubDataSingle(ps[i].getPathItemName());
			}

			if (curxd == null)
				return null;
		}

		return curxd;
	}

	// /////////////////////////////////
	/**
	 * ������XmlData������,�ϲ����Լ��Ķ�����. ����,Ҫ�󱻺ϲ���XmlData�е����ݽṹ���ͱ����ݽṹ��ͻ
	 * 
	 * �÷���һ��������ͬXmlDataStruct�ṹ��,��һ���ֲ����ݵ�����޸�,���뵽������
	 */
	public void combineAppend(XmlData otherxd)
	{
		for (Map.Entry<String, XmlVal> n2xv : otherxd.pname2val.entrySet())
		{
			String pn = n2xv.getKey();
			XmlVal pv = n2xv.getValue();
			pname2val.put(pn, pv.copyMe());
		}

		for (Map.Entry<String, XmlDataParam> n2xdp : otherxd.pname2data
				.entrySet())
		{
			String pn = n2xdp.getKey();
			XmlDataParam pv = n2xdp.getValue();
			pname2data.put(pn, pv.copyMe());
		}
	}

	// /////////////////////////////////

	public Properties toProp()
	{
		return toProp("") ;
	}
	
	public Properties toProp(String name_prefix)
	{
		Properties p = new Properties();
		toProp(p, ROOT_PATH, this ,name_prefix);
		return p;
	}

	private static void toProp(Properties p, String parentp, XmlData curxd,String name_prefix)
	{
		for (String pn : curxd.getParamNames())
		{
			XmlVal xv = curxd.pname2val.get(pn);
			if (xv.bArray)
			{
				List<String> ss = xv.getStrVals();
				if (ss == null)
					continue;

				int c = ss.size();
				for (int i = 0; i < c; i++)
				{
					String k = getArrayParamPath(parentp, pn, i);
					p.setProperty(name_prefix+k + ":" + xv.type, ss.get(i));
				}
			}
			else
			{
				String s = xv.getStrVal();
				if (s == null)
					continue;
				String k = getSingleParamPath(parentp, pn);
				p.setProperty(name_prefix + k + ":" + xv.type, s);
			}
		}

		for (String subn : curxd.getSubDataNames())
		{
			XmlDataParam xdp = curxd.pname2data.get(subn);
			if (xdp.bArray)
			{
				List<XmlData> subxds = xdp.xmlDatas;
				if (subxds == null)
					continue;

				int c = subxds.size();
				for (int i = 0; i < c; i++)
				{
					String subp = getArraySubDataPath(parentp, subn, i);
					toProp(p, subp, subxds.get(i),name_prefix);
				}
			}
			else
			{
				XmlData subxd = xdp.xmlData;
				if (subxd == null)
					continue;

				String subp = getSingleSubDataPath(parentp, subn);
				toProp(p, subp, subxd,name_prefix);
			}
		}
	}

	public void fromProp(Properties p)
	{
		fromProp(this, p);
	}
	
	public void fromProp(HashMap<String,String> p, String name_prefix)
	{
		Properties pp = new Properties() ;
		for(Map.Entry<String, String> n2v:p.entrySet())
		{
			pp.setProperty(n2v.getKey(), n2v.getValue()) ;
		}
		fromProp(pp, name_prefix);
	}

	public void fromProp(Properties p, String name_prefix)
	{
		if (name_prefix == null || name_prefix.equals(""))
		{
			fromProp(this, p);
		}
		else
		{
			Properties tmpnvc = new Properties();
			int sl = name_prefix.length();
			for (Enumeration en = p.propertyNames(); en.hasMoreElements();)
			{
				String n = (String) en.nextElement();
				if (n.startsWith(name_prefix))
					tmpnvc.setProperty(n.substring(sl), p.getProperty(n));
			}

			fromProp(this, tmpnvc);
		}
	}

	private static void fromProp(XmlData curxd, Properties curp)
	{
		Hashtable<String, Properties> n2p = extractToSub(curp);
		Properties memberps = n2p.remove("");

		if (memberps != null)
		{
			ArrayList<String> singlepks = new ArrayList<String>();
			Hashtable<String, Integer> arraypks = new Hashtable<String, Integer>();
			for (Enumeration en = memberps.propertyNames(); en
					.hasMoreElements();)
			{
				String n = (String) en.nextElement();
				int tp = n.indexOf(':');
				// param name
				int ap = n.indexOf('[');
				if (ap > 0)
				{// array
					int ep = n.indexOf(']', ap);
					String tmpn = n.substring(0, ap) + n.substring(tp);
					int newm = Integer.parseInt(n.substring(ap + 1, ep)) + 1;
					Integer ov = arraypks.get(tmpn);
					int oldm = 0;
					if (ov == null)
						oldm = 0;
					else
						oldm = ov;

					if (newm > oldm)
						arraypks.put(tmpn, newm);
				}
				else
				{
					singlepks.add(n);
				}
			}

			for (String spk : singlepks)
			{
				int tp = spk.indexOf(':');
				String pn = spk.substring(0, tp);
				String type = spk.substring(tp + 1);
				String strv = memberps.getProperty(spk);
				if (strv == null)
					continue;

				XmlVal xv = new XmlVal(type, strv);
				curxd.pname2val.put(pn, xv);
			}
			for (Map.Entry<String, Integer> ent : arraypks.entrySet())
			{
				String spk = ent.getKey();
				int tp = spk.indexOf(':');
				String pn = spk.substring(0, tp);
				String type = spk.substring(tp + 1);

				int num = ent.getValue();
				ArrayList<String> strvals = new ArrayList<String>();
				for (int i = 0; i < num; i++)
				{
					String tmpk = pn + "[" + i + "]:" + type;
					String tmpss = memberps.getProperty(tmpk);
					if (tmpss == null)
						continue;
					strvals.add(tmpss);
				}

				XmlVal xv = new XmlVal(type, strvals);
				curxd.pname2val.put(pn, xv);
			}
		}

		Hashtable<String, Integer> arraysubks = new Hashtable<String, Integer>();
		for (String n : n2p.keySet())
		{
			int ap = n.indexOf('[');
			if (ap < 0)
			{// single
				Properties tmpp = n2p.get(n);
				if (tmpp == null)
					continue;
				XmlData tmpxd = new XmlData();
				fromProp(tmpxd, tmpp);
				curxd.setSubDataSingle(n, tmpxd);
			}
			else
			{// array
				int ep = n.indexOf(']', ap);
				String tmpn = n.substring(0, ap);
				int newm = Integer.parseInt(n.substring(ap + 1, ep)) + 1;
				Integer ov = arraysubks.get(tmpn);
				int oldm = 0;
				if (ov == null)
					oldm = 0;
				else
					oldm = ov;

				if (newm > oldm)
					arraysubks.put(tmpn, newm);
			}
		}

		for (Map.Entry<String, Integer> ent : arraysubks.entrySet())
		{
			String n = ent.getKey();
			int m = ent.getValue();
			List<XmlData> tmpxds = curxd.getOrCreateSubDataArray(n);
			for (int i = 0; i < m; i++)
			{
				String tmpk = n + "[" + i + "]";
				Properties tmppp = n2p.get(tmpk);
				if (tmppp == null)
					continue;

				XmlData tmpxd = new XmlData();
				fromProp(tmpxd, tmppp);
				tmpxds.add(tmpxd);
			}
		}
	}

	private static Hashtable<String, Properties> extractToSub(Properties p)
	{
		Hashtable<String, Properties> ret = new Hashtable<String, Properties>();
		for (Enumeration en = p.propertyNames(); en.hasMoreElements();)
		{
			String k = (String) en.nextElement();
			int i = k.indexOf('/', 1);
			if (i < 0)
			{// param
				String n = k.substring(1);
				Properties tmpp = ret.get("");
				if (tmpp == null)
				{
					tmpp = new Properties();
					ret.put("", tmpp);
				}

				tmpp.setProperty(n, p.getProperty(k));
			}
			else
			{// sub data
				String n = k.substring(1, i);
				String subp = k.substring(i);
				Properties tmpp = ret.get(n);
				if (tmpp == null)
				{
					tmpp = new Properties();
					ret.put(n, tmpp);
				}

				tmpp.setProperty(subp, p.getProperty(k));
			}
		}

		return ret;
	}

	public static final String ROOT_PATH = "/";

	public static String getSingleParamPath(String parentpath, String membername)
	{
		if (parentpath == null || parentpath.equals(""))
			throw new IllegalArgumentException(
					"parent path cannot be null,at least be /");

		if (!parentpath.endsWith("/"))
			throw new IllegalArgumentException("parent path must be end with /");

		return parentpath + membername;
	}

	public static String getArrayParamPath(String parentpath,
			String membername, int pidx)
	{
		if (parentpath == null || parentpath.equals(""))
			throw new IllegalArgumentException(
					"parent path cannot be null,at least be /");

		if (!parentpath.endsWith("/"))
			throw new IllegalArgumentException("parent path must be end with /");

		return parentpath + membername + "[" + pidx + "]";
	}

	public static String getSingleSubDataPath(String parentpath, String subname)
	{
		if (parentpath == null || parentpath.equals(""))
			throw new IllegalArgumentException(
					"parent path cannot be null,at least be /");

		if (!parentpath.endsWith("/"))
			throw new IllegalArgumentException("parent path must be end with /");

		return parentpath + subname + "/";
	}

	public static String getArraySubDataPath(String parentpath, String subname,
			int subidx)
	{
		if (parentpath == null || parentpath.equals(""))
			throw new IllegalArgumentException(
					"parent path cannot be null,at least be /");

		if (!parentpath.endsWith("/"))
			throw new IllegalArgumentException("parent path must be end with /");

		return parentpath + subname + "[" + subidx + "]/";
	}

	static String[] TAGS_PARAM = new String[] { "p", "param" };

	static String[] TAGS_DATA = new String[] { "d", "data" };

	static String[] TAGS_DATA_PARAM = new String[] { "dp", "data_param" };

	static String[] TAGS_VAL = new String[] { "v", "val" };

	public String toXmlString()
	{
		StringBuilder sb = new StringBuilder();
		toXmlString("", sb);
		return sb.toString();

		// try
		// {
		// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setNamespaceAware(true);
		// DocumentBuilder db = dbf.newDocumentBuilder();
		// Document doc = db.newDocument();
		//
		// Element ele = toXmlElement(doc);
		// return XmlHelper.elementToString(ele);
		// }
		// catch (Exception e)
		// {
		// throw new RuntimeException(e.getMessage());
		// }
	}

	public byte[] toBytesWithUTF8()
	{
		try
		{
			return toXmlStringCompact().getBytes("UTF-8");
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	void toXmlString(String tab, StringBuilder sb)
	{
		sb.append(tab).append("<d>");

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			XmlVal xvi = i.getValue();
			if (xvi.isArray())
			{
				sb.append("\r\n\t").append(tab).append("<p").append(" t=\"")
						.append(xvi.getValType()).append("\"").append(" n=\"")
						.append(XmlHelper.xmlEncoding(i.getKey())).append("\"")
						.append(" b_a=\"true\">");

				List<String> strvs = xvi.getStrVals();
				if (strvs != null)
				{
					for (String sv : strvs)
					{
						sb.append("\r\n\t\t").append(tab).append("<v>").append(
								XmlHelper.xmlEncoding(sv)).append("</v>");
					}
				}
				sb.append("\r\n\t").append(tab).append("</p>");
			}
			else
			{
				sb.append("\r\n\t").append(tab).append("<p").append(" t=\"")
						.append(xvi.getValType()).append("\"").append(" n=\"")
						.append(XmlHelper.xmlEncoding(i.getKey())).append("\"");

				String strv = xvi.getStrVal();
				if (strv != null)
					sb.append(" v=\"").append(
							XmlHelper.xmlEncoding(xvi.getStrVal()))
							.append("\"");

				sb.append("/>");
			}
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			XmlDataParam xdp = i.getValue();
			sb.append("\r\n\t").append(tab).append("<dp").append(" n=\"")
					.append(XmlHelper.xmlEncoding(i.getKey())).append("\"");
			if (xdp.bArray)
				sb.append(" b_a=\"true\"");
			sb.append(">");

			if (xdp.bArray)
			{
				for (XmlData tmpxd : xdp.xmlDatas)
				{
					sb.append("\r\n");
					tmpxd.toXmlString(tab + "\t", sb);
				}
			}
			else
			{
				sb.append("\r\n");
				xdp.xmlData.toXmlString(tab + "\t", sb);
			}

			sb.append("\r\n\t").append(tab).append("</dp>");
		}
		sb.append("\r\n").append(tab).append("</d>");
	}

	public String toXmlStringCompact()
	{
		StringBuilder sb = new StringBuilder();
		toXmlStringCompact(sb);
		return sb.toString();
	}

	private void toXmlStringCompact(StringBuilder sb)
	{
		sb.append("<d>");

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			XmlVal xvi = i.getValue();
			if (xvi.isArray())
			{
				sb.append("<p").append(" t=\"").append(xvi.getValType())
						.append("\"").append(" n=\"").append(
								XmlHelper.xmlEncoding(i.getKey())).append("\"")
						.append(" b_a=\"true\">");

				List<String> strvs = xvi.getStrVals();
				if (strvs != null)
				{
					for (String sv : strvs)
					{
						sb.append("<v>").append(XmlHelper.xmlEncoding(sv))
								.append("</v>");
					}
				}
				sb.append("</p>");
			}
			else
			{
				sb.append("<p").append(" t=\"").append(xvi.getValType())
						.append("\"").append(" n=\"").append(
								XmlHelper.xmlEncoding(i.getKey())).append("\"");

				String strv = xvi.getStrVal();
				if (strv != null)
					sb.append(" v=\"").append(
							XmlHelper.xmlEncoding(xvi.getStrVal()))
							.append("\"");

				sb.append("/>");
			}
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			XmlDataParam xdp = i.getValue();
			sb.append("<dp").append(" n=\"").append(
					XmlHelper.xmlEncoding(i.getKey())).append("\"");
			if (xdp.bArray)
				sb.append(" b_a=\"true\"");
			sb.append(">");

			if (xdp.bArray)
			{
				for (XmlData tmpxd : xdp.xmlDatas)
				{
					tmpxd.toXmlStringCompact(sb);
				}
			}
			else
			{
				sb.append("\r\n");
				xdp.xmlData.toXmlStringCompact(sb);
			}

			sb.append("</dp>");
		}
		sb.append("</d>");
	}

	public void writeOutCompact(OutputStream os, String encod)
			throws IOException
	{
		if (Convert.isNullOrEmpty(encod))
			encod = "utf-8";

		OutputStreamWriter osw = new OutputStreamWriter(os, encod);
		writeOutCompact(osw);
		osw.flush();
	}

	private void writeOutCompact(Writer w) throws IOException
	{
		w.write("<d>");

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			XmlVal xvi = i.getValue();
			if (xvi.isArray())
			{
				w.write("<p t=\"");
				w.write(xvi.getValType());
				w.write("\" n=\"");
				w.write(XmlHelper.xmlEncoding(i.getKey()));
				w.write("\" b_a=\"true\">");

				List<String> strvs = xvi.getStrVals();
				if (strvs != null)
				{
					for (String sv : strvs)
					{
						w.write("<v>");
						w.write(XmlHelper.xmlEncoding(sv));
						w.write("</v>");
					}
				}
				w.write("</p>");
			}
			else
			{
				w.write("<p t=\"");
				w.write(xvi.getValType());
				w.write("\" n=\"");
				w.write(XmlHelper.xmlEncoding(i.getKey()));
				w.write("\"");

				String strv = xvi.getStrVal();
				if (strv != null)
				{
					w.write(" v=\"");
					w.write(XmlHelper.xmlEncoding(xvi.getStrVal()));
					w.write("\"");
				}

				w.write("/>");
			}
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			XmlDataParam xdp = i.getValue();
			w.write("<dp n=\"");
			w.write(XmlHelper.xmlEncoding(i.getKey()));
			w.write("\"");
			if (xdp.bArray)
				w.write(" b_a=\"true\"");
			w.write(">");

			if (xdp.bArray)
			{
				for (XmlData tmpxd : xdp.xmlDatas)
				{
					tmpxd.writeOutCompact(w);
				}
			}
			else
			{
				w.write("\r\n");
				xdp.xmlData.writeOutCompact(w);
			}

			w.write("</dp>");
		}
		w.write("</d>");
	}
	
	/**
	 * only simple JSONObject contains base props,no sub obj
	 * @return
	 */
	public JSONObject toPropJSONObject()
	{
		JSONObject r = new JSONObject() ;
		for(String pn:this.getParamNames())
		{
			Object v = this.getParamValue(pn);
			if(v==null)
				continue ;
			r.put(pn, v);
		}
		return r ;
	}
	
	
	public void fromPropJSONObject(JSONObject jobj)
	{
		
		for(String k:jobj.keySet())
		{
			Object v = jobj.get(k) ;
			if(v instanceof JSONObject || v instanceof JSONArray)
				continue;
			this.setParamValue(k, v);
		}
	}

	private static void printStrJSON(String txt, Writer w) throws IOException
	{
		if (txt == null)
			return;
		int len = txt.length();
		for (int i = 0; i < len; i++)
		{
			char c = txt.charAt(i);
			if (c == '\'' || c == '\"')
				w.write('\\');
			w.write(c);
		}
	}

	public void writeOutJSON(Writer w) throws IOException
	{
		w.write("{param:{");

		boolean pm_bf = true;
		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			if (pm_bf)
			{
				pm_bf = false;
			}
			else
			{
				w.write(",");
			}
			String n = i.getKey();
			XmlVal xvi = i.getValue();
			if (xvi.isArray())
			{
				printStrJSON(n, w);
				w.write(":{t:\"");
				w.write(xvi.getValType());
				w.write("\",b_a:true,str_vals:[");

				List<String> strvs = xvi.getStrVals();
				if (strvs != null)
				{
					boolean bfirst = true;
					for (String sv : strvs)
					{
						if (bfirst)
						{
							bfirst = false;
							w.write("\"");
							printStrJSON(sv, w);
							w.write("\"");
						}
						else
						{
							w.write(",\"");
							printStrJSON(sv, w);
							w.write("\"");
						}
					}
				}
				w.write("]}");
			}
			else
			{
				printStrJSON(n, w);
				w.write(":{t:\"");
				w.write(xvi.getValType());
				w.write("\"");

				String strv = xvi.getStrVal();
				if (strv != null)
				{
					w.write(",str_v:\"");
					printStrJSON(xvi.getStrVal(), w);
					w.write("\"");
				}

				w.write("}");
			}
		}

		w.write("},data:{");

		boolean d_bf = true;

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			if (d_bf)
			{
				d_bf = false;
			}
			else
			{
				w.write(",");
			}

			XmlDataParam xdp = i.getValue();
			printStrJSON(i.getKey(), w);
			w.write(":");
			if (!xdp.bArray)
			{
				xdp.xmlData.writeOutJSON(w);
				continue;
			}

			w.write("[");

			boolean d_sub_f = true;
			for (XmlData tmpxd : xdp.xmlDatas)
			{
				if (d_sub_f)
					d_sub_f = false;
				w.write(",");
				tmpxd.writeOutJSON(w);
			}

			w.write("]");
		}
		w.write("}}");
	}

	public Element toXmlElement(Document doc)
	{
		Element dataele = doc.createElement("d");

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			XmlVal xvi = i.getValue();

			Element paramele = doc.createElement("p");
			dataele.appendChild(paramele);

			if (xvi.isArray())
			{
				paramele.setAttribute("t", xvi.getValType());
				paramele.setAttribute("n", i.getKey());
				paramele.setAttribute("b_a", "true");

				List<String> strvs = xvi.getStrVals();
				if (strvs != null)
				{
					for (String sv : strvs)
					{
						Element valele = doc.createElement("v");
						valele.appendChild(doc.createTextNode(sv));
						paramele.appendChild(valele);
					}
				}
			}
			else
			{
				paramele.setAttribute("t", xvi.getValType());
				paramele.setAttribute("n", i.getKey());

				String strv = xvi.getStrVal();
				if (strv != null)
					paramele.setAttribute("v", strv);
			}
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			XmlDataParam xdp = i.getValue();
			Element dpele = doc.createElement("dp");
			dataele.appendChild(dpele);

			dpele.setAttribute("n", i.getKey());

			if (xdp.bArray)
				dpele.setAttribute("b_a", "true");

			if (xdp.bArray)
			{
				for (XmlData tmpxd : xdp.xmlDatas)
				{
					dpele.appendChild(tmpxd.toXmlElement(doc));
				}
			}
			else
			{
				dpele.appendChild(xdp.xmlData.toXmlElement(doc));
			}
		}

		return dataele;
	}

	/**
	 * ��xmldata�е����ݣ����������׶��Ĵ�ӡ�ṹ���
	 * 
	 * @return
	 */
	public String toPrintString()
	{
		return null;
	}

	/**
	 * ת��ΪASCII��ʾ���ַ���
	 * 
	 * @return
	 */
	public String toHexString()
	{
		try
		{
			byte[] tmps = toXmlString().getBytes("UTF-8");
			return BinHexTransfer.TransBinToHexStr(tmps);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static XmlData parseFromXmlElement(Element datasetxe)
	{
		String tagn = datasetxe.getTagName();
		if (!tagn.equals("data") && !tagn.equals("d"))
			throw new IllegalArgumentException("not data xml element!");

		XmlData xds = new XmlData();

		for (Element xe : XmlHelper.getSubChildElement(datasetxe, TAGS_PARAM))
		{
			String ptype = xe.getAttribute("t");
			if (!xe.hasAttribute("t"))
				ptype = xe.getAttribute("type");

			if (ptype == null || ptype.equals(""))
				ptype = XmlVal.VAL_TYPE_STR;

			String pname = xe.getAttribute("n");
			if (!xe.hasAttribute("n"))
				pname = xe.getAttribute("name");

			if (pname == null || pname.equals(""))
				throw new IllegalArgumentException(
						"param xml element has no pname attribute!");

			String strba = xe.getAttribute("b_a");
			if (!xe.hasAttribute("b_a"))
				strba = xe.getAttribute("is_array");

			boolean barray = "true".equals(strba);
			XmlVal vi = null;
			if (barray)
			{
				List<String> ss = new ArrayList<String>();
				for (Element valxe : XmlHelper.getSubChildElement(xe, TAGS_VAL))
				{
					ss.add(XmlHelper.getElementFirstText(valxe));
				}

				vi = new XmlVal(ptype, ss);
			}
			else
			{
				String pval = xe.getAttribute("v");
				if (!xe.hasAttribute("v"))
					pval = xe.getAttribute("val");
				vi = new XmlVal(ptype, pval);
			}

			xds.pname2val.put(pname, vi);
		}

		for (Element xe : XmlHelper.getSubChildElement(datasetxe,
				TAGS_DATA_PARAM))
		{
			String n = xe.getAttribute("n");
			if (!xe.hasAttribute("n"))
				n = xe.getAttribute("name");

			if (n == null || n == "")
				throw new IllegalArgumentException(
						"param xml element has no pname attribute!");

			String strba = xe.getAttribute("b_a");
			if (!xe.hasAttribute("b_a"))
				strba = xe.getAttribute("is_array");

			boolean barray = "true".equals(strba);
			List<Element> eles = XmlHelper
					.getSubChildElementList(xe, TAGS_DATA);
			if (barray)
			{
				XmlDataParam xdp = new XmlDataParam();
				xdp.bArray = true;
				xdp.xmlDatas = new ArrayList<XmlData>();

				for (Element subxe : eles)
					xdp.xmlDatas.add(parseFromXmlElement(subxe));

				xds.pname2data.put(n, xdp);
			}
			else
			{
				XmlDataParam xdp = new XmlDataParam();

				if (eles == null || eles.size() <= 0)
					throw new IllegalArgumentException(
							"data_param element has no data sub element!");
				xdp.xmlData = parseFromXmlElement(eles.get(0));
				xdp.bArray = false;
				xds.pname2data.put(n, xdp);
			}
		}

		return xds;
	}

	public static XmlData parseFromHttpRequest(HttpServletRequest req,
			String p_prefix) throws Exception
	{
		XmlData xd = new XmlData();
		updateXmlDataFromHttpRequest(xd, req, p_prefix);
		return xd;
	}

	public static void updateXmlDataFromHttpRequest(XmlData xd,
			HttpServletRequest req, String p_prefix) throws Exception
	{
		for (Enumeration en = req.getParameterNames(); en.hasMoreElements();)
		{
			String pn = (String) en.nextElement();
			if (pn.startsWith(p_prefix))
			{
				String strp = pn.substring(p_prefix.length());
				XmlDataPath xdp = new XmlDataPath(strp);
				String pv = req.getParameter(pn);
				if (pv == null)
					continue;

				if (xdp.isStruct())
				{
					if (pv.equals(""))
						continue;

					// �ύ�Ĳ�����һ��XmlData
					XmlData tmpxd = XmlData.parseFromHexString(pv);
					xd.setSubDataSingleByPath(xdp, tmpxd);
				}
				else
				{
					pv = Convert.decodeSmartUrl(pv);
					xd.setParamXmlValStrByPath(xdp, pv);
				}
				// inputps.setProperty(pn.substring(3),pv) ;
			}
		}
	}

	public static XmlData parseFromUrlStr(String urlstr) throws Exception
	{
		return parseFromUrlStr(urlstr, null);
	}

	public static XmlData parseFromUrlStr(String urlstr, String pn_prefix)
			throws Exception
	{
		if (urlstr == null)
			return null;

		XmlData xd = new XmlData();

		StringTokenizer st = new StringTokenizer(urlstr.trim(), "&?");
		while (st.hasMoreTokens())
		{
			String pnv = st.nextToken();
			if (pn_prefix != null)// && !pn_prefix.equals(""))
			{
				if (!pnv.startsWith(pn_prefix))
					continue;

				if (!pn_prefix.equals(""))
					pnv = pnv.substring(pn_prefix.length());
			}
			else
			{
				if (!pnv.startsWith("dx_") && !pnv.startsWith("xd_"))
					continue;
				pnv = pnv.substring(3);
			}

			int k = pnv.indexOf('=');
			if (k <= 0)
				continue;
			String strp = pnv.substring(0, k);
			XmlDataPath xdp = new XmlDataPath(strp);
			String pv = pnv.substring(k + 1);
			if (xdp.isStruct())
			{
				if (pv.equals(""))
					continue;

				// �ύ�Ĳ�����һ��XmlData
				XmlData tmpxd = XmlData.parseFromHexString(pv);
				xd.setSubDataSingleByPath(xdp, tmpxd);
			}
			else
			{
				xd.setParamXmlValStrByPath(xdp, pv);
			}
		}

		return xd;
	}

	/**
	 * ������xml�ļ���Ӧ��Ԫ��װ�� �������ļ�ֻ�нڵ�Ԫ�ء��ӽڵ�����ԣ�HashMap<String,String>���������ı��������
	 * һ������װ���ֹ�д�������ļ�
	 * 
	 * @param ele
	 */
	public void fromConfXmlElement(Element ele)
	{
		HashMap<String, String> n2v = XmlHelper.getEleAttrNameValueMap(ele);
		if (n2v != null)
		{
			for (Map.Entry<String, String> nv : n2v.entrySet())
			{
				this.setParamValue(nv.getKey(), nv.getValue());
			}
		}

		for (Element sele : XmlHelper.getSubChildElement(ele, "*"))
		{
			String tagn = sele.getTagName();
			// ȫ��ʹ��sub array
			List<XmlData> xds = this.getOrCreateSubDataArray(tagn);
			XmlData tmpxd = new XmlData();
			tmpxd.fromConfXmlElement(sele);
			xds.add(tmpxd);
		}

	}
	
	public void fromConfXmlStream(InputStream inputs)
		throws Exception
	{
		//InputStreamReader sr = new InputStreamReader(inputs, "UTF-8");
		InputSource is = new InputSource(inputs);
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(is);
		fromConfXmlElement(doc.getDocumentElement());
	}
	
	public void fromConfXmlFile(File f) throws Exception
	{
//		InputStreamReader sr = new InputStreamReader(inputs, "UTF-8");
		//InputSource is = new InputSource(f);
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(f);
		fromConfXmlElement(doc.getDocumentElement());
	}

	public Element toConfXmlElement(Document doc, String root_tagname)
	{
		return toConfXmlElement(this, doc, root_tagname);
	}
	
	public void toConfXmlStream(String root_tagname,OutputStream outputs)
		throws Exception
	{
		Document doc = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.newDocument();
		Element root = toConfXmlElement(doc, root_tagname);

		doc.appendChild(root);
		outputXml(doc, outputs) ;
	}
	
	public void writeToConfXmlFile(String roottag,File f)
		throws Exception
	{
		File df = f.getParentFile() ;
		if(!df.exists())
		{
			df.mkdirs() ;
		}
		
		FileOutputStream fos = null ;
		try
		{
			fos = new FileOutputStream(f) ;
			toConfXmlStream(roottag,fos) ;
		}
		finally
		{
			if(fos!=null)
				fos.close() ;
		}
	}
	
	public String toConfXmlStr(String root_tagname)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
			toConfXmlStream(root_tagname,baos) ;
			
			return baos.toString() ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
			return null ;
		}
	}

	private static void outputXml(Document doc, OutputStream outputs)
			throws Exception
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");// �����ĵ��Ļ���������
		
		//PrintWriter pw = new PrintWriter(outputs);
		OutputStreamWriter osw = new OutputStreamWriter(outputs,"UTF-8") ;
		StreamResult result = new StreamResult(osw);
		transformer.transform(source, result);
	}

	private static Element toConfXmlElement(XmlData curxd, Document doc,
			String tagname)
	{
		Element rele = doc.createElement(tagname);

		for (String pn : curxd.getParamNames())
		{
			String pv = curxd.getParamXmlValStr(pn);
			if (pv == null)
				continue;
			rele.setAttribute(pn, pv);
		}

		List<String> ns = curxd.getSubDataArrayNames();
		if (ns != null)
		{
			for (String n : ns)
			{
				List<XmlData> subxds = curxd.getSubDataArray(n);
				if (subxds == null || subxds.size() <= 0)
					continue;
				for (XmlData tmpxd : subxds)
				{
					Element subele = toConfXmlElement(tmpxd, doc, n);
					if (subele == null)
						continue;
					rele.appendChild(subele);
				}
			}
		}
		
//		ns = curxd.getSubDataSingleNames();
//		if (ns != null)
//		{
//			for (String n : ns)
//			{
//				XmlData subxd = curxd.getSubDataSingle(n);
//				if (subxd == null)
//					continue;
//				
//				Element subele = toConfXmlElement(subxd, doc, n);
//				if (subele == null)
//					continue;
//				rele.appendChild(subele);
//				
//			}
//		}

		return rele;
	}

	public static String transXmlDataToUrlStr(XmlData xd)
	{
		if (xd == null)
			return "";

		String[] pns = xd.getParamNames();
		if (pns == null || pns.length == 0)
			return "";

		StringBuilder sb = new StringBuilder();

		for (String pn : pns)
		{
			XmlVal xv = xd.pname2val.get(pn);
			if (xv == null)
				continue;

			String strv = xv.getStrVal();
			if (strv == null)
				continue;

			sb.append("&dx_/").append(pn).append(':').append(xv.getValType())
					.append('=').append(xv.getStrVal());
		}

		return sb.toString();
	}

	public static XmlData parseFromXmlStr(String xmlstr) throws Exception
	{
		InputSource is = new InputSource(new StringReader(xmlstr));
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(is);
		return parseFromXmlElement(doc.getDocumentElement());
	}
	

	public static XmlData parseFromByteArray(byte[] cont, String encod)
			throws Exception
	{
		if (cont == null || cont.length <= 0)
			return new XmlData();

		ByteArrayInputStream bais = new ByteArrayInputStream(cont);
		InputStreamReader sr = new InputStreamReader(bais, encod);
		InputSource is = new InputSource(sr);
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(is);
		return parseFromXmlElement(doc.getDocumentElement());
	}

	public static XmlData parseFromStream(InputStream inputs, String encod)
			throws Exception
	{
		if (Convert.isNullOrEmpty(encod))
			encod = "UTF-8";

		InputStreamReader sr = new InputStreamReader(inputs, encod);
		InputSource is = new InputSource(sr);
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(is);
		return parseFromXmlElement(doc.getDocumentElement());
	}

	public static XmlData parseFromReader(Reader r) throws Exception
	{
		// InputStreamReader sr = new InputStreamReader(inputs, encod);
		InputSource is = new InputSource(r);
		DocumentBuilder db = xmlDocBuilderFactory.newDocumentBuilder();
		// db.
		Document doc = db.parse(is);
		return parseFromXmlElement(doc.getDocumentElement());
	}

	public static XmlData parseFromByteArrayUTF8(byte[] cont) throws Exception
	{
		return parseFromByteArray(cont, "UTF-8");
	}

	public static XmlData parseFromHexString(String hexstr) throws Exception
	{
		if (hexstr == null || hexstr.equals(""))
			return null;

		byte[] bs = BinHexTransfer.TransHexStrToBin(hexstr);
		if (bs == null)
			return null;

		return parseFromByteArray(bs, "UTF-8");
	}

	/**
	 * ���ļ��ж�ȡXmlData������
	 * 
	 * @param filepath
	 * @return
	 * @throws Exception
	 */
	public static XmlData readFromFile(String filepath) throws Exception
	{
		File f = new File(filepath);
		return readFromFile(f);
	}

	public static XmlData readFromFile(File f) throws Exception
	{
		if (!f.exists())
			return null;

		byte[] buf = new byte[(int) f.length()];
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(f);
			fis.read(buf);
			return XmlData.parseFromByteArrayUTF8(buf);
		}
		finally
		{
			if (fis != null)
				fis.close();
		}
	}

	/**
	 * ��һ��XmlDataд�뵽ָ�����ļ�·����
	 * 
	 * @param xd
	 * @param filepath
	 * @throws Exception
	 */
	public static void writeToFile(XmlData xd, String filepath)
			throws Exception
	{
		File f = new File(filepath);
		writeToFile(xd, f);
	}

	public static void writeToFile(XmlData xd, File f) throws Exception
	{

		File fp = f.getParentFile();
		fp.mkdirs();

		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(f);

			byte[] cont = null;
			if (xd != null)
				cont = xd.toBytesWithUTF8();
			else
				cont = new byte[0];

			fos.write(cont);
		}
		finally
		{
			if (fos != null)
				fos.close();
		}
	}

	public Object getPropValue(String propname)
	{
		XmlVal xv = this.pname2val.get(propname);
		if (xv != null)
		{
			if (xv.bArray)
				return xv.getObjectVals();
			else
				return xv.getObjectVal();
		}

		XmlDataParam xdp = this.pname2data.get(propname);
		if (xdp != null)
		{
			if (xdp.bArray)
				return xdp.xmlDatas;
			else
				return xdp.xmlData;
		}

		return null;
	}

	/**
	 * ������յĸ�ʽ�����������ĳ���
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public int calCompactWriteNotXmlStreamLen()
			throws UnsupportedEncodingException
	{
		return calCompactNotXmlLen() + 5;
	}

	private int calCompactNotXmlLen() throws UnsupportedEncodingException
	{
		int r = 2;

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			r += 2;
			String n = i.getKey();
			XmlVal xvi = i.getValue();
			// osw.write(XmlVal.T_PROP) ;
			byte[] nbs = n.getBytes("UTF-8");
			r += 4;
			// osw.write(DataUtil.intToBytes(nbs.length)) ;
			// osw.write(nbs) ;
			r += nbs.length;

			r += xvi.calCompactNotXmlLen();
			// xvi.writeCompactToStream(osw);
			// osw.write(100+XmlVal.T_PROP) ;
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			String n = i.getKey();
			XmlDataParam xdp = i.getValue();

			if (xdp.bArray)
			{
				// osw.write(XmlVal.T_XMLDATA_SUB_ARRAY) ;
				r += 1;

				byte[] nbs = n.getBytes("UTF-8");
				r += 4;
				// osw.write(DataUtil.intToBytes(nbs.length)) ;
				// osw.write(nbs) ;
				r += nbs.length;

				for (XmlData tmpxd : xdp.xmlDatas)
				{
					// tmpxd.writeCompactNotXmlToStream(osw);
					r += tmpxd.calCompactNotXmlLen();
				}
				// osw.write(100+XmlVal.T_XMLDATA_SUB_ARRAY) ;
				r += 1;
			}
			else
			{
				// osw.write(XmlVal.T_XMLDATA_SUB) ;
				r += 1;
				byte[] nbs = n.getBytes("UTF-8");
				// osw.write(DataUtil.intToBytes(nbs.length)) ;
				r += 4;
				// osw.write(nbs) ;
				r += nbs.length;

				// xdp.xmlData.writeCompactNotXmlToStream(osw);
				r += xdp.xmlData.calCompactNotXmlLen();
				// osw.write(100+XmlVal.T_XMLDATA_SUB) ;
				r += 1;
			}
		}

		return r;
	}

	/**
	 * ���ݴ���ʱʹ�õķ������÷�����byte[]���͵������������⴦��
	 * Ϊ�˱���byte[]���͵����ݣ���ת����hex�ַ�����Ȼ����ת����byte[]����Ϊ������,����Ч�ʵͣ�����ռ���ڴ�.
	 * 
	 * ������ͨ��ֱ�������������xml�������ݣ����Ұ�byte[]������Ϊxml��ʽ������������� �����ܵļ����ڴ��ռ�úͼ������ݴ���
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void writeCompactNotXmlToStream(OutputStream osw) throws IOException
	{
		// write head
		osw.write('?');
		int len = calCompactNotXmlLen();
		osw.write(DataUtil.intToBytes(len));
		// write len
		writeCompactNotXmlXmlData(osw);
	}

	private void writeCompactNotXmlXmlData(OutputStream osw) throws IOException
	{
		osw.write(XmlVal.T_XMLDATA);

		for (Map.Entry<String, XmlVal> i : pname2val.entrySet())
		{
			String n = i.getKey();
			XmlVal xvi = i.getValue();
			osw.write(XmlVal.T_PROP);
			byte[] nbs = n.getBytes("UTF-8");
			osw.write(DataUtil.intToBytes(nbs.length));
			osw.write(nbs);
			xvi.writeCompactToStream(osw);
			osw.write(100 + XmlVal.T_PROP);
		}

		for (Map.Entry<String, XmlDataParam> i : pname2data.entrySet())
		{
			String n = i.getKey();
			XmlDataParam xdp = i.getValue();

			if (xdp.bArray)
			{
				osw.write(XmlVal.T_XMLDATA_SUB_ARRAY);

				byte[] nbs = n.getBytes("UTF-8");
				osw.write(DataUtil.intToBytes(nbs.length));
				osw.write(nbs);

				for (XmlData tmpxd : xdp.xmlDatas)
				{
					tmpxd.writeCompactNotXmlXmlData(osw);
				}
				osw.write(100 + XmlVal.T_XMLDATA_SUB_ARRAY);
			}
			else
			{
				osw.write(XmlVal.T_XMLDATA_SUB);
				byte[] nbs = n.getBytes("UTF-8");
				osw.write(DataUtil.intToBytes(nbs.length));
				osw.write(nbs);

				xdp.xmlData.writeCompactNotXmlXmlData(osw);
				osw.write(100 + XmlVal.T_XMLDATA_SUB);
			}
		}

		osw.write(100 + XmlVal.T_XMLDATA);
	}

	public void readCompactNotXmlFromStream(InputStream isw) throws IOException
	{
		PushbackInputStream pbis = new PushbackInputStream(isw);
		int c = pbis.read();
		if (c != '?')
			throw new IOException("illegal argument!");

		int len = DataUtil.readInt(pbis);
		if (len <= 0)
			throw new IOException("illegal len!");
		if (len > 104857600)
			throw new IOException("illegal data len,big than 100M");

		readCompactNotXmlFromStream(len, this, pbis);
	}

	private static String readPropName(int datalen, PushbackInputStream r)
			throws IOException
	{
		int nlen = DataUtil.readInt(r);
		if (nlen > 1000 || nlen > datalen)
			throw new IllegalArgumentException("name is too big;len=" + nlen);

		byte[] bs = DataUtil.readBytes(r, nlen);
		return new String(bs, "UTF-8");
	}

	private static void readCompactNotXmlFromStream(int datalen, XmlData xd,
			PushbackInputStream r) throws IOException
	{
		int b = r.read();
		if (b != XmlVal.T_XMLDATA)
		{
			throw new IOException(
					"illegal format, stream must start with xmldata tag");
		}

		while ((b = r.read()) >= 0)
		{
			if (b > 100)
			{
				b -= 100;
				if (b != XmlVal.T_XMLDATA)
					throw new IOException(
							"illegal format, stream must end with xmldata tag");

				return;
			}

			r.unread(b);

			switch (b)
			{
			case XmlVal.T_PROP:
				readCompactNotXmlProp(datalen, xd, r);
				break;
			case XmlVal.T_XMLDATA_SUB:
				readCompactNotXmlSubXmlData(datalen, xd, r);
				break;
			case XmlVal.T_XMLDATA_SUB_ARRAY:
				readCompactNotXmlSubXmlDataArray(datalen, xd, r);
				break;
			default:
				throw new IOException("invalid format!");
			}
		}

	}

	private static void readCompactNotXmlProp(int datalen, XmlData curxd,
			PushbackInputStream r) throws IOException
	{
		int b = r.read();

		// int nlen = DataUtil.readInt(r) ;
		// if(nlen>1000||nlen>datalen)
		// throw new IllegalArgumentException("name is too big;len="+nlen) ;
		//		
		// byte[] bs = DataUtil.readBytes(r, nlen) ;
		String n = readPropName(datalen, r);
		XmlVal xv = new XmlVal();
		xv.readCompactFromStream(datalen, r);

		curxd.setParamXmlVal(n, xv);

		b = r.read();
		if (b > 100 && (b - 100) == XmlVal.T_PROP)
			return;
		else
			throw new IOException("no end tag with prop");
	}

	private static void readCompactNotXmlSubXmlData(int datalen, XmlData curxd,
			PushbackInputStream r) throws IOException
	{
		int b = r.read();

		// int nlen = DataUtil.readInt(r) ;
		// if(nlen>1000||nlen>datalen)
		// throw new IllegalArgumentException("name is too big;len="+nlen) ;
		//		
		// byte[] bs = DataUtil.readBytes(r, nlen) ;
		String n = readPropName(datalen, r);
		XmlData nxd = new XmlData();
		readCompactNotXmlFromStream(datalen, nxd, r);
		curxd.setSubDataSingle(n, nxd);

		b = r.read();
		if (b > 100 && (b - 100) == XmlVal.T_XMLDATA_SUB)
			return;
		else
			throw new IOException("no end tag with xmldata");
	}

	private static void readCompactNotXmlSubXmlDataArray(int datalen,
			XmlData curxd, PushbackInputStream r) throws IOException
	{
		int b = r.read();

		// int nlen = DataUtil.readInt(r) ;
		// byte[] bs = DataUtil.readBytes(r, nlen) ;
		String n = readPropName(datalen, r);
		List<XmlData> xds = curxd.getOrCreateSubDataArray(n);
		while ((b = r.read()) >= 0)
		{
			if (b != XmlVal.T_XMLDATA)
			{
				r.unread(b);
				break;
			}

			r.unread(b);
			XmlData nxd = new XmlData();
			readCompactNotXmlFromStream(datalen, nxd, r);
			xds.add(nxd);
		}

		b = r.read();
		if (b > 100 && (b - 100) == XmlVal.T_XMLDATA_SUB_ARRAY)
			return;
		else
			throw new IOException("no end tag with xmldata array");
	}

	public byte[] toCompactNotXmlBytes()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writeCompactNotXmlToStream(baos);
			return baos.toByteArray();
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			throw new RuntimeException(ee);
		}
	}

	public void fromCompactNotXmlBytes(byte[] bs)
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);
			readCompactNotXmlFromStream(bais);
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
			throw new RuntimeException(ee);
		}
	}

	public static void main(String[] args) throws Exception
	{
		XmlData xd = new XmlData();

		xd.setParamValue("bd", new BigDecimal(
				"777778743433452345234543.43523452345"));
		xd.setParamValue("aa", 10);
		xd.setParamValue("tt", new Date());

		List<XmlData> xds = xd.getOrCreateSubDataArray("ss");
		XmlData xd0 = new XmlData();
		xd0.setParamValue("s1", "s11111");
		xd0.setParamValues("s2", new String[] { "s12222", "��������" });
		xd0.setParamValue("kk", (byte) 12);
		xd0.setParamValue("kk1", (short) 123);
		xd0.setParamValue("kk2", 123);
		xd0.setParamValue("kk3", System.currentTimeMillis());
		xd0.setParamValue("kk4", 23434424.4545d);
		xd0.setParamValue("kk5", 234324.4545f);
		xd0
				.setParamValues("kk6", new Float[] { 234324.4545f, 3233.4f,
						434.3f });
		xd0.setParamValue("bbbbs", "hah�����ݶ����Ŷ".getBytes("UTF-8"));
		// xd0.setParamValue("kk5", 234324.4545f);
		xds.add(xd0);
		xd0 = new XmlData();
		xd0.setParamValue("s1", "p11111");
		xd0.setParamValue("s2", "p12222");
		xds.add(xd0);

		String xmlstr0 = xd.toXmlString();
		System.out.println("to xml==" + xmlstr0);

		xd = XmlData.parseFromXmlStr(xmlstr0);
		String s1 = xd.toXmlString();
		System.out.println("vv=" + xd.getParamValueBigDecimal("bd", null));
		System.out.println("from xml str==" + s1);

		System.out.println("");
		String xmlstr = xd.toXmlStringCompact();
		System.out.println("to compact xml==" + xmlstr);
		xd = XmlData.parseFromXmlStr(xmlstr);
		String s2 = xd.toXmlString();
		System.out.println("to xml from compact recover==" + s2);

		System.out.println("sor - last equal=" + s2.equals(xmlstr0));

		//
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = xd.calCompactWriteNotXmlStreamLen();
		System.out.println("cal len===" + len);
		xd.writeCompactNotXmlToStream(baos);

		byte[] bs = baos.toByteArray();
		System.out.println("byte array len===" + bs.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		XmlData bsxd = new XmlData();
		bsxd.readCompactNotXmlFromStream(bais);

		String s3 = bsxd.toXmlString();
		System.out.println("s3===" + s3);
		System.out.println("sor - compact not xml equal s1=" + s3.equals(s1));

		PrintWriter pw = new PrintWriter(System.out);
		bsxd.writeOutJSON(pw);
		pw.flush();
		
		System.out.println(bsxd.toConfXmlStr("tag1")) ;
	}
}

package org.iottree.core.util.xmldata;

import java.io.*;
import java.util.*;

import org.iottree.core.util.xmldata.XmlDataPath.PathItem;


public class XmlDataStruct implements IXmlDataDef
{
	public static enum StoreType
	{
		Normal(1), //ȱʡ���
		Separate(2), //��Ӧ�����������洢
		SeparateIdx(3); //��Ӧ�����������洢�����ҽ�������
		
		private final int val;
		StoreType(int val) {
	    	this.val = val;
	    }
		
		public int getIntValue()
		{
			return val ;
		}
		
		public static StoreType valueOf(int v)
		{
			switch(v)
			{
			case 1:
				return Normal;
			case 2:
				return Separate;
			case 3:
				return SeparateIdx;
			default:
				throw new IllegalArgumentException("unknow StoreType value="+v);
			}
		}
	}
	
	/**
	 * �����ݳ�Ա�Ŀ�������
	 * 
	 * @author Jason Zhu
	 */
//	public static enum CtrlType
//	{
//		None(1), //ȱʡ���,�����κο���
//		Hidden(2), //
//		ReadOnly(3); //
//		
//		private final int val;
//		CtrlType(int val) {
//	    	this.val = val;
//	    }
//		
//		public int getIntValue()
//		{
//			return val ;
//		}
//		
//		public static StoreType valueOf(int v)
//		{
//			switch(v)
//			{
//			case 1:
//				return Normal;
//			case 2:
//				return Separate;
//			case 3:
//				return SeparateIdx;
//			default:
//				throw new IllegalArgumentException("unknow StoreType value="+v);
//			}
//		}
//	}
	//public static final XmlDataStruct EMPTY_STRUCT = new XmlDataStruct();
	
	/**
	 * Ϊ��ͬһ�����̿ռ��ڽ��п���ճ������������֧��
	 */
	public static Object COPY_OBJ = null ;
	
	public String getValueTypeStr()
	{
		return "xds" ;
	}
	
	public static void copy(XmlDataMember si)
	{
		COPY_OBJ = si ;
	}
	
	public static void copy(XmlDataStruct xds)
	{
		COPY_OBJ = xds ;
	}
	
	public static boolean canPasteTo(XmlDataStruct parentxds)
	{
		if(parentxds==null)
			return false;
		
		if(COPY_OBJ==null)
			return false;
		
		if(COPY_OBJ instanceof XmlDataMember)
			return true ;
		
		if(COPY_OBJ instanceof XmlDataStruct)
		{
			return true ;
		}
		
		return false;
	}
	
	public static void pasteTo(XmlDataStruct parentxds)
	{
		if(COPY_OBJ==null)
			return;
		
		if(COPY_OBJ instanceof XmlDataMember)
		{
			XmlDataMember si = (XmlDataMember)COPY_OBJ ;
			si = si.copyMe();
			si.belongTo = parentxds;
			parentxds.pname2XmlDataMember.put(si.pname, si);
			return ;
		}
		
		if(COPY_OBJ instanceof XmlDataStruct)
		{
			XmlDataStruct xds = (XmlDataStruct)COPY_OBJ;
			xds = xds.copyMe();
			
			if(xds.name==null||xds.name.equals(""))
			{//���ڲ���Ϣ����
				for(String n:xds.pname2XmlDataMember.keySet())
				{
					XmlDataMember si = xds.pname2XmlDataMember.get(n);
					si.belongTo = parentxds ;
					parentxds.pname2XmlDataMember.put(n, si);
				}
				
				for(String n:xds.pname2SubST.keySet())
				{
					XmlDataStruct tmpxds = xds.pname2SubST.get(n);
					tmpxds.parent = parentxds ;
					parentxds.pname2SubST.put(n, tmpxds);
				}
			}
			else
			{
				xds.parent = parentxds ;
				parentxds.pname2SubST.put(xds.name, xds);
			}
			return ;
		}
	}
	
	private static void checkName(String pname)
	{
		StringBuffer fr = new StringBuffer();
		if(!checkName(pname,fr))
			throw new IllegalArgumentException("invalid name["+pname+"] reson:"+fr.toString());
	}
	
	public static boolean checkName(String pname,StringBuffer failedreson)
	{
		if(pname==null||pname.equals(""))
		{
			failedreson.append("name cannot be null or empty!");
			return false;
		}
		
		char c0 = pname.charAt(0);
		if(!((c0>='a'&&c0<='z')||(c0>='A'&&c0<='Z')||c0=='_'))
		{
			failedreson.append("name first char must be a-z|A-Z|_");
			return false;
		}
		
		int len = pname.length();
		for(int i = 1 ; i < len ; i ++)
		{
			char c = pname.charAt(i);
			if(!((c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')||c=='_'))
			{
				failedreson.append("name char must be a-z|A-Z|0-9|_");
				return false;
			}
		}
		
		return true ;
	}
	
	private Hashtable<String, XmlDataMember> pname2XmlDataMember = new Hashtable<String, XmlDataMember>();

	private Hashtable<String, XmlDataStruct> pname2SubST = new Hashtable<String, XmlDataStruct>();

	private String name = "";

	private boolean bArray = false;

	private boolean bNullable = true;
	
	/**
	 * ����洢���
	 * 
	 * ��������ӽṹ,ͬʱ�ýṹ�����Ƚṹ�����ڷ���洢���,��ýṹ�Ϳ���������Ϊ����洢���ݵı��
	 * 
	 * �ò���һ�������û��ʲô�ô�,�����ʹ�û�����Ҫ���Ǵ洢�ĸ�Ч��,�ò�������������
	 * 
	 * ʹ������:
	 * 	�������̵�ʵ��ʹ��XmlData���д洢,��ʵ���а����Ķ��ActivityIns,���Կ��Ƿ���洢.����
	 * 	��Ӧ��ɾ��������.
	 */
	private boolean bSepStorage = false;

	transient XmlDataStruct parent = null;

	public XmlDataStruct()
	{

	}
	
	public XmlDataStruct(String n)
	{
		this(n,false,true);
	}
	
	public XmlDataStruct(String n,boolean ba,boolean nullable)
	{
		checkName(n);
		
		name = n ;
		bArray = ba ;
		bNullable = nullable ;
	}
	
	public XmlDataStruct copyMe()
	{
		XmlDataStruct xds = new XmlDataStruct();
		
		xds.name = name;

		xds.bArray = bArray;

		xds.bNullable = bNullable;
		
		xds.bSepStorage = bSepStorage;
		
		for(String n:pname2XmlDataMember.keySet())
		{
			XmlDataMember si = pname2XmlDataMember.get(n);
			si = si.copyMe() ;
			si.belongTo = xds ;
			xds.pname2XmlDataMember.put(n, si);
		}
		
		for(String n:pname2SubST.keySet())
		{
			XmlDataStruct tmpxds = pname2SubST.get(n);
			tmpxds = tmpxds.copyMe() ;
			tmpxds.parent = xds ;
			xds.pname2SubST.put(n, tmpxds);
		}
		
		return xds ;
	}
	
	/**
	 * Ϊ��֧�ֶ��Ѿ����ڵ�����(�϶��������ݽṹ)�����²���
	 * ��Ҫһ�������Ľṹ,���Կ���ʹ�ø÷���--�÷������صĽṹ
	 * ��ԭ���Ļ�����ͬ,��û�б�������
	 * @return
	 */
	public XmlDataStruct copyMeWithAllNullable()
	{
		XmlDataStruct xds = new XmlDataStruct();
		
		xds.name = name;

		xds.bArray = bArray;

		xds.bNullable = true;
		
		xds.bSepStorage = bSepStorage;
		
		for(String n:pname2XmlDataMember.keySet())
		{
			XmlDataMember si = pname2XmlDataMember.get(n);
			si = si.copyMe() ;
			si.belongTo = xds ;
			si.setNullable(true) ;
			xds.pname2XmlDataMember.put(n, si);
		}
		
		for(String n:pname2SubST.keySet())
		{
			XmlDataStruct tmpxds = pname2SubST.get(n);
			tmpxds = tmpxds.copyMe() ;
			tmpxds.parent = xds ;
			tmpxds.bNullable = true ;
			xds.pname2SubST.put(n, tmpxds);
		}
		
		return xds ;
	}
	
	/**
	 * �������ṹ�е���Ϣ��ϵ����ṹ��
	 * @param oxds
	 * @param bcover true��ʾ,�������������,���������ṹ�е����ݽ��и���
	 * 		false��ʾ������(=����)
	 */
	public void combineAppend(XmlDataStruct oxds,boolean bcover)
	{
		for(Map.Entry<String, XmlDataMember> n2m:oxds.pname2XmlDataMember.entrySet())
		{
			String n = n2m.getKey();
			if(pname2XmlDataMember.containsKey(n)&&!bcover)
				continue ;
			
			XmlDataMember si = n2m.getValue();
			si = si.copyMe() ;
			si.belongTo = this ;
			pname2XmlDataMember.put(n, si);
		}
		
		for(Map.Entry<String, XmlDataStruct> n2s:oxds.pname2SubST.entrySet())
		{
			String n = n2s.getKey();
			if(pname2SubST.containsKey(n)&&!bcover)
				continue ;
			
			XmlDataStruct tmpxds = n2s.getValue();
			tmpxds = tmpxds.copyMe() ;
			tmpxds.parent = this ;
			pname2SubST.put(n, tmpxds);
		}
	}
	
	public void combineAppend(XmlDataStruct oxds)
	{
		combineAppend(oxds,false);
	}

	public boolean isEmptyStruct()
	{
		if (pname2XmlDataMember.size() > 0)
			return false;

		if (pname2SubST.size() > 0)
			return false;

		return true;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof XmlDataStruct))
			return false;

		XmlDataStruct xds = (XmlDataStruct) o;
		if (name == null || name.equals(""))
		{
			if (xds.name != null && !xds.name.equals(""))
				return false;
		}

		if (name == null || !name.equals(xds.name))
			return false;

		if (bArray != xds.bArray)
			return false;
		if (bNullable != xds.bNullable)
			return false;
		
		if(bSepStorage!=xds.bSepStorage)
			return false;

		if (pname2XmlDataMember.size() != xds.pname2XmlDataMember.size())
			return false;

		if (pname2SubST.size() != xds.pname2SubST.size())
			return false;

		for (Enumeration en = pname2XmlDataMember.keys(); en.hasMoreElements();)
		{
			String k = (String) en.nextElement();
			XmlDataMember msi = pname2XmlDataMember.get(k);
			XmlDataMember osi = xds.pname2XmlDataMember.get(k);
			if (!msi.equals(osi))
				return false;
		}

		for (Enumeration en = pname2SubST.keys(); en.hasMoreElements();)
		{
			String k = (String) en.nextElement();
			XmlDataStruct mds = pname2SubST.get(k);
			XmlDataStruct ods = xds.pname2SubST.get(k);
			if (!mds.equals(ods))
				return false;
		}

		return true;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String n)
	{
		checkName(n);
		name = n;
	}
//	public void setName(String n)
//	{
//		name = n;
//	}

	public String getPath()
	{
		if (parent == null)
		{
//			if (name == null||name.equals(""))
//				return "/";
//
//			return "/" + name + "/";
			return "/";
		}

		return parent.getPath() + name + "/";
	}
	
	
	
	/**
	 * ���߸ýṹ�������������洢�ĳ�Ա
	 * @return
	 */
	public List<XmlDataMember> getSeparateXmlValDefs()
	{
		ArrayList<XmlDataMember> sis = new ArrayList<XmlDataMember>();
		getSeparateXmlValDefs(sis);
		return sis ;
	}
	
	/**
	 * ���ݱ��ṹ����ķ���洢
	 * ���˳��з���洢�ṹ��ɵ��Ӽ��ṹ
	 * @return �ɷ���洢��ɵĽṹ
	 */
	public XmlDataStruct filterSubSetSeparateStruct()
	{
		XmlDataStruct xds = new XmlDataStruct();
		List<XmlDataMember> sis = getSeparateXmlValDefs() ;
		for(XmlDataMember si:sis)
		{
			String ps = si.getPath() ;
			XmlDataPath xdp = new XmlDataPath(ps);
			xds.setXmlDataMemberByPath(xdp.getPath(), si.getValType(), si.isArray(), si.isNullable(), si.getMaxLen(), si.getStoreType());
		}
		return xds ;
	}
	
	private void getSeparateXmlValDefs(List<XmlDataMember> sis)
	{
		for(XmlDataMember si:pname2XmlDataMember.values())
		{
			StoreType st = si.getStoreTypeWithBelongTo() ;
			if(st==StoreType.Separate||st==StoreType.SeparateIdx)
			{
				sis.add(si) ;
			}
		}
		
		for(XmlDataStruct subxds:pname2SubST.values())
		{
			subxds.getSeparateXmlValDefs(sis);
		}
	}
	
	
	/**
	 * �����������Ľṹ�������ϱ��ṹ��XmlData���ݣ� �÷�������������������
	 * 
	 * @return
	 */
	public XmlData randomCreateData()
	{
		XmlData xd = new XmlData();

		for (Enumeration<String> en = pname2XmlDataMember.keys(); en
				.hasMoreElements();)
		{
			String n = en.nextElement();
			XmlDataMember si = pname2XmlDataMember.get(n);
			Object v = si.randomCreateValue();
			if (v != null)
			{
				if (si.isArray())
					xd.setParamValues(n, (Object[]) v);
				else
					xd.setParamValue(n, v);
			}
		}

		for (Enumeration<String> en = pname2SubST.keys(); en.hasMoreElements();)
		{
			String n = en.nextElement();
			XmlDataStruct xds = pname2SubST.get(n);
			if (xds.isArray())
			{
				int c = new Random().nextInt(5);
				List<XmlData> tmpxds = xd.getOrCreateSubDataArray(n);
				for (int i = 0; i < c; i++)
				{
					XmlData xd0 = xds.randomCreateData();
					tmpxds.add(xd0);
				}
			}
			else
			{
				XmlData xd0 = xds.randomCreateData();
				xd.setSubDataSingle(n, xd0);
			}
		}
		return xd;
	}

	public String toString()
	{
		return getPath();
	}

	public boolean isArray()
	{
		return bArray;
	}
	
	public boolean isSepStorage()
	{
		return bSepStorage;
	}

	/**
	 * �ж��Ƿ�����Ч�ķ���洢���
	 * ������ṹ�Ǹ�,����Ч,������ṹ�ĸ��ṹ������Ч�ķ�����
	 * @return
	 */
	public boolean isValidSepStorage()
	{
		if(!bSepStorage)
			return false;
		
		//���ṹû������
		if(this.parent==null)
			return false;
		
		XmlDataStruct tmpp = parent ;
		
		while(tmpp!=null)
		{
			if(tmpp.isValidSepStorage())
				return false;
			tmpp = tmpp.parent;
		}
		
		return true ;
	}
	/**
	 * �жϱ������Ҽ̳��˸��ṹ���Ƿ��Ƕ�ֵ���������һ��XmlData����Ԫ��
	 * 
	 * @return
	 */
	public boolean isArrayWithParent()
	{
		if (parent == null)
			return false;//��ֻ���Ƿ�����

		if (bArray)
			return true;

		boolean parray = parent.isArrayWithParent();
		if (parray)
			return true;

		return bArray;
	}

	public void setIsArray(boolean b)
	{
		bArray = b;
	}
	
	public void setIsSepStorage(boolean b)
	{
		bSepStorage = b ;
	}

	public boolean isNullable()
	{
		return bNullable;
	}

	public void setIsNullable(boolean b)
	{
		bNullable = b;
	}

	public XmlDataStruct getParent()
	{
		return parent;
	}
	

	public XmlDataMember setXmlDataMember(String pname, String valtype)
	{
		return setXmlDataMember(pname, valtype, false, true, -1,StoreType.Normal);
	}
	
	public XmlDataMember setXmlDataMember(String pname ,String title, String valtype)
	{
		return setXmlDataMember(pname,title, valtype, false, true, -1,StoreType.Normal,false,null);
	}
	
	public XmlDataMember setXmlDataMember(String pname ,String title, String valtype,String defstrv)
	{
		return setXmlDataMember(pname,title, valtype, false, true, -1,StoreType.Normal,false,defstrv);
	}
	
	public XmlDataMember setXmlDataMember(String pname ,String title, String valtype,boolean bmulti_rows)
	{
		return setXmlDataMember(pname,title, valtype, false, true, -1,StoreType.Normal,bmulti_rows,null);
	}
	
	public XmlDataMember setXmlDataMember(String pname ,String title, String valtype,boolean bmulti_rows,String strval)
	{
		return setXmlDataMember(pname,title, valtype, false, true, -1,StoreType.Normal,bmulti_rows,strval);
	}

	/**
	 * ���ýṹ��
	 * 
	 * @param path
	 * @param valtype
	 * @param barray
	 */
//	public void setStructItem(String pname, String valtype, boolean barray,
//			boolean bnullable, int maxlen)
//	{
//		setStructItem(pname, valtype, barray,
//				bnullable, maxlen,StoreType.Normal);
//		
//	}
	
	public XmlDataMember setXmlDataMember(String pname, String valtype, boolean barray,
			boolean bnullable, int maxlen)
	{
		return setXmlDataMember(pname, valtype, barray,
				bnullable, maxlen,StoreType.Normal);
	}
	
	public XmlDataMember setXmlDataMember(String pname,String valtype, boolean barray,
			boolean bnullable, int maxlen,StoreType st)
	{
		return setXmlDataMember(pname,null,valtype, barray,
				bnullable, maxlen,st,false,null);
	}
	
	public XmlDataMember setXmlDataMember(String pname,String title, String valtype, boolean barray,
			boolean bnullable, int maxlen,StoreType st)
	{
		return setXmlDataMember(pname,title, valtype, barray,
				bnullable, maxlen,st,false,null) ;
	}
	
	public XmlDataMember setXmlDataMember(String pname,String title, String valtype, boolean barray,
			boolean bnullable, int maxlen,StoreType st,boolean bmulti_rows,String defstrval)
	{
		//�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2XmlDataMember.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		XmlDataMember si = new XmlDataMember(pname,title, valtype, barray, bnullable,
				maxlen,st,bmulti_rows,defstrval);
		si.belongTo = this;
		si.orderNum = pname2XmlDataMember.size()+1 ;
		pname2XmlDataMember.put(pname, si);
		return si ;
	}
	
	public XmlDataMember setXmlDataMember(String pname,XmlValDef xvd)
	{
		XmlDataMember si = new XmlDataMember(pname,xvd);
		si.belongTo = this;
		pname2XmlDataMember.put(pname, si);
		return si ;
	}

	public void unsetXmlDataMember(String pname)
	{
		XmlDataMember si = pname2XmlDataMember.remove(pname);
		if (si != null)
			si.belongTo = null;
	}
	
	public List<XmlDataMember> getXmlDataMembers()
	{
		ArrayList<XmlDataMember> rets = new ArrayList<XmlDataMember>() ;
		rets.addAll(pname2XmlDataMember.values()) ;
		Collections.sort(rets);
		return rets;
	}

	public String[] getXmlDataMemberNames()
	{
		String[] rets = new String[pname2XmlDataMember.size()];
		pname2XmlDataMember.keySet().toArray(rets);
		return rets;
	}

	public XmlDataMember getXmlDataMember(String pname)
	{
		return pname2XmlDataMember.get(pname);
	}
	
	public List<XmlDataMember> getSubXmlDataMembers()
	{
		ArrayList<XmlDataMember> rets = new ArrayList<XmlDataMember>() ;
		rets.addAll(pname2XmlDataMember.values()) ;
		return rets;
	}

	public XmlDataStruct getOrCreateSubStruct(String pname)
	{
		StringBuffer fr = new StringBuffer();
		if(!checkName(pname,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
			
//		�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2SubST.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		XmlDataStruct xds = pname2SubST.get(pname);
		if (xds != null)
			return xds;

		synchronized (this)
		{
			//����ͬ���������Ѱ��һ��
			xds = pname2SubST.get(pname);
			if (xds != null)
				return xds;
			
			xds = new XmlDataStruct();
			xds.parent = this;
			xds.name = pname;
			pname2SubST.put(pname, xds);
			return xds;
		}
	}

	public XmlDataStruct setSubStruct(String pname, boolean barray,
			boolean bnullable,boolean bsep_store)
	{
		StringBuffer fr = new StringBuffer();
		if(!checkName(pname,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
		
//		�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2SubST.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		XmlDataStruct xds = pname2SubST.get(pname);
		if (xds == null)
		{
			synchronized (this)
			{
				if (xds == null)
				{
					xds = new XmlDataStruct();
					xds.parent = this;
					pname2SubST.put(pname, xds);
				}
			}
		}

		xds.name = pname;
		xds.bArray = barray;
		xds.bNullable = bnullable;
		xds.bSepStorage = bsep_store ;

		return xds;
	}
	
	public XmlDataStruct setSubStruct(XmlDataStruct xds)
	{
		String pname = xds.getName();
		if(pname==null||pname.equals(""))
			throw new IllegalArgumentException("sub xml data struct must has name");
		
		StringBuffer fr = new StringBuffer();
		if(!checkName(pname,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
		
//		�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2SubST.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		
		xds.parent = this;

		pname2SubST.put(pname, xds);
		
		return xds;
	}
	
	public XmlDataStruct setSubStruct(String pname,XmlDataStruct xds, boolean barray,
			boolean bnullable,boolean bsep_store)
	{
		StringBuffer fr = new StringBuffer();
		if(!checkName(pname,fr))
			throw new IllegalArgumentException("invalid StructItem name for:"+fr.toString());
		
//		�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2SubST.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		
		xds.name = pname;
		xds.bArray = barray;
		xds.bNullable = bnullable;
		xds.bSepStorage = bsep_store ;
		xds.parent = this;

		pname2SubST.put(pname, xds);
		
		return xds;
	}

	public String[] getSubStructNames()
	{
		String[] rets = new String[pname2SubST.size()];
		pname2SubST.keySet().toArray(rets);
		return rets;
	}

	public XmlDataStruct getSubStruct(String pname)
	{
//		�鿴�����Ƿ���֮�����ִ�Сд��ͬ��
		for(String s:pname2SubST.keySet())
		{
			if(s.equalsIgnoreCase(pname))
			{//����У�����ԭ��������
				pname = s ;
				break;
			}
		}
		
		return pname2SubST.get(pname);
	}

	public XmlDataStruct getSubStructByPath(String[] path)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length; i++)
		{
			tmpxds = tmpxds.getSubStruct(path[i]);
			if (tmpxds == null)
				return null;
		}

		return tmpxds;
	}
	
	public XmlDataStruct getSubStructByPath(PathItem[] path)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length; i++)
		{
			tmpxds = tmpxds.getSubStruct(path[i].getPathItemName());
			if (tmpxds == null)
				return null;
		}

		return tmpxds;
	}
	
	public XmlDataStruct getSubStructByPath(XmlDataPath xdp)
	{
		if(xdp.isRoot())
			return this ;
		
		return getSubStructByPath(xdp.getPath());
	}

	public void unsetSubStruct(String pname)
	{
		XmlDataStruct xds = pname2SubST.remove(pname);
		if (xds != null)
			xds.parent = null;
	}

	public void setXmlDataMemberByPath(String[] path, String valtype)
	{
		setXmlDataMemberByPath(path, valtype, false, true, -1);
	}
	
	
	
	public void setXmlDataMemberByPath(String strpath, String valtype)
	{
		XmlDataPath xdp = new XmlDataPath(strpath);
		setXmlDataMemberByPath(xdp.getPath(), valtype, false, true, -1);
	}
	
	public void setXmlDataMemberByPath(String strpath, String valtype,
			boolean barray, boolean bnullable, int maxlen)
	{
		XmlDataPath xdp = new XmlDataPath(strpath);
		setXmlDataMemberByPath(xdp.getPath(), valtype, barray, bnullable, maxlen);
	}
	
	public void setXmlDataMemberByPath(String[] path, String valtype,
			boolean barray, boolean bnullable, int maxlen)
	{
		 setXmlDataMemberByPath(path, valtype,
					barray, bnullable, maxlen,StoreType.Normal);
	}
	
	public void setXmlDataMemberByPath(PathItem[] path, String valtype,
			boolean barray, boolean bnullable, int maxlen)
	{
		 setXmlDataMemberByPath(path, valtype,
					barray, bnullable, maxlen,StoreType.Normal);
	}

	public void setXmlDataMemberByPath(String[] path, String valtype,
			boolean barray, boolean bnullable, int maxlen,StoreType st)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length - 1; i++)
		{
			tmpxds = tmpxds.getOrCreateSubStruct(path[i]);
		}

		tmpxds.setXmlDataMember(path[path.length - 1], valtype, barray, bnullable,
				maxlen,st);
	}
	
	/**
	 * ����XmlDataPath�������ýṹ�е�����,���ýṹ��Ϣ
	 * @param xdp
	 * @param valtype
	 * @param bnullable
	 * @param maxlen
	 * @param st
	 * @return ������õ����ӽṹ,�򷵻�·��ָ����ӽṹ,����ǳ�Ա,�򷵻�·��ָ���Ա�ĸ��ṹ
	 */
	public XmlDataStruct setByPath(XmlDataPath xdp,String valtype,boolean bnullable,int maxlen,StoreType st)
	{
//		if(xdp.isStruct())
//			throw new IllegalArgumentException("path ="+xdp.toString()+" is struct");
		
		PathItem[] pis = xdp.getPath() ;
		int num = pis.length ;
		XmlDataStruct curxds = this ;
		for(int i = 0 ; i < num - 1 ; i ++)
		{
			curxds = curxds.getOrCreateSubStruct(pis[i].getPathItemName());
			curxds.setIsArray(pis[i].isArray());
			//curxds.setIsNullable(pis[i].)
		}
		
		PathItem lpi = pis[num-1];
		if(xdp.isStruct())
		{
			curxds = curxds.getOrCreateSubStruct(lpi.getPathItemName());
			curxds.setIsArray(lpi.isArray());
			curxds.setIsNullable(bnullable);
		}
		else
		{
			curxds.setXmlDataMember(lpi.getPathItemName(), valtype, lpi.isArray(), bnullable, maxlen, st);
		}
		
		return curxds ;
	}
	
	public XmlDataStruct setSubStructByPath(XmlDataPath xdp,boolean bnullable)
	{
		if(!xdp.isStruct())
			throw new IllegalArgumentException("path="+xdp.toString()+" is not struct!");
		
		return setByPath(xdp,null,bnullable,-1,StoreType.Normal);
	}
	
	public void setXmlDataMemberByPath(PathItem[] path, String valtype,
			boolean barray, boolean bnullable, int maxlen,StoreType st)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length - 1; i++)
		{
			tmpxds = tmpxds.getOrCreateSubStruct(path[i].getPathItemName());
		}

		tmpxds.setXmlDataMember(path[path.length - 1].getPathItemName(), valtype, barray, bnullable,
				maxlen,st);
	}

	public XmlDataMember getXmlDataMemberByPath(String[] path)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length - 1; i++)
		{
			tmpxds = tmpxds.getSubStruct(path[i]);
			if (tmpxds == null)
				return null;
		}

		return tmpxds.getXmlDataMember(path[path.length - 1]);
	}
	
	public XmlDataMember getXmlDataMemberByPath(PathItem[] path)
	{
		if (path == null || path.length <= 0)
			throw new IllegalArgumentException("path cannot be null");

		XmlDataStruct tmpxds = this;
		for (int i = 0; i < path.length - 1; i++)
		{
			tmpxds = tmpxds.getSubStruct(path[i].getPathItemName());
			if (tmpxds == null)
				return null;
		}

		return tmpxds.getXmlDataMember(path[path.length - 1].getPathItemName());
	}
	
	public XmlDataMember getSingleXmlDataMemberByPath(String p)
	{
		XmlDataPath xdp = new XmlDataPath(p);
		if(xdp.isStruct())
			throw new IllegalArgumentException("invalid path becase it is a struct path");
		
		XmlDataMember si = getXmlDataMemberByPath(xdp.getPath());
		if(si==null)
			return null ;
		
		if(si.isArrayWithBelongTo())
			throw new IllegalArgumentException("path ="+p+" is array");
		
		return si ;
	}

	public boolean checkMatchStruct(XmlData xd, StringBuffer failedreson)
	{
		for (XmlDataMember si : pname2XmlDataMember.values())
		{
			// sixds.add(si.toXmlData());
			if (!si.isNullable())
			{
				if (si.isArray())
				{
					List v = xd.getParamValues(si.pname);
					if (v == null || v.size() <= 0)
					{
						failedreson.append(si.pname + " cannot be null!");
						return false;
					}
				}
				else
				{
					Object o = xd.getParamValue(si.pname);
					if (o == null)
					{
						failedreson.append(si.pname + " cannot be null!");
						return false;
					}
				}
			}

			if (si.getValType().equals(XmlVal.VAL_TYPE_STR))
			{
				if (si.isArray())
				{
					List v = xd.getParamValues(si.pname);
					if (v != null)
					{
						for (Object tmpo : v)
						{
							String tmps = (String) tmpo;
							if (tmps.length() > si.getMaxLen())
							{
								failedreson.append(si.pname
										+ " string len big then max len="
										+ si.getMaxLen());
								return false;
							}
						}
					}
				}
				else
				{
					String tmps = (String) xd.getParamValue(si.pname);
					if (tmps != null && tmps.length() > si.getMaxLen())
					{
						failedreson.append(si.pname
								+ " string len big then max len=" + si.getMaxLen());
						return false;
					}
				}
			}
		}

		for (XmlDataStruct xds : pname2SubST.values())
		{
			if (!xds.bNullable)
			{
				if (xds.bArray)
				{
					List<XmlData> lxds = xd.getSubDataArray(xds.name);
					if (lxds == null || lxds.size() <= 0)
					{
						failedreson.append(xds.name + " cannot be null!");
						return false;
					}

					for (XmlData tmpxd : lxds)
					{
						if (!checkMatchStruct(tmpxd, failedreson))
							return false;
					}
				}
				else
				{
					XmlData lxds = xd.getSubDataSingle(xds.name);
					if (lxds == null)
					{
						failedreson.append(xds.name + " cannot be null!");
						return false;
					}

					if (!checkMatchStruct(lxds, failedreson))
						return false;
				}
			}

		}

		return true;
	}

	/**
	 * �ж����㱾�ṹ��XmlData�Ƿ������ӦĿ��XmlDataStruct�ṹ
	 * 
	 * �����������жϲ�ͬ����������Ƿ���Ի���
	 * 
	 * @param tarxds
	 * @return
	 */
	public boolean checkFitFor(XmlDataStruct tarxds, StringBuffer failedreson)
	{
		if (tarxds == null)
			return true;

		// �ж�Ŀ��ͱ��ṹ�Ķ�Ӧ���Ƿ�����һ�£��������ڱ��ṹ���Ƿ����
		for (XmlDataMember tarsi : tarxds.pname2XmlDataMember.values())
		{
			XmlDataMember si = pname2XmlDataMember.get(tarsi.getName());
			if (si == null)
			{//���Դ��ԱΪnull������Ŀ�겻��Ϊnull������ƥ��
				if (failedreson != null)
					failedreson
							.append("no member with path=" + tarsi.getPath()+" that target needed!");
				return false;
			}

			if (!tarsi.isNullable() && si.isNullable())
			{
				if (failedreson != null)
					failedreson.append("tar member with path="
							+ tarsi.getPath() + " is not nullable");
				return false;
			}

			// �ж������Ƿ�һ��
			if (tarsi.isArray() != si.isArray())
			{
				if (failedreson != null)
					failedreson.append("tar member with path="
							+ tarsi.getPath() + " array=" + tarsi.isArray());
				return false;
			}

			// �ж�����
			if (!tarsi.getValType().equals(si.getValType()))
			{
				if (failedreson != null)
					failedreson.append("tar member with path="
							+ tarsi.getPath() + " type=" + tarsi.getValType());
				return false;
			}

			// ������ַ��������жϳ����Ƿ��ͻ
			if (si.getValType().equals(XmlVal.VAL_TYPE_STR))
			{
				if (tarsi.getMaxLen() < si.getMaxLen())
				{
					if (failedreson != null)
						failedreson.append("tar string member with path="
								+ tarsi.getPath() + " max len="
								+ tarsi.getMaxLen());
					return false;
				}
			}
		}

		for (XmlDataStruct tar_subxds : tarxds.pname2SubST.values())
		{
			XmlDataStruct subxds = pname2SubST.get(tar_subxds.getName());
			if (subxds == null)
			{
				if (failedreson != null)
					failedreson.append("no sub struct with name="
							+ tar_subxds.getName());
				return false;
			}

			if (!tar_subxds.isNullable() && subxds.isNullable())
			{
				if (failedreson != null)
					failedreson.append("tar sub struct with name="
							+ tar_subxds.getName() + " is not nullable");
				return false;
			}

			// �ж������Ƿ�һ��
			if (tar_subxds.isArray() != subxds.isArray())
			{
				if (failedreson != null)
					failedreson.append("tar sub struct with name="
							+ tar_subxds.getName() + " array="
							+ tar_subxds.isArray());
				return false;
			}

			if (!subxds.checkFitFor(tar_subxds, failedreson))
				return false;
		}

		return true;
	}

	/**
	 * �оٱ��ṹ�п��������������ṹ��������ӳ�������·��
	 * 
	 * ��������:
	 * 1,����ǿսṹ,�򷵻ؿ�
	 * 2,��һ���ӽڵ㶼������Ϊ����ӳ��·��(������Ա���ӽṹ)
	 * 3,�����һ�����ӽṹ�ǵ�ֵ�ӽṹ,����Եݹ�����Ѱ��·��
	 * @return
	 */
	public ArrayList<String> listCanJoinMatchPaths()
	{
		ArrayList<String> rets = new ArrayList<String>() ;
		
		listCanJoinMatchPaths(rets);
		return rets ;
	}
	
	private void listCanJoinMatchPaths(ArrayList<String> paths)
	{
		if(this.isEmptyStruct())
			return ;
		
		//�Լ���Ȼ���ж�,��϶�����(������/)
		paths.add(toFullPath());
		//�������г�Ա·��
		for(XmlDataMember xvd:pname2XmlDataMember.values())
		{
			paths.add(xvd.toFullPath()) ;
		}
		
		for(XmlDataStruct xds:this.pname2SubST.values())
		{
			if(!xds.isArray())
			{//����Ƿ�����,�����µݹ�
				xds.listCanJoinMatchPaths(paths);
			}
			else
			{
				paths.add(xds.toFullPath());
			}
		}
		
	}
	// private boolean checkMatchStruct(XmlDataStruct)

	public String toFullPath()
	{
		return XmlDataPath.createPath(this).toFullString();
	}
	
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();

		if (name != null)
			xd.setParamValue("name", name);
		xd.setParamValue("is_array", bArray);
		xd.setParamValue("nullable", bNullable);
		xd.setParamValue("is_sep_storage", bSepStorage);

		List<XmlData> sixds = xd.getOrCreateSubDataArray("struct_item");
		for (XmlDataMember si : pname2XmlDataMember.values())
		{
			sixds.add(si.toXmlData());
		}

		List<XmlData> subxds = xd.getOrCreateSubDataArray("sub_struct");
		for (XmlDataStruct xds : pname2SubST.values())
		{
			subxds.add(xds.toXmlData());
		}

		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		name = xd.getParamValueStr("name");
		bArray = xd.getParamValueBool("is_array", false);
		bNullable = xd.getParamValueBool("nullable", true);
		bSepStorage = xd.getParamValueBool("is_sep_storage", false);

		List<XmlData> sixds = xd.getSubDataArray("struct_item");
		if (sixds != null)
		{
			for (XmlData tmpxd : sixds)
			{
				XmlDataMember si = new XmlDataMember();
				si.fromXmlData(tmpxd);
				si.belongTo = this;
				pname2XmlDataMember.put(si.pname, si);
			}
		}

		List<XmlData> subxds = xd.getSubDataArray("sub_struct");
		if (subxds != null)
		{
			for (XmlData tmpxd : subxds)
			{
				XmlDataStruct xds = new XmlDataStruct();
				xds.fromXmlData(tmpxd);
				xds.parent = this;
				pname2SubST.put(xds.name, xds);
			}
		}
	}
}

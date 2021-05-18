package org.iottree.core.util.xmldata;

import java.util.ArrayList;
import java.util.*;

public class XmlDataPath
{
	public static class PathItem
	{
		// 路径名称
		private String pathName = null;

		// 是否是多值路径
		private boolean bArray = false;
		
		private boolean bNullable = true ;

		// 如果是Array,才起作用
		// 如果<0,则表示没有指定Array中具体的哪一个
		private int arrayIdx = -1;

		public PathItem(PathItem pi)
		{
			pathName = pi.pathName;
			bArray = pi.bArray;
			arrayIdx = pi.arrayIdx;
		}
		
		public PathItem(XmlDataStruct xds)
		{
			pathName = xds.getName();
			bArray = xds.isArray();
			bNullable = xds.isNullable() ;
		}
		
		public PathItem(XmlDataMember xvs)
		{
			pathName = xvs.getName();
			bArray = xvs.isArray();
			bNullable = xvs.isNullable() ;
		}
		/**
		 * 路径项
		 * 
		 * @param strpi
		 */
		public PathItem(String strpi)
		{
			if(strpi.endsWith("*"))
			{
				bNullable = false;
				strpi = strpi.substring(0,strpi.length()-1);
			}
			
			int si = strpi.indexOf('[');
			if (si > 0)
			{
				bArray = true;
				pathName = strpi.substring(0, si);
				int ei = strpi.indexOf(']', si + 1);
				String stridx = strpi.substring(si + 1, ei).trim();
				if (!stridx.equals(""))
					arrayIdx = Integer.parseInt(stridx);
			}
			else
			{
				pathName = strpi;
			}
		}

		public PathItem(String pathname, int arrayidx,boolean bnullable)
		{
			pathName = pathname;
			bArray = true;
			arrayIdx = arrayidx;
			if (arrayIdx < 0)
				arrayIdx = -1;
			
			bNullable = bnullable ;
		}

		public String getPathItemName()
		{
			return pathName;
		}

		public boolean isArray()
		{
			return bArray;
		}
		
		public boolean isNullable()
		{
			return bNullable ;
		}

		public int getArrayIdx()
		{
			return arrayIdx;
		}

		public boolean equals(Object o)
		{
			if (!(o instanceof PathItem))
			{
				return false;
			}

			PathItem opi = (PathItem) o;
			if (!pathName.equals(opi.pathName))
				return false;

			if (bArray != opi.bArray)
				return false;

			if (bArray)
			{
				if (arrayIdx != opi.arrayIdx)
					return false;
			}

			return true;
		}

		public String toString()
		{
			if (bArray)
			{
				if (arrayIdx < 0)
					return pathName + "[]";
				else
					return pathName + "[" + arrayIdx + "]";
			}
			else
			{
				return pathName;
			}
		}
		
		public String toFullString()
		{
			String tmps = null ;
			if (bArray)
			{
				if (arrayIdx < 0)
					tmps = pathName + "[]";
				else
					tmps = pathName + "[" + arrayIdx + "]";
			}
			else
			{
				tmps = pathName;
			}
			
			if(!bNullable)
				tmps += "*" ;
			
			return tmps ;
		}
	}

	public static String ArrayPath2StrPath(String[] path, boolean bstruct)
	{
		return ArrayPath2StrPath(path, bstruct, false);
	}

	public static String ArrayPath2StrPath(String[] path, boolean bstruct,
			boolean brelative)
	{
		if(path==null||path.length<=0)
			return "/";
		
		StringBuffer tmps = new StringBuffer();
		if (!brelative)
			tmps.append('/');

		if (path == null)
		{
			return tmps.toString();
		}

		tmps.append(path[0]);
		for (int i = 1; i < path.length; i++)
		{
			tmps.append('/').append(path[i]);
		}
		if (bstruct)
			tmps.append('/');

		return tmps.toString();
	}

	public static String ArrayPath2StrPath(PathItem[] path, boolean bstruct,
			boolean brelative)
	{
		StringBuffer sb = ArrayPath2StringBufferPath(path,bstruct,brelative);
		return sb.toString();
	}
	
	public static StringBuffer ArrayPath2StringBufferPath(PathItem[] path, boolean bstruct,
			boolean brelative)
	{
		StringBuffer tmps = new StringBuffer();
		
		if(path==null||path.length<=0)
			return tmps.append("/");
		
		
		if (!brelative)
			tmps.append('/');

		if (path == null)
		{
			return tmps;
		}

		tmps.append(path[0].toString());
		for (int i = 1; i < path.length; i++)
		{
			tmps.append('/').append(path[i].toString());
		}
		if (bstruct)
		{
			tmps.append('/');
		}
		
		return tmps;
	}

	public static String ArrayPath2ColName(String[] path)
	{
		String tmps = path[0];
		for (int i = 1; i < path.length; i++)
		{
			tmps += ("_" + path[i]);
		}
		return tmps;
	}

	public static String ArrayPath2ColName(PathItem[] path)
	{
		String tmps = path[0].getPathItemName();
		for (int i = 1; i < path.length; i++)
		{
			tmps += ("_" + path[i].getPathItemName());
		}
		return tmps;
	}

	private String xmlValType = null;
	
	//private boolean bNullable = true ;

	private boolean bStruct = false;
	private boolean bRoot = false;

	private boolean bRelative = false;

	private ArrayList<PathItem> pathVal = new ArrayList<PathItem>();
	
	public XmlDataPath()
	{}

	public XmlDataPath(String[] ps, boolean bstruct)
	{
		this(ps, bstruct, false);
	}

	public XmlDataPath(String[] ps, boolean bstruct, boolean brelative)
	{
		if (ps == null || ps.length <= 0)
			throw new IllegalArgumentException(
					"invalid path array,it cannot be null or 0 len");

		for (int i = 0; i < ps.length; i++)
		{
			PathItem pi = new PathItem(ps[i]);
			pathVal.add(pi);
			if (i < ps.length - 1 && pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path cannot be array with no idx set!");
			}
		}
		bStruct = bstruct;
		bRelative = brelative;
	}

	public XmlDataPath(PathItem[] ps, boolean bstruct, boolean brelative)
	{
		this(ps,bstruct,brelative,null,true);
	}
	
	public XmlDataPath(PathItem[] ps,boolean bstruct, boolean brelative,
			String xmlvaltype,boolean bnullable)
	{
		if (ps == null || ps.length <= 0)
			throw new IllegalArgumentException(
					"invalid path array,it cannot be null or 0 len");

		//pathVal = new ArrPathItem[ps.length];
		for (int i = 0; i < ps.length; i++)
		{
			PathItem pi = new PathItem(ps[i]);
			pathVal.add(pi);
			if (i < ps.length - 1 && pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path cannot be array with no idx set!");
			}
			
			if(i==ps.length-1)
			{
				pi.bNullable = bnullable ;
			}
		}
		bStruct = bstruct;
		bRelative = brelative;
		xmlValType = xmlvaltype ;
		//bNullable = bnullable ;
	}
	
	/**
	 * 做为XmlDataStruct生成路径的其中一项提供支持
	 */
	public static XmlDataPath createPath(XmlDataStruct xds)
	{
		if(xds==null)
			return null ;
		
		XmlDataPath xdp = new XmlDataPath();
		xdp.bStruct = true ;
		if(xds.getParent()==null)
		{
			xdp.bRoot = true ;
			
			return xdp;
		}
		
		xdp.pathVal = createPathItems(xds);
		return xdp;
	}
	
	private static ArrayList<PathItem> createPathItems(XmlDataStruct xds)
	{
		ArrayList<PathItem> pis = new ArrayList<PathItem>() ;
		
		do
		{
			if(xds.getParent()==null)
			{//root
				return pis ;
			}
			else
			{
				PathItem pi = new PathItem(xds);
				pis.add(0, pi);
			}
		}
		while((xds=xds.getParent())!=null);
		
		return pis ;
	}
	
	public static XmlDataPath createPath(XmlDataMember xvd)
	{
		if(xvd==null)
			return null ;
		
		XmlDataPath xdp = new XmlDataPath();
		xdp.bStruct = false;
		xdp.pathVal = createPathItems(xvd.getBelongTo());
		
		PathItem pi = new PathItem(xvd);
		xdp.pathVal.add(pi);
		
		xdp.xmlValType = xvd.getValType();
		return xdp ;
	}
	
	public static XmlDataPath createPath(List<PathItem> pis,boolean bstruct, boolean brelative,
			String xmlvaltype)//,boolean bnullable)
	{
		XmlDataPath xdp = new XmlDataPath();
		if(pis==null||pis.size()<=0)
			return xdp ;
		int c = pis.size();
		//pathVal = new ArrPathItem[ps.length];
		for (int i = 0; i < c; i++)
		{
			PathItem pi = new PathItem(pis.get(i));
			xdp.pathVal.add(pi);
			if (i < c - 1 && pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path cannot be array with no idx set!");
			}
			
//			if(i==c-1)
//				pi.bNullable = bnullable;
		}
		xdp.bStruct = bstruct;
		xdp.bRelative = brelative;
		xdp.xmlValType = xmlvaltype ;
		//xdp.bNullable = bnullable ;
		return xdp;
	}

	public XmlDataPath(String path)
	{
		if(path==null||path.equals(""))
			throw new IllegalArgumentException("path cannot be null or empty!");
		
		if("/".equals(path))
		{
			bRoot = true ;
			return ;
		}
		
		bStruct = path.endsWith("/");
		bRelative = !path.startsWith("/");
		
		// if(path==null||path.equals(path))
		StringTokenizer tmpst = new StringTokenizer(path, "/");
		//PathItem[] tmps = new PathItem[tmpst.countTokens()];
		int c = tmpst.countTokens();
		for (int i = 0; i < c - 1; i++)
		{
			PathItem pi = new PathItem(tmpst.nextToken());
			if (pi.isArray())
			{
				if (pi.getArrayIdx() < 0)
					throw new IllegalArgumentException(
							"parent path cannot be array with no idx set!");
			}
			
			pathVal.add(pi);
		}

		String lastp = tmpst.nextToken();
		boolean barr = false;
		if(!bStruct)
		{
			int tp = lastp.indexOf(':');
			if (tp > 0)
			{
				xmlValType = lastp.substring(tp + 1).trim();
				if(xmlValType.endsWith("*"))
				{
					//bNullable = false;
					xmlValType = xmlValType.substring(0,xmlValType.length()-1);
				}
				else if(xmlValType.endsWith("[]"))
				{
					xmlValType = xmlValType.substring(0,xmlValType.length()-2);
					barr = true ;
				}
				lastp = lastp.substring(0, tp).trim();
			}
		}
		
		PathItem pi = new PathItem(lastp);
		if(barr)
		{
			pi.bArray = barr ;
			pi.arrayIdx = -1 ;
		}
		pathVal.add(pi);
		
	}

	public PathItem[] getPath()
	{
		PathItem[] rets = new PathItem[pathVal.size()];
		pathVal.toArray(rets);
		return rets;
	}
	
	public PathItem lastPathItem()
	{
		if(pathVal==null||pathVal.size()<=0)
			return null ;
		
		return pathVal.get(pathVal.size()-1);
	}

	public String getXmlValType()
	{
		return xmlValType;
	}
	
	public boolean isNullable()
	{
		if(this.pathVal==null||pathVal.size()<=0)
			return true ;
		
		return pathVal.get(pathVal.size()-1).bNullable ;
		//return bNullable;
	}

	public boolean isStruct()
	{
		if(pathVal==null||pathVal.size()<=0)
			return true ;
		
		return bStruct;
	}

	public boolean isRoot()
	{
		return bRoot ;
	}
	/**
	 * 判断路径指向的内容,最终是个数组
	 * 
	 * @return
	 */
	public boolean isValueArray()
	{
		PathItem lastpi = pathVal.get(pathVal.size() - 1);
		// if(lastpi.is)
		if (!lastpi.isArray())
			return false;

		return lastpi.getArrayIdx() < 0;
	}

	public boolean isRelative()
	{
		if(pathVal==null||pathVal.size()<=0)
			return false;
		
		return bRelative;
	}

	public String[] getPathNames()
	{
		if (pathVal == null)
			return null;

		String[] rets = new String[pathVal.size()];
		for (int i = 0; i < rets.length; i++)
		{
			rets[i] = pathVal.get(i).getPathItemName();
		}
		return rets;
	}

	public XmlDataPath getSubPath(String subname, boolean bstruct)
	{
		return getSubPath(subname, bstruct, false);
	}

	public XmlDataPath getSubPath(String subname, boolean bstruct,
			boolean brelative)
	{
		return getSubPath(subname, bstruct,
				brelative,
				null,true);
	}
	
	public XmlDataPath getSubPath(String subname, boolean bstruct,
			boolean brelative,
			String xmlvaltype,boolean bnullable)
	{
		ArrayList<PathItem> rets = new ArrayList<PathItem>(pathVal.size() + 1);
		rets.addAll(pathVal);
		
		PathItem pi = new PathItem(subname, -1,bnullable);
		rets.add(pi);
		
		return XmlDataPath.createPath(rets, bstruct, brelative,
				xmlvaltype);//,bnullable);
	}

	public XmlDataPath getSubArrayPath(String subname, int arrayidx,
			boolean bstruct)
	{
		return getSubArrayPath(subname, arrayidx, bstruct, false);
	}

	public XmlDataPath getSubArrayPath(String subname, int arrayidx,
			boolean bstruct, boolean brelative)
	{
		ArrayList<PathItem> rets = new ArrayList<PathItem>(pathVal.size() + 1);
		rets.addAll(pathVal);
		rets.add(new PathItem(subname, arrayidx,true));
		return XmlDataPath.createPath(rets, bstruct, brelative,null);//,true);
	}
	
//	public XmlDataPath getParentPath()
//	{
//		if(this.pathVal==null||pathVal.length<=0)
//			return null ;
//		
//		PathItem[] rets = new PathItem[pathVal.length - 1];
//		System.arraycopy(pathVal, 0, rets, 0, pathVal.length-1);
//		return new XmlDataPath(rets, true, bRelative);
//	}

	public String toString()
	{
		return ArrayPath2StrPath(this.getPath(), bStruct, bRelative);
	}
	
	public String toFullString()
	{
		if(pathVal==null||pathVal.size()<=0)
			return "/";
		
StringBuffer tmps = new StringBuffer();

		int c = pathVal.size();
		
		
		if (!bRelative)
			tmps.append('/');

		tmps.append(pathVal.get(0).toFullString());
		for (int i = 1; i < c; i++)
		{
			tmps.append('/').append(pathVal.get(i).toFullString());
		}
		
		if (bStruct)
		{
			tmps.append('/');
			return tmps.toString();
		}
		
		if(xmlValType!=null&&!xmlValType.equals(""))
		{
			tmps.append(":").append(xmlValType) ;
		}			
		
		return tmps.toString() ;
	}

	public String toColumnName()
	{
		return ArrayPath2ColName(getPath());
	}
	
	/**
	 * 把本路径加上一个相对性路径,返回合并后的路径
	 * @param rxdp
	 * @return
	 */
	public XmlDataPath appendRelativePath(XmlDataPath rxdp)
	{
		if(!this.isStruct())
			throw new IllegalArgumentException("path="+this.toString()+" is not struct which cannot append sub path");
		
		if(!rxdp.isRelative())
			throw new IllegalArgumentException("path="+rxdp+"is not relative");
		
		ArrayList<PathItem> pis = new ArrayList<PathItem>();
		pis.addAll(this.pathVal);
		pis.addAll(rxdp.pathVal);
		
		return createPath(pis,rxdp.bStruct,this.bRelative,rxdp.xmlValType);//,rxdp.bNullable);
	}
	
	public XmlDataPath appendRelativePath(XmlDataPath rxdp,int idx)
	{
		if(!this.isStruct())
			throw new IllegalArgumentException("path="+this.toString()+" is not struct which cannot append sub path");
		
		if(!rxdp.isRelative())
			throw new IllegalArgumentException("path="+rxdp+"is not relative");
		
		PathItem pi = rxdp.lastPathItem();
		if(!pi.isArray())
			throw new IllegalArgumentException("the last one path item is not array!");
		
		ArrayList<PathItem> pis = new ArrayList<PathItem>();
		pis.addAll(this.pathVal);
		pis.addAll(rxdp.pathVal);
		
		XmlDataPath ret = createPath(pis,rxdp.bStruct,this.bRelative,rxdp.xmlValType);//,rxdp.bNullable);
		if(idx<0)
			idx = -1 ;
		ret.lastPathItem().arrayIdx = idx ;
		return ret ;
	}
	
	/**
	 * 得到数组路径的第idx个路径
	 * @param idx
	 * @return
	 */
	public XmlDataPath getArrayIdxPath(int idx)
	{
		PathItem pi = lastPathItem();
		if(pi==null)
			throw new IllegalArgumentException("root path cannot has index");
		
		if(!pi.isArray())
			throw new IllegalArgumentException("path="+this.toString()+" is not array!");
		
		XmlDataPath ret = createPath(this.pathVal,bStruct,bRelative,xmlValType);//,bNullable);
		if(idx<0)
			idx = -1 ;
		ret.lastPathItem().arrayIdx = idx ;
		return ret ;
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof XmlDataPath))
			return false;

		XmlDataPath dxp = (XmlDataPath) o;
		if (pathVal == null || pathVal.size() <= 0)
		{
			if(dxp.pathVal==null||dxp.pathVal.size()<=0)
				return true ;//都是根路径
			else
				return false;
		}
			
		if (bStruct != dxp.bStruct)
			return false;

//		if (dxp.pathVal == null || dxp.pathVal.size() <= 0)
//			return false;

		int c = pathVal.size();
		if (c != dxp.pathVal.size())
			return false;

		
		for (int i = 0; i < c; i++)
		{
			if (!pathVal.get(i).equals(dxp.pathVal.get(i)))
				return false;
		}

		if (xmlValType == null || xmlValType.equals(""))
		{
			if (dxp.xmlValType != null && !dxp.xmlValType.equals(""))
				return false;
		}
		else if (!xmlValType.equals(dxp.xmlValType))
			return false;

		return true;
	}
}

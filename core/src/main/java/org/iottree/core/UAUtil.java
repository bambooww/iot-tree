package org.iottree.core;

import java.util.LinkedList;

import org.iottree.core.util.Convert;

public class UAUtil
{
	public static void assertUAName(String n)
	{
		if (n == null || n.equals(""))
		{
			throw new IllegalArgumentException("name cannot be null or empty") ;
		}

		char c1 = n.charAt(0);
		boolean bc1 = (c1 >= 'a' && c1 <= 'z') || (c1 >= 'A' && c1 <= 'Z') || c1 == '_';
		if (!bc1)
		{
			throw new IllegalArgumentException("name first char must be a-z A-Z");
		}

		int s = n.length();
		for (int i = 1; i < s; i++)
		{
			char c = n.charAt(i);
			boolean bc = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
			if (!bc)
			{
				throw new IllegalArgumentException("invalid char =" + c);
			}
		}
	}
	
	
	/**
	 * 1)remove not number char in str
	 * 2)
	 * @param str
	 * @param dec_num
	 * @return
	 */
	public static String transAddrNumByGuess(String str,int dec_num)
	{
		if(str==null||"".contentEquals(str))
			return "" ;
		StringBuilder sb = new StringBuilder() ;
		for(int i = 0 ; i < dec_num ; i ++)
			sb.append('0') ;
		str.chars().filter((int i)->i>='0'&&i<='9').forEach(c->sb.append(c));
		int len = sb.length() - dec_num ;
		return sb.substring(len) ;
	}
	
	
	
	
	/**
	 * 
	 * @param strv
	 * @param minv
	 * @param maxv
	 * @param failedr
	 * @return
	 */
	public static boolean chkPropValInt(String strv,Long minv,Long maxv,Boolean notempty,
			StringBuilder failedr)
	{
		if(strv==null||(strv=strv.trim()).equals(""))
		{
			if(notempty!=null&&notempty)
			{
				failedr.append("is empty") ;
				return false;
			}
			return true ;
		}
		try
		{
			long lv = Long.parseLong(strv) ;
			if(minv!=null&&lv<minv)
			{
				failedr.append("value must > "+minv) ;
				return false;
			}
			if(maxv!=null&&lv>maxv)
			{
				failedr.append("value must < "+maxv) ;
				return false;
			}
			return true ;
		}
		catch(Exception e)
		{
			failedr.append(" invalid int number") ;
			return false;
		}
	}
	
	/**
	 * support for node with different path ;
	 * @param path
	 * @return
	 */
	public static UANode findNodeByPath(String path)
	{
		if(Convert.isNullOrTrimEmpty(path))
			return null ;
		if(path.indexOf('-')>0)
		{
			return DevManager.getInstance().findNodeByPath(path) ;
		}
		return UAManager.getInstance().findNodeByPath(path) ;
	}
	
	public static UAHmi findHmiByPath(String path)
	{
		UANode n = findNodeByPath(path) ;
		if(n==null)
			return null ;
		if(!(n instanceof UAHmi))
			throw new IllegalArgumentException("path is not hmi node") ;
		return (UAHmi)n ;
	}
	public static boolean isDevDefPath(String path)
	{
		return path.indexOf('-')>0 ;
	}
	
	
	/**
	 * 
	 * @param ownerid  rep_xxx   devdef_xx
	 * @param hmiid
	 * @return
	 */
	public static UAHmi findHmi(String ownerid,String hmiid)
	{
		if(ownerid.startsWith("rep_"))
		{
			UARep rep = UAManager.getInstance().getRepById(ownerid.substring(4)) ;
			if(rep==null)
				return null ;
			return rep.findHmiById(hmiid) ;
		}
		else if(ownerid.startsWith("devdef_"))
		{
			//String defid = ownerid.substring(7) ;
			return (UAHmi)DevManager.getInstance().findNodeById(hmiid) ;
		}
		else
		{
			return null ;
		}
	}
}

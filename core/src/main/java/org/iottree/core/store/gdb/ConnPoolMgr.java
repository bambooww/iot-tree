package org.iottree.core.store.gdb;

import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Element;
import org.iottree.core.store.gdb.conf.Gdb;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.IConnPool;

public class ConnPoolMgr
{
	static IConnPool defaultConnPool = null ;
	
	/**
	 * 缺省的Element元素，用以为多域的情况创建新的连接池
	 */
	static Element defaultPoolEle = null ;
	
	
	static String urlDomainPrefix = null ;
	static String urlDomainSuffix = null ;
	/**
	 * 在多域情况下，域id到确实链接池的
	 */
	static HashMap<Integer,IConnPool> domain2Pool = new HashMap<Integer,IConnPool>() ; 
	
	static HashMap<String, IConnPool> dbname2pool = new HashMap<String, IConnPool>();
	
//	/**
//	 * 域到ip地址的映射
//	 * 此对象支持分布式的情况下进行使用
//	 */
//	private static HashMap<Integer,String> domain2ip = new HashMap<Integer,String>() ;
	
//	public static IConnPool getDefaultConnPool()
//	{
//		String dbname = GDB.tlDBName.get();
//		if(dbname==null||"".equals(dbname))
//		{//
//			return defaultConnPool ;
//		}
//		else
//		{
//			return dbname2pool.get(dbname) ;
//		}
//		//return defaultConnPool;
//	}
	
//	/**
//	 * 分布式情况下，设置某个域对应的IP
//	 * @param domain
//	 * @param ip
//	 */
//	public static void setDomain2IP(int domain,String ip)
//	{
//		domain2ip.put(domain, ip) ;
//		domain2Pool.remove(domain) ;
//	}
	
	
	public static IConnPool getConnPool(String dbname)
	{
		if(dbname==null||dbname.equals(""))
			return null;//return getDefaultConnPool() ;
		
		IConnPool cp = dbname2pool.get(dbname);
		if (cp != null)
			return cp;

		return defaultConnPool;
	}
	
	public static Set<String> getConnPoolNames()
	{
		return dbname2pool.keySet() ;
	}
	
	public static IConnPool getConnPool(Gdb g)
	{
		return getConnPoolGdb(g) ;
	}
	
	public static IConnPool getConnPoolGdb(Gdb g)
	{
		if(g!=null)
		{
			IConnPool cp = dbname2pool.get(g.usingDBName);
			if (cp != null && cp!=defaultConnPool)
				return cp;
		}

		return null ;
	}
}

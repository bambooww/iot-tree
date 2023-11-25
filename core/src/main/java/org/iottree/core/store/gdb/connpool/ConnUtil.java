package org.iottree.core.store.gdb.connpool;

import java.sql.*;
import java.util.*;
/**
 * ����jdbc���ṩһЩͨ�õĺ���֧��
 * @author zzj
 *
 */
public class ConnUtil
{
	public static String[] SUP_DRV_NAMES = new String[] {"mysql","sqlserver"} ;
	//public static String[] SUP_DBDRVS= new String[] {"com.mysql.jdbc.Driver","com.microsoft.jdbc.sqlserver.SQLServerDriver"} ;
	public static String[] SUP_DBDRVS= new String[] {"com.mysql.jdbc.Driver","com.microsoft.sqlserver.jdbc.SQLServerDriver"} ;
	
	private static HashMap<String,String> drvN2C = new HashMap<String,String>() ;
	
	static
	{
		for(int i = 0 ; i < SUP_DRV_NAMES.length ; i ++)
		{
			drvN2C.put(SUP_DRV_NAMES[i], SUP_DBDRVS[i]);
		}
		
	}
	
//	String dbDrvName = null ;
//	
//	String dbUrl = null ;
//	
//	String dbUser ;
//	
//	String dbPsw = null ;
////	public static String getConnUrl(String dbtype,String host,int port)
////	{
////		if("mysql".equals(dbtype))
////		{
////			
////		}
////	}
//	String sqlStr = null ;
	
	
	
	public static Connection makeNewConn(String dbdrv,String dburl,String dbuser,String dbpsw) throws Exception
	{
		try
		{
			DriverManager.registerDriver((Driver)Class.forName(drvN2C.get(dbdrv))
					.newInstance());
			Connection conn = DriverManager.getConnection(dburl, dbuser,dbpsw);
			return conn ;
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SQLException("Can't find class for driver name: " + dbdrv);
		}
		catch (SQLException sqle)
		{
			// һ���������Ӵ���,˵�����ݿ������һ��ʱ��֮�ڲ�������,�������ʱ��֮��,
			// û�б�Ҫ������
			
			throw sqle;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			throw new SQLException("some error newConnection = " + e.toString());
		}
	}
}
